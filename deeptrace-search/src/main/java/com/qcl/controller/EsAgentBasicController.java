package com.qcl.controller;


import com.qcl.entity.AgentBasic;
import com.qcl.service.EsAgentBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/esAgentBasic")
public class EsAgentBasicController {
    @Autowired
    private EsAgentBasicService esAgentBasicService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<AgentBasic>> search(@RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        List<AgentBasic> agentLog = esAgentBasicService.search(keyword, pageNum, pageSize);
        return ResponseEntity.ok(agentLog);
    }
}
