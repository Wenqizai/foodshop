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
     * @param usersBo
     * @return
     */
    public Users createUsers(UserBO userBo);
}
