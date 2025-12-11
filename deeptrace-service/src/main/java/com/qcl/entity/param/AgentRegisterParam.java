package com.qcl.entity.param;

import jakarta.validation.constraints.NotEmpty;

public class AgentRegisterParam {
    @NotEmpty
    private String hostIp;

    @NotEmpty
    private String hostPassword;
    @NotEmpty
    private Integer sshPort;
    @NotEmpty
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
