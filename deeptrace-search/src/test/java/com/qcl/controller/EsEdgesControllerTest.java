package com.qcl.controller;

import com.qcl.config.TestConfig;
import com.qcl.entity.Edges;
import com.qcl.entity.param.QueryEdgeParam;
import com.qcl.service.EsEdgeService;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EsEdgesController.class)
@Import(TestConfig.class)
class EsEdgesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EsEdgeService esEdgeService;

    // 端点可达性测试
    @Nested
    @DisplayName("端点可达性测试")
    class EndpointAccessibilityTests {
        @Test
        void search_returnsOk() throws Exception {
            PageResult<Edges> mockResult = new PageResult<>(Collections.emptyList(), 1, 10, 0L);
            when(esEdgeService.queryByPage(any(QueryEdgeParam.class))).thenReturn(mockResult);

            mockMvc.perform(get("/api/esEdges/log/queryByPage")
                            .param("startTime", "1755926183")
                            .param("endTime", "1755926183")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk());
        }
    }

    // 参数验证测试
    @Nested
    @DisplayName("参数验证测试")
    class ParameterValidationTests {
        @Test
        void search_withMissingRequiredParams_returnsOk() throws Exception {
            // 注意：由于控制器中没有启用参数验证，这个测试将正常通过
            PageResult<Edges> mockResult = new PageResult<>(Collections.emptyList(), 1, 10, 0L);
            when(esEdgeService.queryByPage(any(QueryEdgeParam.class))).thenReturn(mockResult);

            mockMvc.perform(get("/api/esEdges/log/queryByPage")
                            .param("endTime", "1755926183")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk());
        }

        @Test
        void search_withInvalidTimeFormat_returnsOk() throws Exception {
            // 注意：由于控制器中没有启用参数验证，这个测试将正常通过
            PageResult<Edges> mockResult = new PageResult<>(Collections.emptyList(), 1, 10, 0L);
            when(esEdgeService.queryByPage(any(QueryEdgeParam.class))).thenReturn(mockResult);

            mockMvc.perform(get("/api/esEdges/log/queryByPage")
                            .param("startTime", "invalid_time")
                            .param("endTime", "1755926183")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk());
        }
    }

    // 响应格式测试
    @Nested
    @DisplayName("响应格式测试")
    class ResponseFormatTests {
        @Test
        void search_withValidParams_returnsCorrectStructure() throws Exception {
            Edges mockEdge = new Edges();
            PageResult<Edges> mockResult = new PageResult<>(List.of(mockEdge), 1, 10, 1L);
            when(esEdgeService.queryByPage(any(QueryEdgeParam.class))).thenReturn(mockResult);

            mockMvc.perform(get("/api/esEdges/log/queryByPage")
                            .param("startTime", "1755926183")
                            .param("endTime", "1755926183")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }
}