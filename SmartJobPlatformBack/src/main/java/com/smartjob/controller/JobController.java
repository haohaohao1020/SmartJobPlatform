package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.Job;
import com.smartjob.model.JobCategory;
import com.smartjob.model.Industry;
import com.smartjob.service.JobService;
import com.smartjob.service.UserService;
import com.smartjob.utils.CurrentUserHolder;
import com.smartjob.dto.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/jobs/industries")
    public Result<List<Map<String, Object>>> getIndustries() {
        List<Map<String, Object>> industries = Arrays.asList(
            createIndustryMap(1L, "互联网", "el-icon-monitor"),
            createIndustryMap(2L, "金融", "el-icon-money"),
            createIndustryMap(3L, "教育", "el-icon-reading"),
            createIndustryMap(4L, "医疗健康", "el-icon-first-aid-kit"),
            createIndustryMap(5L, "房地产", "el-icon-office-building"),
            createIndustryMap(6L, "制造业", "el-icon-s-operation"),
            createIndustryMap(7L, "服务业", "el-icon-service"),
            createIndustryMap(8L, "文娱传媒", "el-icon-picture-outline"),
            createIndustryMap(9L, "物流运输", "el-icon-truck"),
            createIndustryMap(10L, "能源化工", "el-icon-connection")
        );
        return Result.success(industries);
    }
    
    @GetMapping("/jobs/categories")
    public Result<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = Arrays.asList(
            createCategoryMap(1L, "技术研发", "el-icon-cpu", Arrays.asList("前端开发", "后端开发", "全栈开发", "移动端开发", "测试开发", "运维/安全", "数据/算法", "架构师")),
            createCategoryMap(2L, "产品设计", "el-icon-edit", Arrays.asList("产品经理", "UI设计", "UX设计", "交互设计", "视觉设计", "用户研究")),
            createCategoryMap(3L, "市场运营", "el-icon-data-analysis", Arrays.asList("市场营销", "品牌推广", "新媒体运营", "内容运营", "用户运营", "活动运营", "SEO/SEM")),
            createCategoryMap(4L, "销售商务", "el-icon-bank-card", Arrays.asList("销售经理", "销售代表", "大客户销售", "渠道销售", "商务拓展", "招商专员")),
            createCategoryMap(5L, "人事行政", "el-icon-user", Arrays.asList("HR/招聘", "HRBP", "培训发展", "薪酬福利", "行政专员", "前台接待")),
            createCategoryMap(6L, "财务法务", "el-icon-document", Arrays.asList("会计", "财务分析", "审计", "税务", "法务", "投资/风控")),
            createCategoryMap(7L, "采购物流", "el-icon-goods", Arrays.asList("采购专员", "供应链管理", "物流经理", "仓储管理", "关务/商检")),
            createCategoryMap(8L, "生产制造", "el-icon-set-up", Arrays.asList("生产主管", "质量控制", "工艺工程师", "设备维护", "精益生产")),
            createCategoryMap(9L, "教育培训", "el-icon-reading", Arrays.asList("教师/讲师", "培训师", "课程顾问", "教研/教学管理")),
            createCategoryMap(10L, "医疗健康", "el-icon-first-aid-kit", Arrays.asList("医生", "护士", "药师", "医疗器械", "医药代表", "健康管理"))
        );
        return Result.success(categories);
    }
    
    @GetMapping("/jobs")
    public Result<PageResult<Job>> getJobs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String workYears,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String salaryRange,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) Long industryId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String subCategory,
            @RequestParam(defaultValue = "publishTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Page<Job> jobPage = jobService.getJobPageForJobSeeker(
                page, pageSize, keyword, city, workYears, education,
                salaryRange, salary, industryId, categoryId, subCategory,
                sortBy, sortOrder);
        
        PageResult<Job> result = PageResult.of(
                jobPage.getRecords(),
                jobPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/jobs/{id}")
    public Result<Job> getJobDetail(@PathVariable Long id) {
        Job job = jobService.getJobDetail(id);
        if (job == null) {
            return Result.error("岗位不存在");
        }
        return Result.success(job);
    }
    
    @GetMapping("/jobs/hot")
    public Result<List<Job>> getHotJobs() {
        List<Job> jobs = jobService.getHotJobs();
        return Result.success(jobs);
    }
    
    @GetMapping("/jobs/latest")
    public Result<List<Job>> getLatestJobs() {
        List<Job> jobs = jobService.getLatestJobs();
        return Result.success(jobs);
    }
    
    @GetMapping("/hr/jobs")
    public Result<PageResult<Job>> getHrJobs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        Long hrId = CurrentUserHolder.getUserId();
        Page<Job> jobPage = jobService.getJobPageForHr(page, pageSize, keyword, status, hrId);
        
        PageResult<Job> result = PageResult.of(
                jobPage.getRecords(),
                jobPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PostMapping("/hr/jobs")
    public Result<Job> publishJob(@RequestBody Job job) {
        Long hrId = CurrentUserHolder.getUserId();
        UserInfoVO user = userService.getCurrentUserInfo();
        Job newJob = jobService.publishJob(job, hrId, user.getUsername(), "HR");
        return Result.success("发布成功，等待审核", newJob);
    }
    
    @PutMapping("/hr/jobs/{id}")
    public Result<Job> updateJob(@PathVariable Long id, @RequestBody Job job) {
        job.setId(id);
        Job updatedJob = jobService.updateJob(job);
        return Result.success("更新成功", updatedJob);
    }
    
    @PutMapping("/hr/jobs/{id}/status")
    public Result<Void> updateJobStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        jobService.updateJobStatus(id, status);
        return Result.success("操作成功", null);
    }
    
    @DeleteMapping("/hr/jobs/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success("删除成功", null);
    }
    
    @GetMapping("/admin/jobs")
    public Result<PageResult<Job>> getAdminJobs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        
        Page<Job> jobPage = jobService.getJobPageForAdmin(page, pageSize, keyword, auditStatus);
        
        PageResult<Job> result = PageResult.of(
                jobPage.getRecords(),
                jobPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PutMapping("/admin/jobs/{id}/audit")
    public Result<Void> auditJob(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer auditStatus = (Integer) params.get("auditStatus");
        String rejectReason = (String) params.get("rejectReason");
        
        String message;
        if (auditStatus == 1) {
            message = "审核通过";
        } else if (auditStatus == 3) {
            message = "已拒绝";
        } else {
            message = "操作成功";
        }
        
        jobService.auditJob(id, auditStatus, rejectReason);
        return Result.success(message, null);
    }
    
    private Map<String, Object> createIndustryMap(Long id, String name, String icon) {
        Map<String, Object> industry = new HashMap<>();
        industry.put("id", id);
        industry.put("name", name);
        industry.put("icon", icon);
        return industry;
    }
    
    private Map<String, Object> createCategoryMap(Long id, String name, String icon, List<String> subCategories) {
        Map<String, Object> category = new HashMap<>();
        category.put("id", id);
        category.put("name", name);
        category.put("icon", icon);
        category.put("subCategories", subCategories);
        return category;
    }
}
