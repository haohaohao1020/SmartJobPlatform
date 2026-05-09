package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.Application;
import com.smartjob.model.Resume;
import com.smartjob.service.ApplicationService;
import com.smartjob.service.ResumeService;
import com.smartjob.utils.CurrentUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ResumeService resumeService;
    
    @PostMapping("/applications")
    public Result<Application> submitApplication(@RequestBody Application application) {
        Long userId = CurrentUserHolder.getUserId();
        
        Resume resume = resumeService.getMyResume(userId);
        if (resume == null) {
            return Result.error("请先完善简历");
        }
        
        Application result = applicationService.applyJob(application, userId, resume.getId());
        return Result.success("投递成功", result);
    }
    
    @PostMapping("/applications/submit")
    public Result<Application> submitApplicationPost(@RequestBody Application application) {
        Long userId = CurrentUserHolder.getUserId();
        
        Resume resume = resumeService.getMyResume(userId);
        if (resume == null) {
            return Result.error("请先完善简历");
        }
        
        Application result = applicationService.applyJob(application, userId, resume.getId());
        return Result.success("投递成功", result);
    }
    
    @GetMapping("/applications/my")
    public Result<PageResult<Application>> getMyApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        
        Long userId = CurrentUserHolder.getUserId();
        Page<Application> applicationPage = applicationService.getMyApplications(page, pageSize, status, userId);
        
        PageResult<Application> result = PageResult.of(
                applicationPage.getRecords(),
                applicationPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/applications/list")
    public Result<PageResult<Application>> getApplicationsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        
        Long userId = CurrentUserHolder.getUserId();
        Page<Application> applicationPage = applicationService.getMyApplications(page, pageSize, status, userId);
        
        PageResult<Application> result = PageResult.of(
                applicationPage.getRecords(),
                applicationPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/applications/{id}")
    public Result<Application> getApplicationDetail(@PathVariable Long id) {
        Application application = applicationService.getById(id);
        if (application == null) {
            return Result.error("投递记录不存在");
        }
        return Result.success(application);
    }
    
    @PostMapping("/applications/{id}/cancel")
    public Result<Void> cancelApplication(@PathVariable Long id) {
        Long userId = CurrentUserHolder.getUserId();
        applicationService.cancelApplication(id, userId);
        return Result.success("取消投递成功", null);
    }
    
    @GetMapping("/applications/check")
    public Result<Map<String, Object>> checkApplication(
            @RequestParam Long jobId) {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Object> result = applicationService.checkApplication(userId, jobId);
        return Result.success(result);
    }
    
    @GetMapping("/applications/statistics")
    public Result<Map<String, Integer>> getApplicationStatistics() {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Integer> result = applicationService.getApplicationStatistics(userId);
        return Result.success(result);
    }
    
    @GetMapping("/hr/applications")
    public Result<PageResult<Application>> getHrApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long jobId) {
        
        Long hrId = CurrentUserHolder.getUserId();
        Page<Application> applicationPage = applicationService.getHrApplications(
                page, pageSize, keyword, status, jobId, hrId);
        
        PageResult<Application> result = PageResult.of(
                applicationPage.getRecords(),
                applicationPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PutMapping("/hr/applications/{id}/status")
    public Result<Void> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        applicationService.updateApplicationStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    @PutMapping("/hr/applications/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        applicationService.markAsRead(id);
        return Result.success("标记成功", null);
    }
    
    @GetMapping("/hr/resumes")
    public Result<PageResult<Application>> getHrResumes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer interviewStatus,
            @RequestParam(required = false) Long jobId) {
        return getHrApplications(page, pageSize, keyword, interviewStatus, jobId);
    }
    
    @PutMapping("/hr/resumes/{id}/status")
    public Result<Void> updateResumeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        applicationService.updateApplicationStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    @PutMapping("/hr/resumes/{id}/read")
    public Result<Void> markResumeAsRead(@PathVariable Long id) {
        applicationService.markAsRead(id);
        return Result.success("标记成功", null);
    }
}
