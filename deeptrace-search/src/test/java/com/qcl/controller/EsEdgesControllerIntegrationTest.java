package com.qcl.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class EsEdgesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void integrationTest_searchWithValidParams_returnsOk() throws Exception {
        mockMvc.perform(get("/api/esEdges/log/queryByPage")
                        .param("startTime", "1755926183")
                        .param("endTime", "1755926183")
                        .param("pageNum", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }
}