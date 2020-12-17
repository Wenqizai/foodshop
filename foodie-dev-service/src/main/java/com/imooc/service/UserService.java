package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;

/**
 * @author Wenqi Liang
 * @date 2020/12/1
 */
public interface UserService {
    /**
     * 判断用户名是否存在
     * @return
     */
    public boolean queryUserIsExist(String username);

    /**
     * 创建用户
     * @param userBo
     * @return
     */
    public Users createUsers(UserBO userBo);

    /**
     * 检索用户名和密码是否匹配, 用于登录
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username, String password);
}
