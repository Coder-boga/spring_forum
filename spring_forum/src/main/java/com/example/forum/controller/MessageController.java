package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.dao.UserMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Message;
import com.example.forum.model.User;
import com.example.forum.services.IMessageService;
import com.example.forum.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "私信接口")
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private IMessageService messageService;

    @Autowired
    private IUserService userService;

    @Operation(summary = "发送私信")
    @PostMapping("/send")
    public AppResult send(HttpServletRequest request,
                          @Parameter(description = "接收者Id") @RequestParam("receiveUserId") @NonNull Long receiveUserId,
                          @Parameter(description = "内容") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        if (user.getId() == receiveUserId) {
            log.warn("不能给自己发送私信");
            return AppResult.failed("不能给自己发送私信");
        }

        User receiveUser = userService.selectById(receiveUserId);
        if (receiveUser == null || receiveUser.getId() < 0 || receiveUser.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(receiveUserId);
        message.setContent(content);

        messageService.create(message);

        return AppResult.success("发送成功");
    }

    @Operation(summary = "获取未读数")
    @GetMapping("/getUnreadCount")
    public AppResult getUnreadCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        Integer count = messageService.selectUnreadCount(user.getId());
        return AppResult.success(count);
    }

    @Operation(summary = "获取私信")
    @GetMapping("/getAll")
    public AppResult<List<Message>> getAll(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        List<Message> messages = messageService.selectByReceiveUserId(user.getId());
        return AppResult.success(messages);
    }

    @Operation(summary = "更新已读")
    @PostMapping("/markRead")
    public AppResult markRead(HttpServletRequest request,
                              @Parameter(description = "私信Id") @RequestParam("id") @NonNull Long id) {
        Message message = messageService.selectById(id);
        if (message == null || message.getState() == 1) {
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getId() != message.getReceiveUserId()) {
            log.warn(ResultCode.FAILED_FORBIDDEN.toString());
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        messageService.updateStateById(id, (byte) 1);

        return AppResult.success();
    }

    @Operation(summary = "回复私信")
    @PostMapping("/reply")
    public AppResult reply(HttpServletRequest request,
                           @Parameter(description = "私信Id") @RequestParam("repliedId") @NonNull Long repliedId,
                           @Parameter(description = "内容") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        Message existsMessage = messageService.selectById(repliedId);
        if (existsMessage == null || existsMessage.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }

        if (user.getId() == existsMessage.getPostUserId()) {
            log.warn("不能给自己发送私信！");
            return AppResult.failed("不能给自己发送私信!");
        }

        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(existsMessage.getPostUserId());
        message.setContent(content);

        messageService.reply(repliedId, message);

        return AppResult.success();
    }

}
