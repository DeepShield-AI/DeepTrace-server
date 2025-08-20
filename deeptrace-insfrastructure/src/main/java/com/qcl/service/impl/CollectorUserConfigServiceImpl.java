package com.qcl.service.impl;

import com.qcl.dao.CollectorUserConfigDao;
import com.qcl.entity.CollectorUserConfig;
import com.qcl.service.CollectorUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 采集器配置表（用户添加）服务实现类
 *
 * @author makejava
 * @since 2025-07-10 10:20:25
 */
@Service("collectorUserConfigService")
public class CollectorUserConfigServiceImpl implements CollectorUserConfigService {

    @Autowired
    private CollectorUserConfigDao collectorUserConfigDao;

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CollectorUserConfig queryById(Long id) {
        return collectorUserConfigDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param collectorUserConfig 查询条件
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    @Override
    public Page<CollectorUserConfig> queryByPage(CollectorUserConfig collectorUserConfig, PageRequest pageRequest) {
        long total = collectorUserConfigDao.count(collectorUserConfig);
        if (total == 0) {
            return new PageImpl<>(List.of(), pageRequest, total);
        }
        return new PageImpl<>(collectorUserConfigDao.queryAllByLimit(collectorUserConfig, pageRequest), pageRequest, total);
    }

    /**
     * 新增配置
     *
     * @param config 配置对象
     * @return 新增后的对象
     */
    @Override
    public CollectorUserConfig insert(CollectorUserConfig config) {
        config.setConfigId("CONFIG" + System.currentTimeMillis());
        config.setGroupId(config.getGroupName());
        collectorUserConfigDao.insert(config);
        return config;
    }

    /**
     * 编辑配置
     *
     * @param collectorUserConfig 配置对象
     * @return 更新后的对象
     */
    @Override
    public CollectorUserConfig update(CollectorUserConfig collectorUserConfig) {
        collectorUserConfigDao.update(collectorUserConfig);
        return queryById(collectorUserConfig.getId());
    }

    /**
     * 删除配置
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return collectorUserConfigDao.deleteById(id) > 0;
    }
}