package com.cumulocity.greenbox.server.model;

import static com.google.common.base.Throwables.getRootCause;

import com.google.common.base.Throwables;

public class GreenBoxResponse {

    private boolean success;

    private String message;

    protected GreenBoxResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static GreenBoxResponse success() {
        return new GreenBoxResponse(true, "Request correctly handled");
    }

    public static GreenBoxResponse failure(Throwable cause) {
        return new GreenBoxResponse(false, getRootCause(cause).getMessage());
    }

}
