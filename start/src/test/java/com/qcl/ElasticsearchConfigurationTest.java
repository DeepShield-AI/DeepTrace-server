package com.qcl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础配置测试类 - 测试Spring配置、Bean注入、连接状态
 * 测试目标：验证应用启动和基础配置的正确性
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class ElasticsearchConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * 测试1: 应用启动和上下文配置
     */
    @Test
    void testApplicationContext() {
        assertNotNull(applicationContext, "Spring应用上下文应该成功创建");
        System.out.println("✅ 应用上下文配置测试通过");
    }

    /**
     * 测试2: 关键Bean注入验证
     */
    @Test
    void testKeyBeansInjection() {
        // 验证Elasticsearch相关Bean
        assertNotNull(elasticsearchOperations, "ElasticsearchOperations应该被正确注入");

        // 验证Repository Bean
        var agentRepository = applicationContext.getBean("esAgentBasicRepository");
        assertNotNull(agentRepository, "EsAgentBasicRepository应该存在");

        // 验证Service Bean
        var traceService = applicationContext.getBean("esTraceService");
        assertNotNull(traceService, "EsTraceService应该存在");

        System.out.println("✅ 关键Bean注入测试通过");
    }

    /**
     * 测试3: Elasticsearch连接状态
     */
    @Test
    void testElasticsearchConnection() {
        assertNotNull(elasticsearchOperations, "Elasticsearch连接应该可用");

        // 验证索引存在性
        var indexOps = elasticsearchOperations.indexOps(com.qcl.entity.Traces.class);
        boolean indexExists = indexOps.exists();
        assertTrue(indexExists, "Traces索引应该存在");

        System.out.println("✅ Elasticsearch连接测试通过");
    }

    /**
     * 测试4: 配置文件验证
     */
    @Test
    void testConfigurationProperties() {
        // 验证测试配置文件加载
        String activeProfile = applicationContext.getEnvironment().getActiveProfiles()[0];
        assertEquals("test", activeProfile, "应该使用test配置文件");

        System.out.println("✅ 配置文件验证通过");
    }
}