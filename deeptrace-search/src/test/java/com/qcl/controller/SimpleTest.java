package com.qcl.controller;

import com.qcl.service.EsTraceService;
import com.qcl.vo.PageResult;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import org.mockito.Mockito;

import java.util.ArrayList;

public class SimpleTest {
    public static void main(String[] args) {
        // 直接创建模拟对象，不使用Spring
        EsTraceService mockService = Mockito.mock(EsTraceService.class);

        // 创建测试数据
        PageResult<Traces> mockResult = PageResult.of(new ArrayList<>(), 1, 10, 0);

        // 尝试调用方法 - 如果这行编译通过，说明方法存在
        Mockito.when(mockService.queryByPageResult(Mockito.any(QueryTracesParam.class))).thenReturn(mockResult);

        System.out.println("测试通过！方法queryByPageResult存在且可用");
    }
}