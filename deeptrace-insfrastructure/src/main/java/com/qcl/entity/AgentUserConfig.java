package com.qcl.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 采集器用户配置表（用户添加）(AgentUserConfig)实体类
 *
 * @author makejava
 * @since 2025-12-17 16:07:46
 */
public class AgentUserConfig implements Serializable {
    private static final long serialVersionUID = -88696319396196831L;
/**
     * 主键ID
     */
    private Long id;
/**
     * 采集器IP地址
     */
    private String hostIp;
/**
     * 采集器名称
     */
    private String agentName;
/**
     * 用户ID
     */
    private String userId;
/**
     * 用户完整配置
     */
    private String config;
/**
     * 创建时间
     */
    private Date createTime;
/**
     * 更新时间
     */
    private Date updatedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}

