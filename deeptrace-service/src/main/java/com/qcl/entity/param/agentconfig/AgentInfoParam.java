package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 采集器基本信息--必传
 */
@Data
public class AgentInfoParam {
    /**
     * 采集器名字
     */
    @NotEmpty(message = "采集器名称不能为空")
    @JsonProperty("agent_name")
    private String agentName;
    /**
     * 采集器的密码
     */
    @NotEmpty(message = "采集器密码不能为空")
    @JsonProperty("host_password")
    private String hostPassword;
    /**
     * 采集器的IP
     */
    @NotEmpty(message = "采集器IP不能为空")
    @JsonProperty("host_ip")
    private String hostIp;
    /**
     * 采集器的SSH端口
     */
    @NotNull(message = "采集器SSH端口不能为空")
    @JsonProperty("ssh_port")
    private Integer sshPort;
    /**
     * 当前登录用户的用户名
     */
    @NotNull(message = "采集器主机名不能为空")
    @JsonProperty("user_name")
    private String userName;
    /**
     * 当前登录用户的id
     */
    @JsonProperty("user_id")
    private String userId;

    // Constructors
    public AgentInfoParam() {}

    // Getters and Setters
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
