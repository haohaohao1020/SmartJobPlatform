package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.mapper.SystemConfigMapper;
import com.smartjob.model.SystemConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemConfigService extends ServiceImpl<SystemConfigMapper, SystemConfig> {
    
    public Map<String, Object> getAllConfigs() {
        Map<String, Object> configMap = new HashMap<>();
        List<SystemConfig> configs = this.list();
        for (SystemConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }
    
    public Page<SystemConfig> getConfigPage(Integer page, Integer pageSize, String keyword, String group) {
        Page<SystemConfig> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SystemConfig::getConfigKey, keyword)
                    .or().like(SystemConfig::getConfigName, keyword);
        }
        
        if (StringUtils.hasText(group) && !"全部".equals(group)) {
            wrapper.eq(SystemConfig::getConfigGroup, group);
        }
        
        wrapper.orderByAsc(SystemConfig::getConfigGroup)
                .orderByAsc(SystemConfig::getSort);
        
        return this.page(pageParam, wrapper);
    }
    
    public void updateConfig(String key, String value) {
        SystemConfig config = this.getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, key));
        if (config != null) {
            config.setConfigValue(value);
            this.updateById(config);
        }
    }
    
    public void toggleConfigStatus(Long id, Integer status) {
        SystemConfig config = this.getById(id);
        if (config != null) {
            config.setStatus(status);
            this.updateById(config);
        }
    }
}
