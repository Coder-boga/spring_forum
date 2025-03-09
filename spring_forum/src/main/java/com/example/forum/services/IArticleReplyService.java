package com.example.forum.services;

import com.example.forum.model.ArticleReply;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleReplyService {

    /**
     * 回复帖子
     * @param articleReply
     */
    @Transactional
    void create(ArticleReply articleReply);


    /**
     * 根据帖子Id查询所有的回复
     * @param articleId
     * @return
     */
    List<ArticleReply> selectByArticleId(Long articleId);
}
