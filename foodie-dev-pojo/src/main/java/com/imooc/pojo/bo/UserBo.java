package com.imooc.pojo.bo;

import lombok.Data;

/**
 * @author liangwq
 * @date 2020/12/12
 */
@Data
public class UserBo {

    private String username;
    private String password;
    private String confirmPassword;

}
