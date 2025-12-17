package com.qcl.service;

import com.qcl.api.Result;
import com.qcl.entity.param.AgentRegisterParam;
import com.qcl.entity.param.agentconfig.AgentParam;

public interface AgentService {

    Result<String> forwardGet(String param);

    Result<String>  registerAgent(AgentRegisterParam param);

    Result<String>  delete(AgentRegisterParam param);

    Result<String> disable(AgentRegisterParam param);

    Result<String> enable(AgentRegisterParam param);

    String editAgentConfig(AgentParam param);
}
