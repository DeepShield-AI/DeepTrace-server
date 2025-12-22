package com.qcl.service;

import com.qcl.api.Result;
import com.qcl.entity.param.AgentRegisterParam;

public interface AgentService {

    Result<String> forwardGet(String param);

    String registerAgent(AgentRegisterParam param);

    String delete(AgentRegisterParam param);

    String disable(AgentRegisterParam param);

    String enable(AgentRegisterParam param);
}
