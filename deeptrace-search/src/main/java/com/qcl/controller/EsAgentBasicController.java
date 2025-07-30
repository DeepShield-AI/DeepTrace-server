package com.qcl.controller;

import com.qcl.entity.AgentBasic;
import com.qcl.result.PageResult;
import com.qcl.service.EsAgentBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esAgentBasic")
public class EsAgentBasicController {
    @Autowired
    private EsAgentBasicService esAgentBasicService;

    @GetMapping("/search")
    public PageResult<AgentBasic> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return esAgentBasicService.search(keyword, pageNum, pageSize);
    }
}