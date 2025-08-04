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
 * 采集器配置表（用户添加）(CollectorUserConfig)表服务实现类
 *
 * @author makejava
 * @since 2025-07-10 10:20:25
 */
@Service("collectorUserConfigService")
public class CollectorUserConfigServiceImpl implements CollectorUserConfigService {
    @Autowired
    private CollectorUserConfigDao collectorUserConfigDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CollectorUserConfig queryById(Integer id) {
        return this.collectorUserConfigDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param collectorUserConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<CollectorUserConfig> queryByPage(CollectorUserConfig collectorUserConfig, PageRequest pageRequest) {
        long total = this.collectorUserConfigDao.count(collectorUserConfig);
        if (total==0){
            return new PageImpl<>(List.of(new CollectorUserConfig()), pageRequest, total);
        }
        return new PageImpl<>(this.collectorUserConfigDao.queryAllByLimit(collectorUserConfig, pageRequest), pageRequest, total);
    }
    /**
     * 新增数据
     *
     * @param config 实例对象
     * @return 实例对象
     */
    @Override
    public CollectorUserConfig insert(CollectorUserConfig config) {
        config.setConfigId("CONFIG"+System.currentTimeMillis());
        config.setGroupId(config.getGroupName());
        this.collectorUserConfigDao.insert(config);
        return config;
    }

    /**
     * 修改数据
     *
     * @param collectorUserConfig 实例对象
     * @return 实例对象
     */
    @Override
    public CollectorUserConfig update(CollectorUserConfig collectorUserConfig) {
        this.collectorUserConfigDao.update(collectorUserConfig);
        return this.queryById(collectorUserConfig.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.collectorUserConfigDao.deleteById(id) > 0;
    }
}
