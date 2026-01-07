package com.qcl.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Result类单元测试
 * 测试API响应封装类的各种构造方法
 * 验证成功结果、失败结果、参数校验失败结果、未授权结果、权限拒绝结果的构造方法
 */
class ResultTest {

    @Test
    void shouldCreateSuccessResultWithData() {
        // Given - 准备测试数据
        String testData = "测试数据";

        // When - 执行测试动作
        Result<String> result = Result.success(testData);

        // Then - 验证结果
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals(testData, result.getData());
    }

    @Test
    void shouldCreateSuccessResultWithoutData() {
        Result<Object> result = Result.success();

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateErrorResultWithMessage() {
        String errorMessage = "操作失败";

        Result<Object> result = Result.error(errorMessage);

        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateErrorResultWithCodeAndMessage() {
        Integer errorCode = 404;
        String errorMessage = "资源未找到";

        Result<Object> result = Result.error(errorCode, errorMessage);

        assertEquals(errorCode, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateErrorResultWithErrorCodeEnum() {
        Result<Object> result = Result.error(ResultCode.FAILED);

        assertEquals(500, result.getCode());
        assertEquals("操作失败", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateUnauthorizedResult() {
        String data = "需要登录";

        Result<String> result = Result.unauthorized(data);

        assertEquals(401, result.getCode());
        assertEquals("暂未登录或token已经过期", result.getMessage());
        assertEquals(data, result.getData());
    }
}