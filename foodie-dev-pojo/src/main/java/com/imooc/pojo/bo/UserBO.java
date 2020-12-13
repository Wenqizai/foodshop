package com.imooc.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liangwq
 * @date 2020/12/12
 */
@ApiModel(value = "用户对象BO", description = "从客户端, 由用户传入的数据封装在次Entity中")
@Data
public class UserBO {

    @ApiModelProperty(value = "用户名", name = "username", example = "imooc", required = true)
    private String username;
    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = true)
    private String confirmPassword;

}
