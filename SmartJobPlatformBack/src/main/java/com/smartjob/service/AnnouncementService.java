package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.mapper.AnnouncementMapper;
import com.smartjob.model.Announcement;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AnnouncementService extends ServiceImpl<AnnouncementMapper, Announcement> {
    
    public Page<Announcement> getAnnouncementPage(Integer page, Integer pageSize, 
                                                   String keyword, Integer type) {
        Page<Announcement> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Announcement::getStatus, 1);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getId);
        
        return this.page(pageParam, wrapper);
    }
    
    public Page<Announcement> getAnnouncementPageForAdmin(Integer page, Integer pageSize, 
                                                           String keyword, Integer type,
                                                           Integer status) {
        Page<Announcement> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        
        wrapper.orderByDesc(Announcement::getId);
        
        return this.page(pageParam, wrapper);
    }
    
    public Announcement getAnnouncementDetail(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement != null) {
            this.update(new LambdaUpdateWrapper<Announcement>()
                    .eq(Announcement::getId, id)
                    .setSql("view_count = view_count + 1"));
        }
        return announcement;
    }
    
    public void updateAnnouncementStatus(Long id, Integer status) {
        this.update(new LambdaUpdateWrapper<Announcement>()
                .eq(Announcement::getId, id)
                .set(Announcement::getStatus, status));
    }
    
    public void toggleTop(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement != null) {
            this.update(new LambdaUpdateWrapper<Announcement>()
                    .eq(Announcement::getId, id)
                    .set(Announcement::getIsTop, announcement.getIsTop() == 1 ? 0 : 1));
        }
    }
}
