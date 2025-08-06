package com.qcl.controller;

import com.qcl.entity.AgentBasic;
import com.qcl.service.EsAgentBasicService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/esAgentBasic")
public class EsAgentBasicController {
    @Autowired
    private EsAgentBasicService esAgentBasicService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<PageResult<AgentBasic>> search(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<AgentBasic> agentPage = esAgentBasicService.search(keyword, pageNum, pageSize);
        PageResult<AgentBasic> result = new PageResult<>(
                agentPage.getContent(),
                agentPage.getNumber(),
                agentPage.getSize(),
                agentPage.getTotalElements(),
                agentPage.getTotalPages()
        );
        return ResponseEntity.ok(result);
    }
}