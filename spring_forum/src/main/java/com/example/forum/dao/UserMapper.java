package com.example.forum.dao;

import com.example.forum.model.Article;
import com.example.forum.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    User selectByUserName(@Param("username") String username);


}