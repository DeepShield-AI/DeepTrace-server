package com.qcl.service;

import com.qcl.entity.AgentManageConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 采集器管理信息表（注册、启用、禁用、删除等）(AgentManageConfig)表服务接口
 *
 * @author makejava
 * @since 2025-12-17 13:59:06
 */
public interface AgentManageConfigService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AgentManageConfig queryById(Long id);

    /**
     * 分页查询
     *
     * @param agentManageConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<AgentManageConfig> queryByPage(AgentManageConfig agentManageConfig, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param agentManageConfig 实例对象
     * @return 实例对象
     */
    AgentManageConfig insert(AgentManageConfig agentManageConfig);

    /**
     * 修改数据
     *
     * @param agentManageConfig 实例对象
     * @return 实例对象
     */
    AgentManageConfig update(AgentManageConfig agentManageConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    boolean isHostIpExist(String hostIp,String type);

    boolean deleteByHostIp(String userId, String hostIp);

    /**
     * 分页查询
     *
     * @param agentManageConfig 筛选条件
     * @return 查询结果
     */
    List<AgentManageConfig> queryAll(AgentManageConfig agentManageConfig);

    boolean deleteByParam(AgentManageConfig agentManageConfig);
}
