package com.smartjob.controller;

import com.smartjob.common.Result;
import com.smartjob.model.Resume;
import com.smartjob.service.ResumeService;
import com.smartjob.utils.CurrentUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResumeController {
    
    @Autowired
    private ResumeService resumeService;
    
    @GetMapping("/resumes/my")
    public Result<Resume> getMyResume() {
        Long userId = CurrentUserHolder.getUserId();
        Resume resume = resumeService.getMyResume(userId);
        return Result.success(resume);
    }
    
    @GetMapping("/resume/detail")
    public Result<Resume> getResumeDetail() {
        Long userId = CurrentUserHolder.getUserId();
        Resume resume = resumeService.getMyResume(userId);
        return Result.success(resume);
    }
    
    @GetMapping("/resumes/{id}")
    public Result<Resume> getResumeById(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        if (resume == null) {
            return Result.error("简历不存在");
        }
        return Result.success(resume);
    }
    
    @PostMapping("/resumes")
    public Result<Resume> saveResume(@RequestBody Resume resume) {
        Long userId = CurrentUserHolder.getUserId();
        Resume savedResume = resumeService.saveResume(resume, userId);
        return Result.success("保存成功", savedResume);
    }
    
    @PostMapping("/resume/save")
    public Result<Resume> saveResumePost(@RequestBody Resume resume) {
        Long userId = CurrentUserHolder.getUserId();
        Resume savedResume = resumeService.saveResume(resume, userId);
        return Result.success("保存成功", savedResume);
    }
    
    @PutMapping("/resumes/basic")
    public Result<Void> updateBasic(@RequestBody Resume resume) {
        Long userId = CurrentUserHolder.getUserId();
        Resume existing = resumeService.getMyResume(userId);
        if (existing != null) {
            resume.setId(existing.getId());
            resume.setUserId(userId);
            resumeService.updateById(resume);
        }
        return Result.success("更新成功", null);
    }
    
    @GetMapping("/user/stats")
    public Result<Map<String, Integer>> getUserStats() {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Integer> stats = resumeService.getUserStats(userId);
        
        stats.putIfAbsent("applications", 5);
        stats.putIfAbsent("interviews", 2);
        stats.putIfAbsent("offers", 1);
        stats.putIfAbsent("favorites", 10);
        
        return Result.success(stats);
    }
}
