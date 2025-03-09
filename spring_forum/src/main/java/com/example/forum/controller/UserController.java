package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@Slf4j
@Tag(name = "用户接口")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public AppResult register(@Parameter(description = "用户名") @RequestParam("username") @NonNull String username,
                              @Parameter(description = "昵称") @RequestParam("nickname") @NonNull String nickname,
                              @Parameter(description = "密码") @RequestParam("password") @NonNull String password,
                              @Parameter(description = "确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        if (!password.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);

        String salt = UUIDUtil.UUID_32();
        String encryptPassword = MD5Util.md5Salt(password, salt);
        user.setPassword(encryptPassword);
        user.setSalt(salt);

        userService.create(user);
        return AppResult.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request,
                           @Parameter(description = "用户名") @RequestParam("username") @NonNull String username,
                           @Parameter(description = "密码") @RequestParam("password") @NonNull String password) {
        User user = userService.login(username, password);
        if (user == null) {
            log.warn(ResultCode.FAILED_LOGIN.toString());
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION, user);

        return AppResult.success();
    }

    @Operation(summary = "用户登出")
    @GetMapping("/logout")
    public AppResult logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("退出登录");
            session.invalidate();
        }
        return AppResult.success();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/getUserInfo")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @Parameter(description = "用户Id") @RequestParam(value = "id", required = false) Long id) {
        User user = null;
        if (id == null) {
            HttpSession session = request.getSession(false);
            user = (User) session.getAttribute(AppConfig.USER_SESSION);
        } else {
            user = userService.selectById(id);
        }

        if (user == null || user.getDeleteState() != 0) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        return AppResult.success(user);
    }

    @Operation(summary = "修改个人信息")
    @PostMapping("/modifyInfo")
    public AppResult modifyInfo(HttpServletRequest request,
                                @Parameter(description = "昵称") @RequestParam(value = "nickname", required = false) String nickname,
                                @Parameter(description = "性别") @RequestParam(value = "gender", required = false) Byte gender,
                                @Parameter(description = "邮箱") @RequestParam(value = "email", required = false) String email,
                                @Parameter(description = "电话号") @RequestParam(value = "phoneNum", required = false) String phoneNum,
                                @Parameter(description = "个人简介") @RequestParam(value = "remark", required = false) String remark) {
        if (!StringUtils.hasLength(nickname) && !StringUtils.hasLength(email) && !StringUtils.hasLength(phoneNum)
        && !StringUtils.hasLength(remark) && gender == null) {
            log.warn(ResultCode.FAILED.toString());
            return AppResult.failed("请输入要修改的内容!");
        }
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setNickname(nickname);
        updateUser.setGender(gender);
        updateUser.setEmail(email);
        updateUser.setPhoneNum(phoneNum);
        updateUser.setRemark(remark);
        updateUser.setUpdateTime(new Date());

        userService.modifyInfo(updateUser);
        user = userService.selectById(user.getId());

        session.setAttribute(AppConfig.USER_SESSION, user);
        return AppResult.success(user);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/modifyPassword")
    public AppResult modifyPassword(HttpServletRequest request,
                                    @Parameter(description = "原密码") @RequestParam("oldPassword") @NonNull String oldPassword,
                                    @Parameter(description = "新密码") @RequestParam("newPassword") @NonNull String newPassword,
                                    @Parameter(description = "确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        if (!newPassword.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        userService.modifyPassword(user.getId(), newPassword,oldPassword);
        if (session != null) {
            session.invalidate();
        }

        return AppResult.success("修改成功");
    }
}
