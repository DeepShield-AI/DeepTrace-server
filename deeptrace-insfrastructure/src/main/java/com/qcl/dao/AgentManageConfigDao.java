package com.qcl.dao;

import com.qcl.entity.AgentManageConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 采集器管理信息表（注册、启用、禁用、删除等）(AgentManageConfig)表数据库访问层
 *
 * @author makejava
 * @since 2025-12-17 13:59:03
 */
public interface AgentManageConfigDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AgentManageConfig queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param agentManageConfig 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<AgentManageConfig> queryAllByLimit(AgentManageConfig agentManageConfig, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param agentManageConfig 查询条件
     * @return 总行数
     */
    long count(AgentManageConfig agentManageConfig);

    /**
     * 新增数据
     *
     * @param agentManageConfig 实例对象
     * @return 影响行数
     */
    int insert(AgentManageConfig agentManageConfig);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<AgentManageConfig> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<AgentManageConfig> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<AgentManageConfig> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<AgentManageConfig> entities);

    /**
     * 修改数据
     *
     * @param agentManageConfig 实例对象
     * @return 影响行数
     */
    int update(AgentManageConfig agentManageConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据条件删除
     * @param agentManageConfig 条件参数
     * @return 删除的记录数
     */
    int deleteByParam(AgentManageConfig agentManageConfig);

    List<AgentManageConfig> queryAll(AgentManageConfig agentManageConfig);
}

