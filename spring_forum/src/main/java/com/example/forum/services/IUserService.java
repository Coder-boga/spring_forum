package com.example.forum.services;

import com.example.forum.model.Article;
import com.example.forum.model.User;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserService {
    /**
     * 根据用户名查找用户
     *
     * @param username
     * @return
     */
    User selectByUserName(String username);

    /**
     * 创建用户
     *
     * @param user
     */
    void create(User user);

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 根据用户id查询用户信息
     * @param id
     * @return
     */
    User selectById(Long id);

    /**
     * 更新当前用户的发帖数 + 1
     * @param id
     */
    void addOneArticleCountById(Long id);

    /**
     * 更新当前用户的发帖数 - 1
     * @param id
     */
    void subOneArticleCountById(Long id);

    /**
     * 修改用户信息
     * @param user
     */
    void modifyInfo(User user);

    /**
     *  修改密码
     * @param
     */
    void modifyPassword(Long id, String newPssword, String oldPassword);


}