package com.imooc.service;

import com.imooc.pojo.Stu;
import com.imooc.pojo.bo.SubmitBO;

/**
 * @author Wenqi Liang
 * @date 2020/12/1
 */
public interface OrderService {
    /**
     * 创建订单相关信息
     * @param submitBO
     */
    public String createOrder(SubmitBO submitBO);
}
