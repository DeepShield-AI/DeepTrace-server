package com.qcl.service.impl;

import com.qcl.entity.AgentManageConfig;
import com.qcl.dao.AgentManageConfigDao;
import com.qcl.service.AgentManageConfigService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 采集器管理信息表（注册、启用、禁用、删除等）(AgentManageConfig)表服务实现类
 *
 * @author makejava
 * @since 2025-12-17 13:59:07
 */
@Service("agentManageConfigService")
public class AgentManageConfigServiceImpl implements AgentManageConfigService {
    @Resource
    private AgentManageConfigDao agentManageConfigDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public AgentManageConfig queryById(Long id) {
        return this.agentManageConfigDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param agentManageConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<AgentManageConfig> queryByPage(AgentManageConfig agentManageConfig, PageRequest pageRequest) {
        long total = this.agentManageConfigDao.count(agentManageConfig);
        return new PageImpl<>(this.agentManageConfigDao.queryAllByLimit(agentManageConfig, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param agentManageConfig 实例对象
     * @return 实例对象
     */
    @Override
    public AgentManageConfig insert(AgentManageConfig agentManageConfig) {
        this.agentManageConfigDao.insert(agentManageConfig);
        return agentManageConfig;
    }

    /**
     * 修改数据
     *
     * @param agentManageConfig 实例对象
     * @return 实例对象
     */
    @Override
    public AgentManageConfig update(AgentManageConfig agentManageConfig) {
        this.agentManageConfigDao.update(agentManageConfig);
        return this.queryById(agentManageConfig.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.agentManageConfigDao.deleteById(id) > 0;
    }

    @Override
    public boolean isHostIpExist(String hostIp, String type) {
        AgentManageConfig agentManageConfig = new AgentManageConfig();
        agentManageConfig.setType(type);
        agentManageConfig.setHostIp(hostIp);
        long total = this.agentManageConfigDao.count(agentManageConfig);
        return total > 0;
    }

    @Override
    public boolean deleteByHostIp(String userId, String hostIp) {
        AgentManageConfig agentManageConfig = new AgentManageConfig();
        agentManageConfig.setUserId(userId);
        agentManageConfig.setHostIp(hostIp);
        return this.agentManageConfigDao.deleteByParam(agentManageConfig)>0;
    }

    @Override
    public List<AgentManageConfig> queryAll(AgentManageConfig agentManageConfig){
        return this.agentManageConfigDao.queryAll(agentManageConfig);
    }


}
