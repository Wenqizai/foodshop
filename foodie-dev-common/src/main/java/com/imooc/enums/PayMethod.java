package com.imooc.enums;

/**
 * 支付方式枚举
 * @author liangwq
 * @date 2020/12/27
 */
public enum  PayMethod {

    WEIXIN(1, "微信"),
    ALIPAY(2, "支付宝");

    public final Integer type;
    public final String value;

    PayMethod(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
