package com.qcl.service;

import com.qcl.entity.CollectorUserConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 采集器配置表（用户添加）(CollectorUserConfig)表服务接口
 *
 * @author makejava
 * @since 2025-07-10 10:19:49
 */
public interface CollectorUserConfigService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CollectorUserConfig queryById(Integer id);

    /**
     * 分页查询
     *
     * @param collectorUserConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<CollectorUserConfig> queryByPage(CollectorUserConfig collectorUserConfig, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param collectorUserConfig 实例对象
     * @return 实例对象
     */
    CollectorUserConfig insert(CollectorUserConfig collectorUserConfig);

    /**
     * 修改数据
     *
     * @param collectorUserConfig 实例对象
     * @return 实例对象
     */
    CollectorUserConfig update(CollectorUserConfig collectorUserConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
