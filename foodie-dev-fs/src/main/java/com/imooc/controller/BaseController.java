package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UserVO;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * @author liangwq
 * @date 2020/11/29
 */
@Controller
public class BaseController {
    /**
     * 购物车
     */
    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;
    /**
     * 用户token
     */
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private MyOrdersService myOrdersService;
    @Autowired
    private RedisOperator redisOperator;


    /**
     * 微信支付成功 -> 支付中心 -> 天天吃货平台
     *                         -> 回调通知的URL
     */
    public static final String payReturnUrl="http://wtz3w9.natappfree.cc/orders/notifyMerChantOrderPaid";

    /**
     * 支付中心的调用地址(生产环境)
     */
    public static final String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";
//    public static final String paymentUrl = "http://localhost:8089/payment/createMerchantOrder";

    /**
     * 头像上传路径(开发环境)
     */
    public static final String IMAGE_USER_FACE_LOCATION = "C:\\@D\\-Development\\Study\\Codes\\java-idea\\Learning" +
            "\\Project\\foodie-dev-git\\faces";


    /**
     * 用于验证用户和订单是否有关联关系, 避免非法用户调用
     *
     * @param userId
     * @param orderId
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders orders = myOrdersService.queryMyOrder(userId, orderId);
        if (orders == null) {
            return IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(orders);
    }

    /**
     * 实现用户的Redis会话
     * @param userResult
     * @return
     */
    public UserVO conventUserVO(Users userResult) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), uniqueToken);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult, userVO);
        userVO.setUserUniqueToken(uniqueToken);
        return userVO;
    }
}
