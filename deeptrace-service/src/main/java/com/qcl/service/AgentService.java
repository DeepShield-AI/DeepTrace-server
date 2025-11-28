package com.qcl.service;

import com.qcl.vo.Result;
import org.springframework.http.ResponseEntity;

public interface AgentService {

    Result<String> forwardGet(String param);
}
