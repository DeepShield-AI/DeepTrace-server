package com.qcl.controller;

import com.alibaba.druid.util.StringUtils;
import com.qcl.api.Result;
import com.qcl.entity.AgentUserConfig;
import com.qcl.entity.User;
import com.qcl.entity.UserDTO;
import com.qcl.service.AgentUserConfigService;
import com.qcl.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;

/**
 * 采集器用户配置表（用户添加）(AgentUserConfig)表控制层
 *
 * @author makejava
 * @since 2025-12-17 16:07:46
 */
@RestController
@RequestMapping("/api/agent/user/config")
public class AgentUserConfigController {
    /**
     * 服务对象
     */
    @Resource
    private AgentUserConfigService agentUserConfigService;
    @Autowired
    private UserService userService;

    /**
     * 查询用户添加的配置
     * v2:支持用户筛选  Page<AgentUserConfig>
     * @param agentUserConfig
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/queryByPage")
    public Result<?> queryByPage(
            AgentUserConfig agentUserConfig,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Principal principal) {

        //获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return Result.error("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return  Result.error(userName+"该用户不存在");
        }
        //用户为管理员，则选择一个用户
        if (StringUtils.equalsIgnoreCase(user.getRole(),"admin") && StringUtils.isEmpty(agentUserConfig.getUserId())){
            return Result.error("请选择用户");
        }
        //普通用户，进筛选当前用户的数据
        agentUserConfig.setUserId(user.getUserId().toString());

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "id");
        return Result.success(agentUserConfigService.queryByPage(agentUserConfig, pageRequest));
    }


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    /*@GetMapping("{id}")
    public ResponseEntity<AgentUserConfig> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.agentUserConfigService.queryById(id));
    }
*/
    /**
     * 新增数据
     *
     * @param agentUserConfig 实体
     * @return 新增结果
     */
    /*@RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<AgentUserConfig> add(AgentUserConfig agentUserConfig, Principal principal) {
        String userName = principal.getName();
        if (userName == null){
            return Result.unauthorized(null);
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return Result.error(userName+"该用户不存在");
        }
        agentUserConfig.setUserId(user.getUserId().toString());
        return Result.success(this.agentUserConfigService.insert(agentUserConfig));
    }*/

    /**
     * 编辑数据
     *
     * @param agentUserConfig 实体
     * @return 编辑结果
     */
    /*@PutMapping
    public ResponseEntity<AgentUserConfig> edit(AgentUserConfig agentUserConfig) {
        return ResponseEntity.ok(this.agentUserConfigService.update(agentUserConfig));
    }
*/
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    /*@DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.agentUserConfigService.deleteById(id));
    }*/

}

