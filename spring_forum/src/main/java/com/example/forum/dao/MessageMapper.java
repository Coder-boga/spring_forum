package com.example.forum.dao;

import com.example.forum.model.Message;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    /**
     * 根据接收用户查询未读数
     * @param
     * @return
     */
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);

    /**
     * 根据接收者用户Id查询所有私信
     * @param receiveUserId
     * @return
     */
    List<Message> selectByReceiveUserId(@Param("receiveUserId") Long receiveUserId);
}