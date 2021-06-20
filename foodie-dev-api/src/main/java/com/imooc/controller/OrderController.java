package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrderService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private RedissonClient redissonClient;

    @ApiOperation(value = "获取订单token", notes = "获取订单token", httpMethod = "POST")
    @PostMapping("/getOrderToken")
    public IMOOCJSONResult getOrderToken(HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        redisOperator.set("ORDER_TOKEN:" + request.getSession().getId(), token, 10 * 60);
        return IMOOCJSONResult.ok(token);
    }

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitBO submitBO, HttpServletRequest request,
                                  HttpServletResponse response) {
        // 幂等检验
        String orderTokenKey = "ORDER_TOKEN:" + request.getSession().getId();
        String lockKey = "LOCK_KEY:" + request.getSession().getId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            String orderToken = redisOperator.get(orderTokenKey);
            if (StringUtils.isBlank(orderToken)) {
                throw new RuntimeException("orderToken不存在");
            }
            if (!orderToken.equals(submitBO.getToken())) {
                throw new RuntimeException("orderToken不正确");
            }
            // token检验正确, 需将token删除
            redisOperator.del(orderTokenKey);
        } finally {
            lock.unlock();
        }

        if (!submitBO.getPayMethod().equals(PayMethod.WEIXIN.type)
                && !submitBO.getPayMethod().equals(PayMethod.ALIPAY.type)) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return IMOOCJSONResult.errorMsg("购物车数据不正确!");
        }

        // redis中已经有购物车
        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(submitBO, shopcartList);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后, 移除购物车中已结算(已提交)的商品
        /**
         * 1001
         * 1002 -> 用户购买
         * 2002 -> 用户购买
         * 1003
         * 1004
         */
        // 清理覆盖现有的Redis中的购物车数据
        shopcartList.removeAll(orderVO.getToBeRemovedShopcartList());
        redisOperator.set(FOODIE_SHOPCART + ":" + submitBO.getUserId(), JsonUtils.objectToJson(shopcartList));
        // 整合redis之后, 完善购物车中的已结算商品清除, 并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartList), true);

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
