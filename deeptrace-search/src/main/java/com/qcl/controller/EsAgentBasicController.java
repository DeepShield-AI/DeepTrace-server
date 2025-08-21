package com.qcl.controller;

import com.qcl.entity.AgentBasic;
import com.qcl.service.EsAgentBasicService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/esAgentBasic")
public class EsAgentBasicController {
    @Autowired
    private EsAgentBasicService esAgentBasicService;

    // 分页查询
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<PageResult<AgentBasic>> search(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<AgentBasic> page = esAgentBasicService.search(keyword, pageNum, pageSize);
        PageResult<AgentBasic> result = new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ResponseEntity.ok(result);
    }

    // 根据 lcuuid 查询单条详情
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResponseEntity<AgentBasic> detail(@RequestParam String lcuuid) {
        AgentBasic agent = esAgentBasicService.findByLcuuid(lcuuid);
        return ResponseEntity.ok(agent);
    }
}