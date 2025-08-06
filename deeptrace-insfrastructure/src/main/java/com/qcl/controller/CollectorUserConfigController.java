package com.qcl.controller;

import com.qcl.entity.CollectorUserConfig;
import com.qcl.service.CollectorUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 采集器配置表（用户添加）(CollectorUserConfig)表控制层
 *
 * @author makejava
 * @since 2025-07-10 10:19:44
 */
@RestController
@RequestMapping("/api/user/config")
public class CollectorUserConfigController {
    /**
     * 服务对象
     */
    @Autowired
    private CollectorUserConfigService collectorUserConfigService;
    @GetMapping("/valid")
    public ResponseEntity<String> queryByPage() {
        return ResponseEntity.ok("success");
    }

    /**
     * 分页查询 http://localhost:8887/api/user/config/queryByPage?pageNumber=0&pageSize=10
     *
     * @param collectorUserConfig 筛选条件
     * @param pageNumber     页码
     * @param pageSize       每页大小
     * @return 查询结果
     */
    @GetMapping("/queryByPage")
    public ResponseEntity<Page<CollectorUserConfig>> queryByPage(CollectorUserConfig collectorUserConfig, @RequestParam(defaultValue = "0") int pageNumber,
                                                                 @RequestParam(defaultValue = "10") int pageSize) {


        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,  Sort.Direction.DESC, "id");
        return ResponseEntity.ok(this.collectorUserConfigService.queryByPage(collectorUserConfig, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<CollectorUserConfig> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.collectorUserConfigService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param collectorUserConfig 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public ResponseEntity<CollectorUserConfig> add(CollectorUserConfig collectorUserConfig) {
        return ResponseEntity.ok(this.collectorUserConfigService.insert(collectorUserConfig));
    }

    /**
     * 编辑数据
     *
     * @param collectorUserConfig 实体
     * @return 编辑结果
     */
    @PutMapping("/edit")
    public ResponseEntity<CollectorUserConfig> edit(CollectorUserConfig collectorUserConfig) {
        return ResponseEntity.ok(this.collectorUserConfigService.update(collectorUserConfig));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.collectorUserConfigService.deleteById(id));
    }

}

