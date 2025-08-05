package com.qcl.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 采集器配置表（用户添加）(CollectorUserConfig)实体类
 *
 * @author makejava
 * @since 2025-07-10 10:19:48
 */
@Setter
@Getter
public class CollectorUserConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 827792852025052156L;
/**
     * 主键ID
     */
    private Integer id;
/**
     * 配置ID
     */
    private String configId;
/**
     * 采集器组ID
     */
    private String groupId;
/**
     * 采集器组名称
     */
    private String groupName;
/**
     * 所属团队
     */
    private String teamName;
/**
     *  CPU限制(Core)
     */
    private Integer maxCpus;
/**
     * 内存限制(MB)
     */
    private Integer maxMemory;

    private Date createdAt;

    private Date updatedAt;
    /**
     * 采集网口
     */
    private String collectionPort;


}

