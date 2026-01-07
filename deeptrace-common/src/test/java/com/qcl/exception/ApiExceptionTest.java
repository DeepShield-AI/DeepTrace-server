package com.qcl.exception;

import com.qcl.api.IErrorCode;
import com.qcl.api.ResultCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * ApiException单元测试
 * 测试异常类的构造函数和基本行为
 */
class ApiExceptionTest {

    @Test
    void shouldCreateApiExceptionWithMessage() {
        String errorMessage = "自定义错误消息";

        ApiException exception = new ApiException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getErrorCode(), "使用字符串构造时errorCode应该为null");
    }

    @Test
    void shouldCreateApiExceptionWithErrorCode() {
        IErrorCode errorCode = ResultCode.FAILED;

        ApiException exception = new ApiException(errorCode);

        assertEquals("操作失败", exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(500, exception.getErrorCode().getCode());
    }

    @Test
    void shouldCreateApiExceptionWithMessageAndCause() {
        String errorMessage = "包装异常";
        RuntimeException cause = new RuntimeException("原始异常");

        ApiException exception = new ApiException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateApiExceptionWithCauseOnly() {
        RuntimeException cause = new RuntimeException("原始异常");

        ApiException exception = new ApiException(cause);

        assertEquals("java.lang.RuntimeException: 原始异常", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}