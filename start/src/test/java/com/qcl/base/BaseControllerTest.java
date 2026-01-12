package com.qcl.base;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 控制器层单元测试基类
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public abstract class BaseControllerTest {
    // 公共的Mock配置和工具方法
}
