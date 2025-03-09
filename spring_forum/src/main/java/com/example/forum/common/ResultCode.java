package com.example.forum.common;

public enum ResultCode {
    SUCCESS                     (0, "操作成功"),
    FAILED                      (1000, "操作失败"),
    FAILED_UNAUTHORIZED         (1001, "未授权"),
    FAILED_PARAMS_VALIDATE      (1002, "参数校验失败"),
    FAILED_FORBIDDEN            (1003, "禁止访问"),
    FAILED_CREATE               (1004, "新增失败"),
    FAILED_NOT_EXISTS           (1005, "资源不存在"),

    // 用户
    FAILED_USER_EXISTS          (1101, "用户已存在"),
    FAILED_USER_NOT_EXISTS      (1102, "用户不存在"),
    FAILED_LOGIN                (1103, "用户名或密码错误"),
    FAILED_USER_BANNED          (1104, "您已被禁用，请联系管理员"),
    FAILED_TWO_PWD_NOT_SAME     (1105, "两次输入的密码不一致"),
    FAILED_USER_ARTICLE_COUNT    (1106, "用户更新帖子数量失败"),
    FAILED_PASSWORD_NOT_SAME    (1107, "与原密码不一致"),

    // 板块
    FAILED_BOARD_EXISTS         (1201, "板块已存在"),
    FAILED_BOARD_NOT_EXISTS     (1202, "板块不存在"),
    FAILED_BOARD_BANNED         (1203, "板块状态异常"),
    FAILED_BOARD_ARTICLE_COUNT  (1201, "板块更新帖子数量失败"),



    // 帖子
    FAILED_ARTICLE_NOT_EXISTS    (1301, "帖子不存在"),
    FAILED_ARTICLE_BANNED         (1302, "帖子状态异常"),

    // 私信
    FAILED_MESSAGE_NOT_EXISTS      (1401, "私信不存在"),

    // 服务器
    ERROR_SERVICES              (2000, "服务器内部错误"),
    ERROR_IS_NULL               (2001, "IS NULL");

    private int code;
    private String message;
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "code = " + code + ", message = " + message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
