package com.imooc.controller;

import com.imooc.service.StuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author liangwq
 * @date 2020/11/29
 */
@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;


    /**
     * 微信支付成功 -> 支付中心 -> 天天吃货平台
     *                         -> 回调通知的URL
     */
    public static final String payReturnUrl="http://dyd76y.natappfree.cc/orders/notifyMerChantOrderPaid";

    /**
     * 支付中心的调用地址(生产环境)
     */
    public static final String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";
//    public static final String paymentUrl = "http://localhost:8089/payment/createMerchantOrder";
}
