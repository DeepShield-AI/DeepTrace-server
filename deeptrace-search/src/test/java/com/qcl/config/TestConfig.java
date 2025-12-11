package com.qcl.config;

import com.qcl.service.EsTraceService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public EsTraceService esTraceService() {
        return Mockito.mock(EsTraceService.class);
    }
}