package com.example.forum.services;

import com.example.forum.model.Message;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IMessageService {
    void create(Message message);

    /**
     * 根据接收用户查询未读数
     * @param
     * @return
     */
    Integer selectUnreadCount(Long receiveUserId);

    /**
     * 根据接收者用户Id查询所有私信
     * @param receiveUserId
     * @return
     */
    List<Message> selectByReceiveUserId(Long receiveUserId);

    /**
     * 更新指定私信的状态
     * @param id
     * @param state
     */
    void updateStateById(Long id, Byte state);

    /**
     * 根据Id查询私信
     * @param id
     * @return
     */
    Message selectById(Long id);

    /**
     * 回复私信
     * @param repliedId
     * @param message
     */
    @Transactional
    void reply(Long repliedId, Message message);
}
