package com.qcl.service;

import com.qcl.entity.AgentUserConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 采集器用户配置表（用户添加）(AgentUserConfig)表服务接口
 *
 * @author makejava
 * @since 2025-12-17 16:07:46
 */
public interface AgentUserConfigService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AgentUserConfig queryById(Long id);

    /**
     * 分页查询
     *
     * @param agentUserConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<AgentUserConfig> queryByPage(AgentUserConfig agentUserConfig, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param agentUserConfig 实例对象
     * @return 实例对象
     */
    AgentUserConfig insert(AgentUserConfig agentUserConfig);

    /**
     * 修改数据
     *
     * @param agentUserConfig 实例对象
     * @return 实例对象
     */
    AgentUserConfig update(AgentUserConfig agentUserConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
