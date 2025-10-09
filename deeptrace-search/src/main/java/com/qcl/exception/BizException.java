package com.qcl.exception;

import lombok.Getter;

public class BizException extends RuntimeException {

    @Getter
    private String code;
    private String message;

    // 默认构造函数
    public BizException() {
        super();
    }

    // 带错误信息的构造函数
    public BizException(String message) {
        super(message);
        this.message = message;
    }

    // 带错误码和错误信息的构造函数
    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    // 带错误信息和异常原因的构造函数
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    // 带错误码、错误信息和异常原因的构造函数
    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
