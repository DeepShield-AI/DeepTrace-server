package com.qcl.controller;

import com.qcl.service.EsAgentStatService;
import com.qcl.dto.StatItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/esAgentStat")
public class EsAgentStatController {
    @Autowired
    private EsAgentStatService esAgentStatService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, List<StatItem>>> search(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String agentName) {
        Map<String, List<StatItem>> statItems = esAgentStatService.searchStatItems(startTime, endTime, agentName);
        return ResponseEntity.ok(statItems);
    }
}