package com.users.app.dto;

import java.time.LocalDateTime;

public class ApiError {
    private LocalDateTime timestamp;
    private int code;
    private String detail;

    public ApiError(int code, String detail) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.detail = detail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}

