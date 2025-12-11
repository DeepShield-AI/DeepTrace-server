package com.qcl.entity.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AgentRegisterParam {
    @NotEmpty(message = "IP不能为空")
    private String hostIp;

    @NotEmpty(message = "密码不能为空")
    private String hostPassword;

    @NotNull(message = "SSH端口不能为空")
    private Integer sshPort;

    @NotEmpty(message = "名称不能为空")
    private String agentName;

    private String userName;
    private String userId;

    // getters and setters
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

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
