package com.imooc.enums;

/**
 * 性别枚举
 *
 * @author liangwq
 * @date 2020/12/12
 */
public enum SexEnum {

    WOMAN(0, "女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    public final Integer type;
    public final String value;

    SexEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
