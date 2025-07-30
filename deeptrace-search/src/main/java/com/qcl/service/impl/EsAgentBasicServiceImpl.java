package com.qcl.service.impl;

import com.qcl.entity.AgentBasic;
import com.qcl.repository.EsAgentBasicRepository;
import com.qcl.result.PageResult;
import com.qcl.service.EsAgentBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EsAgentBasicServiceImpl implements EsAgentBasicService {
    private static final Logger log = LoggerFactory.getLogger(EsAgentBasicServiceImpl.class);

    @Autowired
    private EsAgentBasicRepository esAgentBasicRepository;

    @Override
    public PageResult<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize) {
        // 所有数据
        List<AgentBasic> allList = StreamSupport.stream(esAgentBasicRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // 关键字过滤
        if (keyword != null && !keyword.isEmpty()) {
            allList = allList.stream()
                    .filter(agent -> agent.getName() != null && agent.getName().contains(keyword))
                    .collect(Collectors.toList());
        }

        // 分页参数校验与默认值
        int safePageNum = (pageNum == null || pageNum < 0) ? 0 : pageNum;
        int safePageSize = (pageSize == null || pageSize <= 0) ? 5 : pageSize;

        int total = allList.size();
        int fromIndex = safePageNum * safePageSize;
        int toIndex = Math.min(fromIndex + safePageSize, total);

        // 边界判断，超出范围返回空数据
        List<AgentBasic> pageList = (fromIndex >= total) ? Collections.emptyList()
                : allList.subList(fromIndex, toIndex);

        // 封装分页结果
        PageResult<AgentBasic> pageResult = new PageResult<>();
        pageResult.setList(pageList);
        pageResult.setTotal(total);
        pageResult.setPageNum(safePageNum);
        pageResult.setPageSize(safePageSize);
        
        // log
        log.info("分页结果: {}", pageResult);
        return pageResult;
    }
}
