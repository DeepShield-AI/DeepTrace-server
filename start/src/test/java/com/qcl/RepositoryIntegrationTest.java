package com.qcl;

import com.qcl.entity.AgentBasic;
import com.qcl.repository.EsAgentBasicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据访问层测试类 - 测试Repository层的基础CRUD操作
 * 测试目标：验证数据访问层的完整性和正确性
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class RepositoryIntegrationTest {

    @Autowired
    private EsAgentBasicRepository esAgentBasicRepository;

    private static final int TEST_PAGE_SIZE = 5;

    @BeforeEach
    void setUp() {
        assertNotNull(esAgentBasicRepository, "Repository应该被正确注入");
    }

    /**
     * 测试1: 数据统计功能
     */
    @Test
    void testDataCount() {
        long agentCount = esAgentBasicRepository.count();
        assertTrue(agentCount >= 0, "Agent数量应该大于等于0");
        System.out.println("✅ 数据统计测试通过，总数: " + agentCount);
    }

    /**
     * 测试2: 分页查询功能
     */
    @Test
    void testPagination() {
        Pageable pageable = PageRequest.of(0, TEST_PAGE_SIZE);
//        Page<AgentBasic> agentPage = esAgentBasicRepository.findAll(pageable);
        Page<AgentBasic> agentPage = esAgentBasicRepository.findAll(pageable);

        assertNotNull(agentPage, "分页查询结果不应该为null");
        assertEquals(TEST_PAGE_SIZE, agentPage.getNumberOfElements(),
                "分页查询应该返回指定数量的记录");

        System.out.println("✅ 分页查询测试通过");
    }
//    @Test
//    void testPagination() {
//        try {
//            Pageable pageable = PageRequest.of(0, TEST_PAGE_SIZE);
//            Page<AgentBasic> agentPage = esAgentBasicRepository.findAll(pageable);
//
//            assertNotNull(agentPage, "分页查询结果不应该为null");
//            assertTrue(agentPage.getNumberOfElements() >= 0,
//                    "分页查询应该返回有效数量的记录");
//
//            // 如果数据不为空，验证数据格式
//            if (!agentPage.isEmpty()) {
//                AgentBasic firstAgent = agentPage.getContent().get(0);
//                assertNotNull(firstAgent.getLcuuid(), "Agent的LCUUID不应该为null");
//                assertNotNull(firstAgent.getName(), "Agent名称不应该为null");
//            }
//
//            System.out.println("✅ 分页查询测试通过，返回记录数: " + agentPage.getNumberOfElements());
//        } catch (Exception e) {
//            System.err.println("❌ 分页查询测试失败: " + e.getMessage());
//            e.printStackTrace();
//            fail("分页查询测试应该成功: " + e.getMessage());
//        }
//    }

    /**
     * 测试3: 按ID查询功能
     */
    @Test
    void testFindById() {
        // 先获取一个样本ID
        Pageable pageable = PageRequest.of(0, 1);
        Page<AgentBasic> samplePage = esAgentBasicRepository.findAll(pageable);

        if (!samplePage.isEmpty()) {
            String sampleLcuuid = samplePage.getContent().get(0).getLcuuid();
            Optional<AgentBasic> foundAgent = esAgentBasicRepository.findById(sampleLcuuid);

            assertTrue(foundAgent.isPresent(), "应该能找到指定ID的Agent");
            assertEquals(sampleLcuuid, foundAgent.get().getLcuuid(), "返回的Agent ID应该匹配");

            System.out.println("✅ 按ID查询测试通过");
        } else {
            System.out.println("⚠️ 没有样本数据，跳过按ID查询测试");
        }
    }

    /**
     * 测试4: 数据完整性验证
     */
    @Test
    void testDataIntegrity() {
        Pageable pageable = PageRequest.of(0, TEST_PAGE_SIZE);
        Page<AgentBasic> agentPage = esAgentBasicRepository.findAll(pageable);

        if (!agentPage.isEmpty()) {
            AgentBasic sampleAgent = agentPage.getContent().get(0);

            // 验证关键字段完整性
            assertNotNull(sampleAgent.getLcuuid(), "LCUUID不应该为null");
            assertNotNull(sampleAgent.getName(), "Agent名称不应该为null");
            assertNotNull(sampleAgent.getState(), "Agent状态不应该为null");

            System.out.println("✅ 数据完整性测试通过");
        } else {
            System.out.println("⚠️ 没有样本数据，跳过数据完整性测试");
        }
    }
}
