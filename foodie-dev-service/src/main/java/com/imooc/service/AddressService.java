package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

/**
 * 收货地址
 *
 * @author Wenqi Liang
 * @date 2020/12/19
 */
public interface AddressService {

    /**
     * 根据用户id查询用户的收货地址列表
     *
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     *
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 用户更新地址
     *
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用户id和地址id, 删除用户对应的地址id
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId, String addressId);

    /**
     * 根据用户id和地址id, 修改默认地址
     * @param userId
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId, String addressId);

    /**
     * 根据用户id和地址id, 查询具体的用户地址对象信息
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId, String addressId);
}
