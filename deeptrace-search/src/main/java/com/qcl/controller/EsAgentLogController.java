package com.qcl.controller;

import com.qcl.entity.AgentLog;
import com.qcl.service.EsAgentLogService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/esAgentLog")
public class EsAgentLogController {

    @Autowired
    private EsAgentLogService esAgentLogService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<PageResult<AgentLog>> search(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                       @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<AgentLog> page = esAgentLogService.search(keyword, pageNum, pageSize);
        PageResult<AgentLog> result = new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ResponseEntity.ok(result);
    }
}