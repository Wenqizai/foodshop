package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrderService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liangwq
 * @date 2020/11/29
 */
@Api(value = "订单相关", tags = {"订单相关相关的api接口"})
@RequestMapping("orders")
@RestController
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitBO submitBO, HttpServletRequest request,
                                  HttpServletResponse response) {
        if (submitBO.getPayMethod() != PayMethod.WEIXIN.type
                && submitBO.getPayMethod() != PayMethod.ALIPAY.type) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }
        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(submitBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后, 移除购物车中已结算(已提交)的商品
        /**
         * 1001
         * 1002 -> 用户购买
         * 2002 -> 用户购买
         * 1003
         * 1004
         */
        // TODO 整合redis之后, 完善购物车中的已结算商品清除, 并且同步到前端的cookie
//        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);

        // 3. 向支付中心发送当前订单, 用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);
        // 此项是为了方便测试支付, 所以所有的支付金额都统一设置为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("imoocUserId", "imooc");
        headers.set("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<IMOOCJSONResult> responseEntity
                = restTemplate.postForEntity(paymentUrl, entity, IMOOCJSONResult.class);
        IMOOCJSONResult paymentResult = responseEntity.getBody();

        if (paymentResult.getStatus() != 200) {
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败, 请联系管理员!");
        }
        return IMOOCJSONResult.ok(orderId);
    }

    @PostMapping("/notifyMerChantOrderPaid")
    public Integer notifyMerChantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("/getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }
}
