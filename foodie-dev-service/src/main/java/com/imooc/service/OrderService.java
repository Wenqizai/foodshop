package com.imooc.service;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Stu;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitBO;
import com.imooc.pojo.vo.OrderVO;

import java.util.List;

/**
 * @author Wenqi Liang
 * @date 2020/12/1
 */
public interface OrderService {
    /**
     * 创建订单相关信息
     * @param submitBO
     */
    public OrderVO createOrder(SubmitBO submitBO, List<ShopcartBO> shopcartList);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付的订单
     */
    public void closeOrder();
}
