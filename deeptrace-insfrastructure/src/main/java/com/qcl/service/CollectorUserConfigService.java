package com.qcl.service;

import com.qcl.entity.CollectorUserConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 采集器配置服务接口
 */
public interface CollectorUserConfigService {

    /**
     * 根据主键查询
     * @param id 主键
     * @return 配置对象
     */
    CollectorUserConfig queryById(Long id);

    /**
     * 分页条件查询
     * @param collectorUserConfig 查询条件
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    Page<CollectorUserConfig> queryByPage(CollectorUserConfig collectorUserConfig, PageRequest pageRequest);

    /**
     * 新增配置
     * @param collectorUserConfig 配置对象
     * @return 新增后的对象
     */
    CollectorUserConfig insert(CollectorUserConfig collectorUserConfig);

    /**
     * 编辑配置
     * @param collectorUserConfig 配置对象
     * @return 更新后的对象
     */
    CollectorUserConfig update(CollectorUserConfig collectorUserConfig);

    /**
     * 删除配置
     * @param id 主键
     * @return 删除是否成功
     */
    boolean deleteById(Long id);
}