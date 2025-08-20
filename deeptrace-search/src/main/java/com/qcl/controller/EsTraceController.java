package com.qcl.controller;

import com.qcl.entity.AgentStat;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.service.EsTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/esTraces")
public class EsTraceController {

    @Autowired
    private EsTraceService esTraceService;
    @RequestMapping(value = "/queryByPage", method = RequestMethod.GET)
    public ResponseEntity< List<Traces>> search(QueryTracesParam queryTracesParam) {
        List<Traces> traces = esTraceService.queryByPage(queryTracesParam);
        return ResponseEntity.ok(traces);
    }
}
