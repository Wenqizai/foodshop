package com.imooc.interceptor;

import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author liangwq
 * @date 2021/3/1
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求, 在访问controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // false: 请求被拦截, 被驳回, 验证出现问题
        // true: 请求在经过验证校验以后, 是ok的, 是可以放行的
        System.out.println("进入拦截器, 被拦截...");
        String userToken = request.getHeader("headerUserToken");
        String userId = request.getHeader("headerUserId");
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)) {
                returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
                return false;
            } else {
                if (!uniqueToken.equals(uniqueToken)) {
                    returnErrorResponse(response, IMOOCJSONResult.errorMsg("账号在异地登录..."));
                    return false;
                }
            }
        } else {
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }
        return true;
    }

    /**
     * 拦截请求, 在访问controller之后, 渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 拦截请求, 渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

    }

    /**
     * 返回错误信息方法
     * @param response
     * @param result
     */
    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
