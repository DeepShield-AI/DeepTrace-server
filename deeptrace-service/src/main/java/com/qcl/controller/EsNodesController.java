package com.qcl.controller;

import com.qcl.entity.User;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryNodeParam;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsNodeService;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.service.UserService;
import com.qcl.vo.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/esNodes")
public class EsNodesController {

    @Autowired
    private EsNodeService esNodeServices;
    @Autowired
    private UserService userService;


    /**
     * 资源分析 —— 调用日志查询
     * v2:支持用户筛选
     * @param queryNodeParam
     * @return PageResult<Nodes>
     */
    @RequestMapping(value = "/log/queryByPage", method = RequestMethod.GET)
    public ResponseEntity<?> search(QueryNodeParam queryNodeParam, Principal principal) {
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

        PageResult<Nodes> result = esNodeServices.queryByPage(queryNodeParam, userDTO);

        return ResponseEntity.ok(result);
    }


    /**
     * 资源分析 —— 调用日志分组
     * v2:支持用户筛选
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/statistic/status", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryNodeParam queryNodeParam, Principal principal) {
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

        List<StatusTimeBucketResult> statusResult = esNodeServices.getStatusCountByMinute(queryNodeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }


    /**
     * 资源分析 ——  端点列表（全量数据）
     * v2:支持用户筛选
     * @param queryNodeParam
     * @return List<EndpointProtocolStatsResult>
     */
    @RequestMapping(value = "/queryEndpoint", method = RequestMethod.GET)
    public ResponseEntity<?> getEndpointProtocolStats(QueryNodeParam queryNodeParam, Principal principal) {
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

        List<EndpointProtocolStatsResult> results = esNodeServices.getEndpointProtocolStats(queryNodeParam, userDTO);
        return ResponseEntity.ok(results);
    }




    /**
     * 资源分析 —— 按分钟统计请求个数，每秒XX个
     * v2:支持用户筛选
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/kpi/qps", method = RequestMethod.GET)
    public ResponseEntity<?> qpsByMinute(@ModelAttribute QueryNodeParam queryNodeParam, Principal principal) {
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

        List<TimeBucketResult> statusResult = esNodeServices.qpsByMinute(queryNodeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 资源分析 —— 按分钟统计异常比例
     * v2:支持用户筛选
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/kpi/errorRate", method = RequestMethod.GET)
    public ResponseEntity<?> errorRateByMinute(@ModelAttribute QueryNodeParam queryNodeParam, Principal principal) {
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


        List<TimeBucketResult> statusResult = esNodeServices.errorRateByMinute(queryNodeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 资源分析 —— 按分钟统计响应时延
     * v2:支持用户筛选
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/kpi/latencyStats", method = RequestMethod.GET)
    public ResponseEntity<?> latencyStatsByMinute(@ModelAttribute QueryNodeParam queryNodeParam, Principal principal) {
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

        List<TimeBucketResult> statusResult = esNodeServices.latencyStatsByMinute(queryNodeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }


}
