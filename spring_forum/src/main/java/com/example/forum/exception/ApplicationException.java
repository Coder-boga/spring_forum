package com.example.forum.exception;

import com.example.forum.common.AppResult;

public class ApplicationException extends RuntimeException{
    protected AppResult errorResult;

    public ApplicationException(AppResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }

    public ApplicationException(String message) {
        super(message);
    }

    public AppResult getErrorResult() {
        return errorResult;
    }
}
