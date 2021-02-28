package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "注册登录", tags = "用于注册和登录的相关接口")
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空!");
        }
        // 2. 查找注册的用户名是否存在
        boolean isExist = userService.queryUserIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        // 3. 请求成功, 用户名没有重复
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBo,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String username = userBo.getUsername();
        String password = userBo.getPassword();
        String confirmPassword = userBo.getConfirmPassword();
        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空!");
        }
        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUserIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度不能少于6位");
        }
        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        // 4. 实现注册
        Users userResult = userService.createUsers(userBo);
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 生成用户token, 存入Redis会话中
        // TODO 同步购物车数据
        synchShopcartData(userResult.getId(), request, response);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBo,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String username = userBo.getUsername();
        String password = userBo.getPassword();
        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空!");
        }
        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userResult == null) {
            return IMOOCJSONResult.errorMsg("用户名或密码错误");
        }
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 生成用户token, 存入Redis会话中
        // TODO 同步购物车数据
        synchShopcartData(userResult.getId(), request, response);
        return IMOOCJSONResult.ok(userResult);
    }

    /**
     * 注册登录成功后, 同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {
        /**
         * 1. Redis中为空:
         *      cookie中购物车为空, 那么这时候不做任何处理
         *      cookie中购物车不为空, 此时直接放入redis中
         *
         * 2. Redis中有数据:
         *      如果cookie购物车为空, 则将Redis中数据覆盖到cookie中
         *      如果cookie购物车不为空, 如果cookie中的的某个商品在redis中存在, 则以cookie为主,
         *      删除redis中数据, 把cookie中的数据覆盖redis.(京东)
         * 3. 同步到Redis之后，需要将最新的数据覆盖到cookie中，保证本地购物车中数据为最新。
         */
        // 从Redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        // 从Cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);
        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis为空, cookie不为空, 直接把cookie中的数据放入Redis中
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis中不为空, cookie不为空, 合并cookie和redis中购物车的商品数据(同一商品则cookie为主)
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                /**
                 * 1. 已经存在, 把cookie中对应的数量, 覆盖redis(参看京东)
                 * 2. 该项商品标记为待删除, 统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */
                List<ShopcartBO> shopcartBOListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartBOListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);
                // 存放待删除集合
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();
                for (ShopcartBO redisShopcart : shopcartBOListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();
                    for (ShopcartBO cookieShopcart : shopcartBOListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();
                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量, 不累加, 参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表, 用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }
                // 从现有cookie中删除对应的覆盖过的数据
                shopcartBOListCookie.removeAll(pendingDeleteList);
                // 合并两个list
                shopcartBOListRedis.addAll(shopcartBOListCookie);
                // 更新到Redis和cookie
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartBOListRedis));
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartBOListRedis), true);
            } else {
                // redis不为空, cookie为空, 直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }
        }

    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // 用户退出登录, 需要清空购物车
        // TODO 分布式会话中需要清除用户数据
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);
        return IMOOCJSONResult.ok();
    }

    /**
     * 取消敏感信息展示
     * @param userResult
     * @return
     */
    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setRealname(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setBirthday(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        return userResult;
    }

}
