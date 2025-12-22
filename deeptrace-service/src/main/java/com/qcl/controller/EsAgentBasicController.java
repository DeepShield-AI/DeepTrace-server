package com.qcl.controller;

import com.alibaba.druid.util.StringUtils;
import com.qcl.api.Result;
import com.qcl.constants.UserRoleEnum;
import com.qcl.entity.AgentBasic;
import com.qcl.entity.User;
import com.qcl.service.EsAgentBasicService;
import com.qcl.service.UserService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/api/esAgentBasic")
public class EsAgentBasicController {
    @Autowired
    private EsAgentBasicService esAgentBasicService;
    @Autowired
    private UserService userService;

    // 分页查询
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> search(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                    Principal principal) {
        //获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return  ResponseEntity.badRequest().body(userName+"该用户不存在");
        }

        Page<AgentBasic> page = esAgentBasicService.search(user, keyword, pageNum, pageSize);
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