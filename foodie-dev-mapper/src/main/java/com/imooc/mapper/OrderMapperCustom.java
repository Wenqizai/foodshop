package com.imooc.mapper;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.MyOrdersVO;
import com.imooc.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderMapperCustom {
    /**
     * 查询所有订单
     * @param map
     * @return
     */
    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    /**
     * 查询订单状态数量
     * @param map
     * @return
     */
    public int getMyOrderStatusCount(@Param("paramsMap") Map<String, Object> map);

    /**
     * 查询订单动向
     * @param map
     * @return
     */
    public List<OrderStatus> queryMyOrdersTrend(@Param("paramsMap") Map<String, Object> map);
}