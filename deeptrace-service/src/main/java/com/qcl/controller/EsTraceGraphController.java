package com.qcl.controller;

import com.qcl.entity.AgentManageConfigDTO;
import com.qcl.entity.User;
import com.qcl.entity.UserDTO;
import com.qcl.entity.graph.EdgeStatsResult;
import com.qcl.entity.graph.NodeStatsResult;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.service.AgentManageConfigService;
import com.qcl.service.EsTraceGraphService;
import com.qcl.service.UserService;
import com.qcl.utils.AgentUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/esTracesGraph")
public class EsTraceGraphController {

    @Autowired
    private EsTraceGraphService esTraceGraphService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentManageConfigService agentManageConfigService;


    /**
     * 调用链拓扑图-- 节点及其指标
     * v2:支持用户筛选 List<NodeStatsResult>
     * @param queryTracesParam
     * @return
     */
    @RequestMapping(value = "/nodes", method = RequestMethod.GET)
    public ResponseEntity<?> getContainerStats(QueryTracesParam queryTracesParam, Principal principal) {

        //todo??? 同步当前用户的采集器列表的traces nodes edges
        //获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return  ResponseEntity.badRequest().body(userName+"该用户不存在");
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);

        //同步当前用户的采集器列表的traces nodes edges
        List<AgentManageConfigDTO> agents = AgentUtil.queryAllAgent(agentManageConfigService,user.getUserId());
        AgentUtil.syncData(agents);


        List<NodeStatsResult> nodes = esTraceGraphService.getNodesStats(queryTracesParam, userDTO);
        return ResponseEntity.ok(nodes);
    }

    /**
     * 调用链拓扑图-- 边及其指标
     * v2:支持用户筛选 Map<String,List<EdgeStatsResult>>
     * @param queryTracesParam
     * @return
     */
    @RequestMapping(value = "/edges", method = RequestMethod.GET)
    public ResponseEntity<?> getEdgesStats(QueryTracesParam queryTracesParam, Principal principal) {

        //todo??? 同步当前用户的采集器列表的traces nodes edges
        //获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return  ResponseEntity.badRequest().body(userName+"该用户不存在");
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);

        //同步当前用户的采集器列表的traces nodes edges
        List<AgentManageConfigDTO> agents = AgentUtil.queryAllAgent(agentManageConfigService,user.getUserId());
        AgentUtil.syncData(agents);



        List<EdgeStatsResult> edges = esTraceGraphService.getEdgesStats(queryTracesParam, userDTO);
        Map<String, List<EdgeStatsResult>> result = edges.stream()
                .collect(Collectors.groupingBy(EdgeStatsResult::getSrcNodeId));
        return ResponseEntity.ok(result);
    }

}
