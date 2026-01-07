package com.qcl.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultCode枚举测试
 * 测试错误码枚举的定义和值
 */
class ResultCodeTest {

    @Test
    void shouldHaveCorrectSuccessCode() {
        //  验证SUCCESS枚举值
        assertEquals(200, ResultCode.SUCCESS.getCode());
        assertEquals("操作成功", ResultCode.SUCCESS.getMessage());
    }

    @Test
    void shouldHaveCorrectFailedCode() {
        // 验证FAILED枚举值
        assertEquals(500, ResultCode.FAILED.getCode());
        assertEquals("操作失败", ResultCode.FAILED.getMessage());
    }

    @Test
    void shouldHaveCorrectValidateFailedCode() {
        // 验证VALIDATE_FAILED枚举值
        assertEquals(404, ResultCode.VALIDATE_FAILED.getCode());
        assertEquals("参数检验失败", ResultCode.VALIDATE_FAILED.getMessage());
    }

    @Test
    void shouldHaveCorrectUnauthorizedCode() {
        //  验证UNAUTHORIZED枚举值
        assertEquals(401, ResultCode.UNAUTHORIZED.getCode());
        assertEquals("暂未登录或token已经过期", ResultCode.UNAUTHORIZED.getMessage());
    }

    @Test
    void shouldHaveCorrectForbiddenCode() {
        //  验证FORBIDDEN枚举值
        assertEquals(403, ResultCode.FORBIDDEN.getCode());
        assertEquals("没有相关权限", ResultCode.FORBIDDEN.getMessage());
    }

    @Test
    void shouldImplementIErrorCodeInterface() {
        // 准备测试数据
        IErrorCode errorCode = ResultCode.SUCCESS;

        //  验证接口实现
        assertNotNull(errorCode.getCode());
        assertNotNull(errorCode.getMessage());
    }
}