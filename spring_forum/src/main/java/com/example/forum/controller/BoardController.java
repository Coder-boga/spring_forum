package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "板块接口")
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private IBoardService boardService;

    @Value("${forum.index.board-num:9}")
    private Integer indexObardNum;

    @GetMapping("/topList")
    @Operation(summary = "获取首页板块列表")
    public AppResult topList() {
        List<Board> boards = boardService.selectByNum(indexObardNum);
        if (boards == null) {
            boards = new ArrayList<>();
        }

        return AppResult.success(boards);
    }

    @Operation(summary = "获取板块信息")
    @GetMapping("/getById")
    public AppResult<Board> getById(@Parameter(description = "板块Id") @RequestParam(value = "id") @NonNull Long id) {
        Board board = boardService.selectById(id);
        if (board == null || board.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }
        return AppResult.success(board);
    }
}
