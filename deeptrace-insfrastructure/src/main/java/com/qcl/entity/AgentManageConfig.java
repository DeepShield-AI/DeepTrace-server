package com.qcl.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 采集器管理信息表（注册、启用、禁用、删除等）(AgentManageConfig)实体类
 *
 * @author makejava
 * @since 2025-12-17 13:59:05
 */
public class AgentManageConfig implements Serializable {
    private static final long serialVersionUID = 984388986276762433L;
/**
     * 主键ID
     */
    private Long id;
/**
     * 管理类型 register：注册  enable：启用  disable：禁用  delete：删除
     */
    private String type;
/**
     * 采集器IP地址
     */
    private String hostIp;
/**
     * 采集器密码
     */
    private String hostPassword;
/**
     * SSH端口
     */
    private Integer sshPort;
/**
     * 采集器名称
     */
    private String agentName;
/**
     * 用户名
     */
    private String userName;
/**
     * 用户ID
     */
    private String userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

