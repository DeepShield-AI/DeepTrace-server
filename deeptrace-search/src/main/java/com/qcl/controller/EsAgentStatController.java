package com.qcl.controller;

import com.qcl.entity.AgentLog;
import com.qcl.entity.AgentStat;
import com.qcl.service.EsAgentLogService;
import com.qcl.service.EsAgentStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/esAgentStat")
public class EsAgentStatController {
    @Autowired
    private EsAgentStatService esAgentStatService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<AgentStat>> search(@RequestParam(required = false) String agentName) {
        List<AgentStat> agentLog = esAgentStatService.search(agentName);
        return ResponseEntity.ok(agentLog);
    }
}
