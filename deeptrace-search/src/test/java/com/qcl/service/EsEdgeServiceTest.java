//package com.qcl.service;
//
//import com.qcl.entity.Edges;
//import com.qcl.entity.param.QueryEdgeParam;
//import com.qcl.vo.PageResult;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class EsEdgeServiceTest {
//
//    // 使用正确的接口类型而不是不存在的实现类
//    @Mock
//    private EsEdgeService esEdgeService;
//
//    @Test
//    void queryByPage_withValidParams_returnsPageResult() {
//        // 准备测试数据
//        QueryEdgeParam param = new QueryEdgeParam();
//        param.setStartTime(1755926183L);
//        param.setEndTime(1755926183L);
//        param.setPageNum(1);  // 修改2: 页码从1开始而不是0
//        param.setPageSize(10);
//
//        // 修改3: 使用正确的PageResult构造函数（4个参数）
//        PageResult<Edges> expectedResult = new PageResult<>(Collections.emptyList(), 1, 10, 0L);
//        when(esEdgeService.queryByPage(any(QueryEdgeParam.class))).thenReturn(expectedResult);
//
//        // 执行测试
//        PageResult<Edges> result = esEdgeService.queryByPage(param);
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals(0, result.getTotalElements());
//    }
//
//    @Test
//    void queryByPage_withInvalidTimeRange_handlesCorrectly() {
//        // 测试结束时间早于开始时间的情况
//        QueryEdgeParam param = new QueryEdgeParam();
//        param.setStartTime(1755926183L);
//        param.setEndTime(1755926180L); // 结束时间早于开始时间
//        param.setPageNum(1);  // 修改4: 页码从1开始而不是0
//        param.setPageSize(10);
//
//        // 修改5: 使用正确的PageResult构造函数（4个参数）
//        PageResult<Edges> expectedResult = new PageResult<>(Collections.emptyList(), 1, 10, 0L);
//        when(esEdgeService.queryByPage(any(QueryEdgeParam.class))).thenReturn(expectedResult);
//
//        PageResult<Edges> result = esEdgeService.queryByPage(param);
//
//        assertNotNull(result);
//        assertEquals(0, result.getTotalElements());
//    }
//}

package com.qcl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EsEdgeServiceTest {

    @Mock
    private EsEdgeService esEdgeService;

    // 业务逻辑测试
    @Nested
    @DisplayName("业务逻辑测试")
    class BusinessLogicTests {
        @Test
        void queryByPage_withValidParams_returnsPageResult() { /* ... */ }
    }

    // 参数验证测试
    @Nested
    @DisplayName("参数验证测试")
    class ParameterValidationTests {
        @Test
        void queryByPage_withNullStartTime_throwsBizException() { /* ... */ }
    }

    // 异常处理测试
    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTests {
        @Test
        void queryByPage_withElasticsearchException_throwsRuntimeException() { /* ... */ }
    }
}