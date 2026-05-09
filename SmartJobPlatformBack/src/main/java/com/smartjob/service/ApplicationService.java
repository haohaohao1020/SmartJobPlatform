package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.common.Constants;
import com.smartjob.mapper.ApplicationMapper;
import com.smartjob.model.Application;
import com.smartjob.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService extends ServiceImpl<ApplicationMapper, Application> {
    
    @Autowired
    private JobService jobService;
    
    private String getStatusName(Integer status) {
        switch (status) {
            case 1: return "待查看";
            case 2: return "待面试";
            case 3: return "面试中";
            case 4: return "待Offer";
            case 5: return "已拒绝";
            case 6: return "已录用";
            default: return "待查看";
        }
    }
    
    @Transactional
    public Application applyJob(Application application, Long userId, Long resumeId) {
        Application exist = this.getOne(new LambdaQueryWrapper<Application>()
                .eq(Application::getUserId, userId)
                .eq(Application::getJobId, application.getJobId()));
        
        if (exist != null) {
            throw new IllegalArgumentException("您已经投递过该岗位");
        }
        
        Job job = jobService.getById(application.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("岗位不存在");
        }
        
        application.setUserId(userId);
        application.setResumeId(resumeId);
        application.setStatus(Constants.APPLICATION_STATUS_PENDING);
        application.setStatusName("待查看");
        application.setIsRead(0);
        application.setApplyTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        application.setHrId(job.getHrId());
        application.setHrName(job.getHrName());
        application.setJobTitle(job.getTitle());
        application.setCompanyId(job.getCompanyId());
        application.setCompanyName(job.getCompanyName());
        application.setCompanyLogo(job.getCompanyLogo());
        application.setSalaryMin(job.getSalaryMin());
        application.setSalaryMax(job.getSalaryMax());
        application.setCity(job.getCity());
        application.setWorkYears(job.getWorkYears());
        application.setEducation(job.getEducation());
        
        this.save(application);
        jobService.incrementApplyCount(application.getJobId());
        
        return application;
    }
    
    public Page<Application> getMyApplications(Integer page, Integer pageSize, Integer status, Long userId) {
        Page<Application> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Application::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(Application::getStatus, status);
        }
        
        wrapper.orderByDesc(Application::getApplyTime);
        return this.page(pageParam, wrapper);
    }
    
    public Map<String, Object> checkApplication(Long userId, Long jobId) {
        Application exist = this.getOne(new LambdaQueryWrapper<Application>()
                .eq(Application::getUserId, userId)
                .eq(Application::getJobId, jobId));
        
        Map<String, Object> result = new HashMap<>();
        if (exist != null) {
            result.put("hasApplied", true);
            result.put("applicationId", exist.getId());
        } else {
            result.put("hasApplied", false);
            result.put("applicationId", null);
        }
        return result;
    }
    
    public Map<String, Integer> getApplicationStatistics(Long userId) {
        Map<String, Integer> stats = new HashMap<>();
        
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getUserId, userId);
        List<Application> all = this.list(wrapper);
        
        stats.put("total", all.size());
        stats.put("pending", (int) all.stream().filter(a -> a.getStatus() == 1).count());
        stats.put("interviewing", (int) all.stream().filter(a -> a.getStatus() == 2 || a.getStatus() == 3).count());
        stats.put("offer", (int) all.stream().filter(a -> a.getStatus() == 4).count());
        stats.put("hired", (int) all.stream().filter(a -> a.getStatus() == 6).count());
        stats.put("rejected", (int) all.stream().filter(a -> a.getStatus() == 5).count());
        
        return stats;
    }
    
    @Transactional
    public void cancelApplication(Long id, Long userId) {
        Application application = this.getById(id);
        if (application == null) {
            throw new IllegalArgumentException("投递记录不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作");
        }
        this.removeById(id);
    }
    
    public Page<Application> getHrApplications(Integer page, Integer pageSize, String keyword, 
                                                 Integer status, Long jobId, Long hrId) {
        Page<Application> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Application::getHrId, hrId);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Application::getJobTitle, keyword);
        }
        
        if (status != null) {
            wrapper.eq(Application::getStatus, status);
        }
        
        if (jobId != null) {
            wrapper.eq(Application::getJobId, jobId);
        }
        
        wrapper.orderByDesc(Application::getApplyTime);
        return this.page(pageParam, wrapper);
    }
    
    @Transactional
    public void updateApplicationStatus(Long id, Integer status) {
        this.update(new LambdaUpdateWrapper<Application>()
                .eq(Application::getId, id)
                .set(Application::getStatus, status)
                .set(Application::getStatusName, getStatusName(status)));
    }
    
    @Transactional
    public void markAsRead(Long id) {
        this.update(new LambdaUpdateWrapper<Application>()
                .eq(Application::getId, id)
                .set(Application::getIsRead, 1));
    }
}
