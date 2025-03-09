package com.example.forum.services.impl;

import com.example.forum.model.Article;
import com.example.forum.services.IArticleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceImplTest {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void create() {
        Article article = new Article();
        article.setUserId(2L);
        article.setBoardId(2L);
        article.setTitle("单元测试");
        article.setContent("测试内容");
        articleService.create(article);
        System.out.println("发帖成功！");
    }

    @Test
    void selectAll() throws JsonProcessingException {
        List<Article> articles = articleService.selectAll();
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectAllByBoardId() throws JsonProcessingException {
        List<Article> articles = articleService.selectAllByBoardId(22L);
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectDetailById() {
        Article article = articleService.selectDetailById(5L);
        System.out.println(article);
        System.out.println("查询成功");
    }

    @Test
    @Transactional
    void modify() {
        articleService.modify(1L, "修改测试","修改测试内容");
        System.out.println("修改成功");
    }

    @Test
    void selectById() {
        Article article = articleService.selectById(1L);
        System.out.println(article);
        System.out.println("查询成功");
    }

    @Test
    void thumbsUpById() {
        articleService.thumbsUpById(1L);
        System.out.println("点赞成功");
    }

    @Test
    @Transactional
    void deleteById() {
        articleService.deleteById(5L);
        System.out.println("删除成功");

    }

    @Test
    void addOneReplyCountById() {
        articleService.addOneReplyCountById(7L);
        System.out.println("更新成功");
    }

    @Test
    void selectByUserId() throws JsonProcessingException {
        List<Article> articles = articleService.selectByUserId(1L);
        System.out.println(objectMapper.writeValueAsString(articles));
    }
}