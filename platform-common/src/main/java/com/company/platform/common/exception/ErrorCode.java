package com.company.platform.common.exception;

public enum ErrorCode {
    BAD_REQUEST("BAD_REQUEST", "Request validation failed"),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication required"),
    FORBIDDEN("FORBIDDEN", "Permission denied"),
    NOT_FOUND("NOT_FOUND", "Resource not found"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Internal server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
