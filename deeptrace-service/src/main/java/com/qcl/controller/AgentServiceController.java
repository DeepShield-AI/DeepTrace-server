package com.qcl.controller;

import com.qcl.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/agent")
public class AgentServiceController {

    @Autowired
    private AgentService agentService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    ResponseEntity<String> forwardGet(String param){
        return agentService.forwardGet(param);
    }
}
