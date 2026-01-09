package com.qcl.controller;

import com.qcl.entity.Edges;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.User;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryEdgeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsEdgeService;
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

/**
 * 路径分析对外API
 */
@Controller
@RequestMapping("/api/esEdges")
public class EsEdgesController {

    @Autowired
    private EsEdgeService esEdgeService;
    @Autowired
    private UserService userService;


    /**
     * 路径分析--调用日志查询
     * v2:支持用户筛选 PageResult<Edges>
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/log/queryByPage", method = RequestMethod.GET)
    public ResponseEntity<?> search(QueryEdgeParam queryEdgeParam, Principal principal) {

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

        PageResult<Edges> result = esEdgeService.queryByPage(queryEdgeParam, userDTO );

        return ResponseEntity.ok(result);
    }

    /**
     * 路径分析-调用日志分组
     * v2:支持用户筛选
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/statistic/status", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryEdgeParam queryEdgeParam, Principal principal) {

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


        List<StatusTimeBucketResult> statusResult = esEdgeService.getStatusCountByMinute(queryEdgeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }


    /**
     * 路径分析端点列表
     * v2:支持用户筛选 List<EndpointProtocolStatsResult>
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/queryEndpoint", method = RequestMethod.GET)
    public ResponseEntity<?> getEndpointProtocolStats(QueryEdgeParam queryEdgeParam, Principal principal) {

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

        List<EndpointProtocolStatsResult> results = esEdgeService.getEndpointProtocolStats(queryEdgeParam, userDTO);
        return ResponseEntity.ok(results);
    }





    /**
     * 按分钟统计请求个数，每秒XX个
     * v2:支持用户筛选
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/kpi/qps", method = RequestMethod.GET)
    public ResponseEntity<?> qpsByMinute(@ModelAttribute QueryEdgeParam queryEdgeParam , Principal principal) {

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

        List<TimeBucketResult> statusResult = esEdgeService.qpsByMinute(queryEdgeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计异常比例
     * v2:支持用户筛选
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/kpi/errorRate", method = RequestMethod.GET)
    public ResponseEntity<?> errorRateByMinute(@ModelAttribute  QueryEdgeParam queryEdgeParam, Principal principal) {

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

        List<TimeBucketResult> statusResult = esEdgeService.errorRateByMinute(queryEdgeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计响应时延
     * v2:支持用户筛选
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/kpi/latencyStats", method = RequestMethod.GET)
    public ResponseEntity<?> latencyStatsByMinute(@ModelAttribute  QueryEdgeParam queryEdgeParam, Principal principal) {

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

        List<TimeBucketResult> statusResult = esEdgeService.latencyStatsByMinute(queryEdgeParam, userDTO);
        return ResponseEntity.ok(statusResult);
    }



}
