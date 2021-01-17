package com.imooc.mapper;

import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.MyOrdersVO;
import com.imooc.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderMapperCustom {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

}