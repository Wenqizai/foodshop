package com.imooc.config;

import com.imooc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单相关的定时任务
 * @author liangwq
 * @date 2021/1/9
 */

@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    /**
     * 自动关闭未关闭的订单
     */
    @Scheduled(cron = "0 0 0/8 * * ?")
    public void autoCloseOrder() {
        try {
            orderService.closeOrder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
