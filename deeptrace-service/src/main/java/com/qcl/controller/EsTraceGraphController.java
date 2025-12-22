package com.qcl.controller;

import com.qcl.entity.graph.EdgeStatsResult;
import com.qcl.entity.graph.NodeStatsResult;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.service.EsTraceGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/esTracesGraph")
public class EsTraceGraphController {

    @Autowired
    private EsTraceGraphService esTraceGraphService;

    @RequestMapping(value = "/nodes", method = RequestMethod.GET)
    public ResponseEntity<List<NodeStatsResult>> getContainerStats(QueryTracesParam queryTracesParam) {
        List<NodeStatsResult> nodes = esTraceGraphService.getNodesStats(queryTracesParam);
        return ResponseEntity.ok(nodes);
    }
    @RequestMapping(value = "/edges", method = RequestMethod.GET)
    public ResponseEntity<Map<String,List<EdgeStatsResult>>> getEdgesStats(QueryTracesParam queryTracesParam) {
        List<EdgeStatsResult> edges = esTraceGraphService.getEdgesStats(queryTracesParam);
        Map<String, List<EdgeStatsResult>> result = edges.stream()
                .collect(Collectors.groupingBy(EdgeStatsResult::getSrcNodeId));
        return ResponseEntity.ok(result);
    }
    @RequestMapping(value = "/traces", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getTraces(QueryTracesParam queryTracesParam) {
        return null;
    }
}
