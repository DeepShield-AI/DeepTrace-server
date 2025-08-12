package com.qcl.dao;

import com.qcl.entity.CollectorUserConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 采集器配置表（用户添加）(CollectorUserConfig)表数据库访问层
 *
 * @author makejava
 * @since 2025-07-10 10:19:47
 */
@Mapper
public interface CollectorUserConfigDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CollectorUserConfig queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<CollectorUserConfig> queryAllByLimit(@Param("config") CollectorUserConfig config, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param collectorUserConfig 查询条件
     * @return 总行数
     */
    long count(CollectorUserConfig collectorUserConfig);

    /**
     * 新增数据
     *
     * @param collectorUserConfig 实例对象
     * @return 影响行数
     */
    int insert(CollectorUserConfig collectorUserConfig);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<CollectorUserConfig> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<CollectorUserConfig> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<CollectorUserConfig> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<CollectorUserConfig> entities);

    /**
     * 修改数据
     *
     * @param collectorUserConfig 实例对象
     * @return 影响行数
     */
    int update(CollectorUserConfig collectorUserConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}