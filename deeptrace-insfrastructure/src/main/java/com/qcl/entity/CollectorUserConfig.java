package com.qcl.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 采集器配置表（用户添加）(CollectorUserConfig)实体类
 *
 * @author makejava
 * @since 2025-07-10 10:19:48
 */
public class CollectorUserConfig implements Serializable {
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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getMaxCpus() {
        return maxCpus;
    }

    public void setMaxCpus(Integer maxCpus) {
        this.maxCpus = maxCpus;
    }

    public Integer getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Integer maxMemory) {
        this.maxMemory = maxMemory;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getCollectionPort() {
        return collectionPort;
    }

    public void setCollectionPort(String collectionPort) {
        this.collectionPort = collectionPort;
    }
}

