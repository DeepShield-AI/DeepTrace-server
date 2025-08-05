package com.qcl.controller;


import com.qcl.entity.AgentConfig;
import com.qcl.service.EsAgentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/esAgentConfig")
public class EsAgentConfigController {

    @Autowired
    private EsAgentConfigService esAgentConfigService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<AgentConfig>> search(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                    @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        List<AgentConfig> agentLog = esAgentConfigService.search(keyword, pageNum, pageSize);
        return ResponseEntity.ok(agentLog);
    }
}
