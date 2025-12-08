package com.qcl.api;

import lombok.Data;

@Data
public class Result <T>{

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    protected Result() {
    }

    protected Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    public static <T> Result<T> error(IErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 未登录返回结果
     */
    public static <T> Result<T> unauthorized(T data) {
        return new Result<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(),data);
    }

}
