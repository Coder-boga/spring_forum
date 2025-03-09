package com.example.forum.services.impl;

import com.example.forum.dao.UserMapper;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import org.apache.ibatis.annotations.Update;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IUserService userService;

    @Test
    void selectByUserName() {
        User user = userMapper.selectByUserName("boy");
        System.out.println(user);
    }

    @Test
    @Transactional
    void create() {
        User user = new User();
        user.setUsername("boy");
        user.setNickname("boy");
        String password = "123456";
        String salt = UUIDUtil.UUID_32();
        String ciphertext = MD5Util.md5Salt(password, salt);
        user.setPassword(ciphertext);
        user.setSalt(salt);
        userService.create(user);
        System.out.println(user);
    }

    @Test
    void login() {
        User user = userService.login("boy", "1234567");
        System.out.println(user);
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        userService.addOneArticleCountById(10L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void subOneArticleCountById() {
        userService.subOneArticleCountById(1L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void modifyInfo() {
        User user = new User();
        user.setId(1L);
        user.setNickname("boy");
        user.setGender((byte) 1);
        user.setPhoneNum("15819838888");
        user.setEmail("@qq");
        user.setRemark("哈哈哈哈哈哈哈哈哈");
        userService.modifyInfo(user);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void modifyPassword() {
        userService.modifyPassword(1L,"123456","1234566");
        System.out.println("更新成功");
    }
}