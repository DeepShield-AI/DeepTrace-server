package com.qcl.service;

import org.springframework.http.ResponseEntity;

public interface AgentService {

    ResponseEntity<String> forwardGet(String param);
}
