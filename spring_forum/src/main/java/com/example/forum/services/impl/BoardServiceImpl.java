package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.BoardMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IBoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BoardServiceImpl implements IBoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<Board> selectByNum(Integer num) {
        if (num <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<Board> boards = boardMapper.selectByNum(num);
        return boards;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id < 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }

        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        Board updateboard = new Board();
        updateboard.setId(board.getId());
        updateboard.setArticleCount(board.getArticleCount() + 1);
        Date date = new Date();
        updateboard.setCreateTime(date);
        updateboard.setUpdateTime(date);
        int row = boardMapper.updateByPrimaryKeySelective(updateboard);
        if (row != 1) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
    }

    @Override
    public Board selectById(Long id) {
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_BANNED));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void subOneArticleCountById(Long id) {
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null || board.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        if (board.getState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_BANNED));
        }
        Board updateBoard = new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount() - 1);
        updateBoard.setDeleteState((byte) 0);
        updateBoard.setUpdateTime(new Date());
        if (updateBoard.getArticleCount() < 0) {
            updateBoard.setArticleCount(0);
        }
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
