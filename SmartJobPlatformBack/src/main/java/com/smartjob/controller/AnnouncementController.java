package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.Announcement;
import com.smartjob.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @GetMapping("/announcements")
    public Result<PageResult<Announcement>> getAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {
        
        Page<Announcement> announcementPage = announcementService.getAnnouncementPage(
                page, pageSize, keyword, type);
        
        PageResult<Announcement> result = PageResult.of(
                announcementPage.getRecords(),
                announcementPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/announcements/list")
    public Result<PageResult<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {
        return getAnnouncements(page, pageSize, keyword, type);
    }
    
    @GetMapping("/announcements/recent")
    public Result<List<Announcement>> getRecentAnnouncements() {
        Page<Announcement> page = announcementService.getAnnouncementPage(1, 5, null, null);
        return Result.success(page.getRecords());
    }
    
    @GetMapping("/announcements/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementDetail(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }
    
    @GetMapping("/admin/announcements")
    public Result<PageResult<Announcement>> getAdminAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        
        Page<Announcement> announcementPage = announcementService.getAnnouncementPageForAdmin(
                page, pageSize, keyword, type, status);
        
        PageResult<Announcement> result = PageResult.of(
                announcementPage.getRecords(),
                announcementPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PostMapping("/admin/announcements")
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return Result.success("创建成功", announcement);
    }
    
    @PutMapping("/admin/announcements/{id}")
    public Result<Announcement> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.updateById(announcement);
        return Result.success("更新成功", announcement);
    }
    
    @DeleteMapping("/admin/announcements/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    @PutMapping("/admin/announcements/{id}/status")
    public Result<Void> updateAnnouncementStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        announcementService.updateAnnouncementStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    @PutMapping("/admin/announcements/{id}/top")
    public Result<Void> toggleTop(@PathVariable Long id) {
        announcementService.toggleTop(id);
        return Result.success("操作成功", null);
    }
    
    @GetMapping("/admin/announcements/{id}")
    public Result<Announcement> getAdminAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }
}
