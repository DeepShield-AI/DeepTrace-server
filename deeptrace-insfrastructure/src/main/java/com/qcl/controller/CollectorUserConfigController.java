/*
package com.qcl.controller;

import com.qcl.entity.CollectorUserConfig;
import com.qcl.service.CollectorUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

*/
/**
 * 采集器配置表（用户添加）控制层
 *
 * @author makejava
 * @since 2025-07-10 10:19:44
 *//*

@RestController
@RequestMapping("/api/user/config")
public class CollectorUserConfigController {

    @Autowired
    private CollectorUserConfigService collectorUserConfigService;

    */
/**
     * 校验接口
     *//*

//    @GetMapping("/valid")
//    public ResponseEntity<String> valid() {
//        return ResponseEntity.ok("success");
//    }

    */
/**
     * 分页查询
     * 示例: GET /api/user/config/queryByPage?pageNumber=0&pageSize=10
     *
     * @param collectorUserConfig 查询条件
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @return 分页结果
     *//*

    @GetMapping("/queryByPage")
    public ResponseEntity<Page<CollectorUserConfig>> queryByPage(
            CollectorUserConfig collectorUserConfig,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "id");
        return ResponseEntity.ok(collectorUserConfigService.queryByPage(collectorUserConfig, pageRequest));
    }

    */
/**
     * 根据主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     *//*

    @GetMapping("{id}")
    public ResponseEntity<CollectorUserConfig> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(collectorUserConfigService.queryById(id));
    }

    */
/**
     * 新增数据
     *
     * @param collectorUserConfig 实体
     * @return 新增结果
     *//*

    @PostMapping("/add")
    public ResponseEntity<CollectorUserConfig> add(@RequestBody @Valid CollectorUserConfig collectorUserConfig) {
        return ResponseEntity.ok(collectorUserConfigService.insert(collectorUserConfig));
    }

    */
/**
     * 编辑数据
     *
     * @param collectorUserConfig 实体
     * @return 编辑结果
     *//*

    @PutMapping("/edit")
    public ResponseEntity<CollectorUserConfig> edit(@RequestBody @Valid CollectorUserConfig collectorUserConfig) {
        return ResponseEntity.ok(collectorUserConfigService.update(collectorUserConfig));
    }

    */
/**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     *//*

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(collectorUserConfigService.deleteById(id));
    }
}*/
