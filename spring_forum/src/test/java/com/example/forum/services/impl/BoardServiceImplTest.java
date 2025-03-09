package com.example.forum.services.impl;

import com.example.forum.dao.BoardMapper;
import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceImplTest {
    @Autowired
    private IBoardService boardService;

    @Test
    void selectByNum() {
        List<Board> boards = boardService.selectByNum(9);
        System.out.println(boards);
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        boardService.addOneArticleCountById(22L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void subOneArticleCountById() {
        boardService.subOneArticleCountById(2L);
        System.out.println("更新成功");
    }
}