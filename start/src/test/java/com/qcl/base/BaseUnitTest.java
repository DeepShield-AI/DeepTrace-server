package com.qcl.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * 单元测试基类 - 提供单元测试通用配置
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit-test")
public abstract class BaseUnitTest {

    @BeforeEach
    void setUp() {
        // 单元测试通用设置
    }

    protected <T> T createMock(Class<T> clazz) {
        return org.mockito.Mockito.mock(clazz);
    }
}