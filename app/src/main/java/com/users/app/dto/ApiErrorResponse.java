package com.users.app.dto;

import java.util.List;

public class ApiErrorResponse {
    private List<ApiError> error;

    public ApiErrorResponse(List<ApiError> error) {
        this.error = error;
    }

    public List<ApiError> getError() {
        return error;
    }

    public void setError(List<ApiError> error) {
        this.error = error;
    }
}

