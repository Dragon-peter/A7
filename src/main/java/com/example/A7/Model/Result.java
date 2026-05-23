package com.example.A7.Model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Result <D> implements Serializable {
    @JsonProperty("isSuccess")
    private boolean success = false;

    private String code;
    private String message;

    private D data;

    public static <T> Result<T> create() {
        return new Result<T>();
    }

    public boolean isSuccess() {
        return success;
    }

    public Result setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Result<D> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<D> setMessage(String message) {
        this.message = message;
        return this;
    }

    public D getData() {
        return data;
    }

    public Result<D> setData(D data) {
        this.data = data;
        return this;
    }
    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return Result.<T>create()
                .setSuccess(true)
                .setCode("200")
                .setMessage("成功")
                .setData(data);
    }

    // 错误响应（仅消息）
    public static <T> Result<T> error(String code, String message) {
        return Result.<T>create()
                .setSuccess(false)
                .setCode(code)
                .setMessage(message);
    }
}

