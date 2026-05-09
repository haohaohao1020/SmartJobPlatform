package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.common.Constants;
import com.smartjob.mapper.JobMapper;
import com.smartjob.model.Job;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JobService extends ServiceImpl<JobMapper, Job> {
    
    public Page<Job> getJobPageForJobSeeker(Integer page, Integer pageSize, String keyword, 
                                              String city, String workYears, String education,
                                              String salaryRange, String salary,
                                              Long industryId, Long categoryId, String subCategory,
                                              String sortBy, String sortOrder) {
        Page<Job> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Job::getStatus, Constants.JOB_STATUS_ONLINE)
               .eq(Job::getAuditStatus, Constants.AUDIT_STATUS_APPROVED);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Job::getTitle, keyword)
                    .or().like(Job::getCompanyName, keyword)
                    .or().like(Job::getSubCategory, keyword));
        }
        
        if (StringUtils.hasText(city)) {
            wrapper.like(Job::getCity, city);
        }
        
        if (StringUtils.hasText(workYears) && !"不限".equals(workYears)) {
            wrapper.eq(Job::getWorkYears, workYears);
        }
        
        if (StringUtils.hasText(education) && !"不限".equals(education)) {
            wrapper.eq(Job::getEducation, education);
        }
        
        if (StringUtils.hasText(salaryRange)) {
            addSalaryRangeFilter(wrapper, salaryRange);
        }
        
        if (StringUtils.hasText(salary)) {
            String[] parts = salary.split("-");
            if (parts.length >= 1) {
                try {
                    int min = Integer.parseInt(parts[0]);
                    if (parts.length == 2) {
                        int max = Integer.parseInt(parts[1]);
                        wrapper.and(w -> w
                                .le(Job::getSalaryMax, max)
                                .ge(Job::getSalaryMin, min));
                    } else {
                        wrapper.ge(Job::getSalaryMin, min);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        
        if (StringUtils.hasText(subCategory)) {
            wrapper.eq(Job::getSubCategory, subCategory);
        }
        
        applySorting(wrapper, sortBy, sortOrder);
        
        return this.page(pageParam, wrapper);
    }
    
    private void addSalaryRangeFilter(LambdaQueryWrapper<Job> wrapper, String salaryRange) {
        switch (salaryRange) {
            case "1":
                wrapper.le(Job::getSalaryMax, 3000);
                break;
            case "2":
                wrapper.and(w -> w.le(Job::getSalaryMax, 5000).ge(Job::getSalaryMin, 3000));
                break;
            case "3":
                wrapper.and(w -> w.le(Job::getSalaryMax, 10000).ge(Job::getSalaryMin, 5000));
                break;
            case "4":
                wrapper.and(w -> w.le(Job::getSalaryMax, 20000).ge(Job::getSalaryMin, 10000));
                break;
            case "5":
                wrapper.and(w -> w.le(Job::getSalaryMax, 50000).ge(Job::getSalaryMin, 20000));
                break;
            case "6":
                wrapper.ge(Job::getSalaryMin, 50000);
                break;
        }
    }
    
    private void applySorting(LambdaQueryWrapper<Job> wrapper, String sortBy, String sortOrder) {
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        switch (sortBy) {
            case "salary":
                if (isAsc) {
                    wrapper.orderByAsc(Job::getSalaryMax);
                } else {
                    wrapper.orderByDesc(Job::getSalaryMax);
                }
                break;
            case "viewCount":
                if (isAsc) {
                    wrapper.orderByAsc(Job::getViewCount);
                } else {
                    wrapper.orderByDesc(Job::getViewCount);
                }
                break;
            case "applyCount":
                if (isAsc) {
                    wrapper.orderByAsc(Job::getApplyCount);
                } else {
                    wrapper.orderByDesc(Job::getApplyCount);
                }
                break;
            default:
                wrapper.orderByDesc(Job::getPublishTime);
        }
    }
    
    public Job getJobDetail(Long id) {
        Job job = this.getById(id);
        if (job != null) {
            job.setViewCount(job.getViewCount() == null ? 1 : job.getViewCount() + 1);
            this.updateById(job);
        }
        return job;
    }
    
    public List<Job> getHotJobs() {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, Constants.JOB_STATUS_ONLINE)
               .eq(Job::getAuditStatus, Constants.AUDIT_STATUS_APPROVED)
               .orderByDesc(Job::getViewCount)
               .last("limit 10");
        return this.list(wrapper);
    }
    
    public List<Job> getLatestJobs() {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, Constants.JOB_STATUS_ONLINE)
               .eq(Job::getAuditStatus, Constants.AUDIT_STATUS_APPROVED)
               .orderByDesc(Job::getPublishTime)
               .last("limit 10");
        return this.list(wrapper);
    }
    
    public Page<Job> getJobPageForHr(Integer page, Integer pageSize, String keyword, 
                                      Integer status, Long hrId) {
        Page<Job> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Job::getHrId, hrId);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Job::getTitle, keyword);
        }
        
        if (status != null) {
            wrapper.eq(Job::getStatus, status);
        }
        
        wrapper.orderByDesc(Job::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    @Transactional
    public Job publishJob(Job job, Long hrId, String hrName, String hrPosition) {
        job.setHrId(hrId);
        job.setHrName(hrName);
        job.setHrTitle(hrPosition);
        job.setStatus(Constants.JOB_STATUS_OFFLINE);
        job.setAuditStatus(Constants.AUDIT_STATUS_PENDING);
        job.setViewCount(0);
        job.setApplyCount(0);
        job.setPublishTime(LocalDateTime.now());
        this.save(job);
        return job;
    }
    
    @Transactional
    public Job updateJob(Job job) {
        job.setAuditStatus(Constants.AUDIT_STATUS_PENDING);
        this.updateById(job);
        return job;
    }
    
    @Transactional
    public void updateJobStatus(Long jobId, Integer status) {
        this.update(new LambdaUpdateWrapper<Job>()
                .eq(Job::getId, jobId)
                .set(Job::getStatus, status));
    }
    
    @Transactional
    public void deleteJob(Long jobId) {
        this.removeById(jobId);
    }
    
    public Page<Job> getJobPageForAdmin(Integer page, Integer pageSize, String keyword, 
                                          Integer auditStatus) {
        Page<Job> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Job::getTitle, keyword)
                    .or().like(Job::getCompanyName, keyword));
        }
        
        if (auditStatus != null) {
            wrapper.eq(Job::getAuditStatus, auditStatus);
        }
        
        wrapper.orderByDesc(Job::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    @Transactional
    public void auditJob(Long jobId, Integer auditStatus, String rejectReason) {
        LambdaUpdateWrapper<Job> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Job::getId, jobId)
               .set(Job::getAuditStatus, auditStatus);
        
        if (StringUtils.hasText(rejectReason)) {
            wrapper.set(Job::getAuditRemark, rejectReason);
        }
        
        if (auditStatus == Constants.AUDIT_STATUS_APPROVED) {
            wrapper.set(Job::getStatus, Constants.JOB_STATUS_ONLINE);
        }
        
        this.update(wrapper);
    }
    
    public void incrementApplyCount(Long jobId) {
        Job job = this.getById(jobId);
        if (job != null) {
            job.setApplyCount(job.getApplyCount() == null ? 1 : job.getApplyCount() + 1);
            this.updateById(job);
        }
    }
}
