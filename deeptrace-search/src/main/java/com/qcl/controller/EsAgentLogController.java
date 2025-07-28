package com.qcl.controller;


import com.qcl.entity.AgentLog;
import com.qcl.service.EsAgentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/esAgentLog")
public class EsAgentLogController {

    @Autowired
    private EsAgentLogService esAgentLogService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<AgentLog>> search(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                             @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        List<AgentLog> agentLog = esAgentLogService.search(keyword, pageNum, pageSize);
        return ResponseEntity.ok(agentLog);
    }
}
