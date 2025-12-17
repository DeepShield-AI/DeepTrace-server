package com.qcl.dao;

import com.qcl.entity.AgentUserConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 采集器用户配置表（用户添加）(AgentUserConfig)表数据库访问层
 *
 * @author makejava
 * @since 2025-12-17 16:07:46
 */
public interface AgentUserConfigDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AgentUserConfig queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param agentUserConfig 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<AgentUserConfig> queryAllByLimit(AgentUserConfig agentUserConfig, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param agentUserConfig 查询条件
     * @return 总行数
     */
    long count(AgentUserConfig agentUserConfig);

    /**
     * 新增数据
     *
     * @param agentUserConfig 实例对象
     * @return 影响行数
     */
    int insert(AgentUserConfig agentUserConfig);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<AgentUserConfig> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<AgentUserConfig> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<AgentUserConfig> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<AgentUserConfig> entities);

    /**
     * 修改数据
     *
     * @param agentUserConfig 实例对象
     * @return 影响行数
     */
    int update(AgentUserConfig agentUserConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

