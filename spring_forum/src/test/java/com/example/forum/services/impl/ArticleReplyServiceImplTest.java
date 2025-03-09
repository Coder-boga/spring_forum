package com.example.forum.services.impl;

import com.example.forum.dao.ArticleReplyMapper;
import com.example.forum.model.ArticleReply;
import com.example.forum.services.IArticleReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleReplyServiceImplTest {
    @Autowired
    private IArticleReplyService articleReplyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void create() {
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(9L);
        articleReply.setPostUserId(1L);
        articleReply.setContent("单元回复测试");
        articleReplyService.create(articleReply);
        System.out.println("添加成功");
    }

    @Test
    void selectByArticleId() throws JsonProcessingException {
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(1L);
        System.out.println(objectMapper.writeValueAsString(articleReplies));
    }
}