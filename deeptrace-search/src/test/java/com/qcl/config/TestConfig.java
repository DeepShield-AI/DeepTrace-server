package com.qcl.config;

import com.qcl.service.EsEdgeService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@ComponentScan(basePackages = "com.qcl") // 添加组件扫描
public class TestConfig {

    @Bean
    @Primary
    public EsEdgeService esEdgeService() {
        return Mockito.mock(EsEdgeService.class);
    }
}