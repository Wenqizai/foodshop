package com.imooc.service;

import com.imooc.pojo.Stu;
import com.imooc.pojo.bo.SubmitBO;
import com.imooc.pojo.vo.OrderVO;

/**
 * @author Wenqi Liang
 * @date 2020/12/1
 */
public interface OrderService {
    /**
     * 创建订单相关信息
     * @param submitBO
     */
    public OrderVO createOrder(SubmitBO submitBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);
}
