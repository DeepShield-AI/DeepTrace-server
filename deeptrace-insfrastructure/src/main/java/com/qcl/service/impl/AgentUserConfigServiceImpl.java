package com.qcl.service.impl;

import com.qcl.entity.AgentUserConfig;
import com.qcl.dao.AgentUserConfigDao;
import com.qcl.service.AgentUserConfigService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * 采集器用户配置表（用户添加）(AgentUserConfig)表服务实现类
 *
 * @author makejava
 * @since 2025-12-17 16:46:56
 */
@Service("agentUserConfigService")
public class AgentUserConfigServiceImpl implements AgentUserConfigService {
    @Resource
    private AgentUserConfigDao agentUserConfigDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public AgentUserConfig queryById(Long id) {
        return this.agentUserConfigDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param agentUserConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<AgentUserConfig> queryByPage(AgentUserConfig agentUserConfig, PageRequest pageRequest) {
        long total = this.agentUserConfigDao.count(agentUserConfig);
        return new PageImpl<>(this.agentUserConfigDao.queryAllByLimit(agentUserConfig, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param agentUserConfig 实例对象
     * @return 实例对象
     */
    @Override
    public AgentUserConfig insert(AgentUserConfig agentUserConfig) {
        this.agentUserConfigDao.insert(agentUserConfig);
        return agentUserConfig;
    }

    /**
     * 修改数据
     *
     * @param agentUserConfig 实例对象
     * @return 实例对象
     */
    @Override
    public AgentUserConfig update(AgentUserConfig agentUserConfig) {
        this.agentUserConfigDao.update(agentUserConfig);
        return this.queryById(agentUserConfig.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.agentUserConfigDao.deleteById(id) > 0;
    }
}
