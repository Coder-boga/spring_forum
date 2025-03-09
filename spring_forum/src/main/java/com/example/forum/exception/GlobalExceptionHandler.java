package com.example.forum.exception;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody // 将 AppResult 对象序列化为 JSON 并返回给客户端
    @ExceptionHandler(ApplicationException.class)
    public AppResult applicationException(ApplicationException e) {
        log.error(e.getMessage());
        if (e.getMessage() != null) {
            return e.getErrorResult();
        }
        if (e.getMessage() == null || e.getMessage().equals("")) {
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        return AppResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AppResult exceptionHandler(Exception e) {
        log.error(e.getMessage());
        if (e.getMessage() == null || e.getMessage().equals("")) {
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        return AppResult.failed(e.getMessage());
    }
}
