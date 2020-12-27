package com.imooc.pojo.bo;

import lombok.Data;

/**
 * 用于创建订单BO
 * @author liangwq
 * @date 2020/12/27
 */
@Data
public class SubmitBO {

    private String userId;
    private String itemSpecIds;
    private String addressId;
    private Integer payMethod;
    private String leftMsg;

}
