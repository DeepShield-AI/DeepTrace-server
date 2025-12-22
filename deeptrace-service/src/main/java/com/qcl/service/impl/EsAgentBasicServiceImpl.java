package com.qcl.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.qcl.constants.UserRoleEnum;
import com.qcl.entity.AgentBasic;
import com.qcl.entity.User;
import com.qcl.repository.EsAgentBasicRepository;
import com.qcl.service.EsAgentBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EsAgentBasicServiceImpl implements EsAgentBasicService {
    @Autowired
    private EsAgentBasicRepository esAgentBasicRepository;

    // 分页查询
    /**
     * v2: 普通用户查询自己的agent，管理员用户查询所有的agent
     * @param user  当前登录用户
     * @param keyword agent的名称
     * @param pageNum 当前页码
     * @param pageSize 每页显示的条数
     * @return
     */
    @Override
    public Page<AgentBasic> search(User user, String keyword, Integer pageNum, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Long userId = user.getUserId();
        //管理员用户查询所有agent
        if (StringUtils.equalsIgnoreCase(user.getRole(), UserRoleEnum.ADMIN.getCode()) ){
            if (keyword == null || keyword.isEmpty()) {
                return esAgentBasicRepository.findAll(pageRequest);
            }
           return esAgentBasicRepository.findByNameContaining(keyword, pageRequest);
        }else {
            //普通用户获取自己的agent
            if (keyword == null || keyword.isEmpty()) {
                return esAgentBasicRepository.findByUserId(userId.toString(), pageRequest);
            }
            return esAgentBasicRepository.findByNameContainingAndUserIdEquals(keyword, userId.toString(), pageRequest);
        }
    }

    // 根据 lcuuid 查询单条详情
    @Override
    public AgentBasic findByLcuuid(String lcuuid) {
        return esAgentBasicRepository.findById(lcuuid).orElse(null);
    }
}