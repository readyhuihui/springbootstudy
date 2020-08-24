package com.yonghui.springbootmybatisgenerator.service;

import com.yonghui.springbootmybatisgenerator.entity.User;
import com.yonghui.springbootmybatisgenerator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:lyh
 * @Description:
 * @Date:Created in 2020/8/23 19:22
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> selectByExample() {
        return userMapper.selectAllUser();
    }


    public User selectByPrimaryKey(Integer userid) {
        return userMapper.selectByPrimaryKey(userid);
    }

    public User findUserByName(String name) {
        return userMapper.findUserByName(name);
    }
}
