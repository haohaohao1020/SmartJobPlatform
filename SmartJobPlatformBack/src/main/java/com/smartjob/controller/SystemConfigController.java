package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.SystemConfig;
import com.smartjob.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SystemConfigController {
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @GetMapping("/configs")
    public Result<Map<String, Object>> getAllConfigs() {
        Map<String, Object> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }
    
    @GetMapping("/configs/all")
    public Result<List<SystemConfig>> getAllConfigList() {
        List<SystemConfig> configs = systemConfigService.list();
        return Result.success(configs);
    }
    
    @GetMapping("/admin/configs")
    public Result<PageResult<SystemConfig>> getAdminConfigs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String group) {
        
        Page<SystemConfig> configPage = systemConfigService.getConfigPage(page, pageSize, keyword, group);
        
        PageResult<SystemConfig> result = PageResult.of(
                configPage.getRecords(),
                configPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PostMapping("/admin/configs")
    public Result<SystemConfig> createConfig(@RequestBody SystemConfig config) {
        systemConfigService.save(config);
        return Result.success("创建成功", config);
    }
    
    @PutMapping("/admin/configs/{id}")
    public Result<SystemConfig> updateConfig(
            @PathVariable Long id,
            @RequestBody SystemConfig config) {
        config.setId(id);
        systemConfigService.updateById(config);
        return Result.success("更新成功", config);
    }
    
    @DeleteMapping("/admin/configs/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        systemConfigService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    @PutMapping("/admin/configs/{id}/status")
    public Result<Void> updateConfigStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        systemConfigService.toggleConfigStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    @PostMapping("/admin/configs/update-all")
    public Result<Void> updateAllConfigs(@RequestBody Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            systemConfigService.updateConfig(entry.getKey(), entry.getValue());
        }
        return Result.success("配置更新成功", null);
    }
    
    @GetMapping("/admin/configs/{id}")
    public Result<SystemConfig> getAdminConfigDetail(@PathVariable Long id) {
        SystemConfig config = systemConfigService.getById(id);
        return Result.success(config);
    }
    
    @GetMapping("/admin/settings")
    public Result<Map<String, Object>> getAdminSettings() {
        Map<String, Object> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }
    
    @PutMapping("/admin/settings")
    public Result<Void> updateAdminSettings(@RequestBody Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            systemConfigService.updateConfig(entry.getKey(), entry.getValue());
        }
        return Result.success("更新成功", null);
    }
}
