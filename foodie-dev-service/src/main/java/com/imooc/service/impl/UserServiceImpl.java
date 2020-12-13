package com.imooc.service.impl;

import com.imooc.enums.SexEnum;
import com.imooc.mapper.StuMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Stu;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBo;
import com.imooc.service.StuService;
import com.imooc.service.UserService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.MD5Utils;
import org.n3r.idworker.IdWorker;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author liangwq
 * @date 2020/12/1
 */
@Service
public class UserServiceImpl implements UserService {
    // 默认用户头像
    private static final String USER_FACE = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=157768749,3220967333&fm=26&gp=0.jpg";

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserIsExist(String username) {
        Example userExample = new Example(Users.class);
        userExample.createCriteria().andEqualTo("username", username);
        Users users = usersMapper.selectOneByExample(userExample);
        return users == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUsers(UserBo userBo) {
        Users user = new Users();
        // 借助id生成器, 生成全局id
        String userId = sid.nextShort();
        user.setId(userId);
        user.setUsername(userBo.getUsername());
        try {
            user.setPassword(MD5Utils.getMD5Str(userBo.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 默认用户昵称同头像
        user.setNickname(userBo.getUsername());
        // 默认头像
        user.setFace(USER_FACE);
        // 默认生日
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        // 默认性别为保密
        user.setSex(SexEnum.SECRET.type);
        // 注册时间
        user.setCreatedTime(new Date());
        user.setUpdatedTime(user.getCreatedTime());
        usersMapper.insert(user);
        return user;
    }
}
