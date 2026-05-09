package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.Company;
import com.smartjob.model.Job;
import com.smartjob.service.CompanyService;
import com.smartjob.service.JobService;
import com.smartjob.service.UserService;
import com.smartjob.utils.CurrentUserHolder;
import com.smartjob.dto.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
    public Result<PageResult<Map<String, Object>>> getJobs(
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
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Job job : jobPage.getRecords()) {
            list.add(convertJobToMap(job));
        }
        
        PageResult<Map<String, Object>> result = PageResult.of(
                list,
                jobPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/jobs/{id}")
    public Result<Map<String, Object>> getJobDetail(@PathVariable Long id) {
        Job job = jobService.getJobDetail(id);
        if (job == null) {
            return Result.error("岗位不存在");
        }
        return Result.success(convertJobToMap(job));
    }
    
    @GetMapping("/jobs/hot")
    public Result<List<Map<String, Object>>> getHotJobs() {
        List<Job> jobs = jobService.getHotJobs();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Job job : jobs) {
            list.add(convertJobToMap(job));
        }
        return Result.success(list);
    }
    
    @GetMapping("/jobs/latest")
    public Result<List<Map<String, Object>>> getLatestJobs() {
        List<Job> jobs = jobService.getLatestJobs();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Job job : jobs) {
            list.add(convertJobToMap(job));
        }
        return Result.success(list);
    }
    
    @GetMapping("/hr/jobs")
    public Result<PageResult<Map<String, Object>>> getHrJobs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        Long hrId = CurrentUserHolder.getUserId();
        Page<Job> jobPage = jobService.getJobPageForHr(page, pageSize, keyword, status, hrId);
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Job job : jobPage.getRecords()) {
            list.add(convertJobToMap(job));
        }
        
        PageResult<Map<String, Object>> result = PageResult.of(
                list,
                jobPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PostMapping("/hr/jobs")
    public Result<Map<String, Object>> publishJob(@RequestBody Map<String, Object> jobForm) {
        Long hrId = CurrentUserHolder.getUserId();
        UserInfoVO user = userService.getCurrentUserInfo();
        
        Job job = new Job();
        job.setTitle((String) jobForm.get("title"));
        job.setCity((String) jobForm.get("city"));
        job.setWorkYears((String) jobForm.get("workYears"));
        job.setEducation((String) jobForm.get("education"));
        job.setJobType((String) jobForm.get("jobType"));
        job.setDescription((String) jobForm.get("description"));
        job.setRequirement((String) jobForm.get("requirements"));
        job.setSubCategory((String) jobForm.get("subCategory"));
        
        Object salaryMin = jobForm.get("salaryMin");
        if (salaryMin != null) {
            job.setSalaryMin(((Number) salaryMin).intValue());
        }
        Object salaryMax = jobForm.get("salaryMax");
        if (salaryMax != null) {
            job.setSalaryMax(((Number) salaryMax).intValue());
        }
        
        Object welfare = jobForm.get("welfare");
        if (welfare != null) {
            try {
                job.setTags(objectMapper.writeValueAsString(welfare));
            } catch (Exception e) {
                job.setTags(welfare.toString());
            }
        }
        
        Company company = companyService.getMyCompany(hrId);
        if (company != null) {
            job.setCompanyId(company.getId());
            job.setCompanyName(company.getName());
            job.setCompanyLogo(company.getLogo());
            job.setIndustry(company.getIndustry());
        }
        
        Job newJob = jobService.publishJob(job, hrId, user.getUsername(), "HR");
        return Result.success("发布成功，等待审核", convertJobToMap(newJob));
    }
    
    @PutMapping("/hr/jobs/{id}")
    public Result<Map<String, Object>> updateJob(@PathVariable Long id, @RequestBody Map<String, Object> jobForm) {
        Job job = jobService.getById(id);
        if (job == null) {
            return Result.error("岗位不存在");
        }
        
        if (jobForm.containsKey("title")) {
            job.setTitle((String) jobForm.get("title"));
        }
        if (jobForm.containsKey("city")) {
            job.setCity((String) jobForm.get("city"));
        }
        if (jobForm.containsKey("workYears")) {
            job.setWorkYears((String) jobForm.get("workYears"));
        }
        if (jobForm.containsKey("education")) {
            job.setEducation((String) jobForm.get("education"));
        }
        if (jobForm.containsKey("jobType")) {
            job.setJobType((String) jobForm.get("jobType"));
        }
        if (jobForm.containsKey("description")) {
            job.setDescription((String) jobForm.get("description"));
        }
        if (jobForm.containsKey("requirements")) {
            job.setRequirement((String) jobForm.get("requirements"));
        }
        if (jobForm.containsKey("subCategory")) {
            job.setSubCategory((String) jobForm.get("subCategory"));
        }
        
        Object salaryMin = jobForm.get("salaryMin");
        if (salaryMin != null) {
            job.setSalaryMin(((Number) salaryMin).intValue());
        }
        Object salaryMax = jobForm.get("salaryMax");
        if (salaryMax != null) {
            job.setSalaryMax(((Number) salaryMax).intValue());
        }
        
        Object welfare = jobForm.get("welfare");
        if (welfare != null) {
            try {
                job.setTags(objectMapper.writeValueAsString(welfare));
            } catch (Exception e) {
                job.setTags(welfare.toString());
            }
        }
        
        Job updatedJob = jobService.updateJob(job);
        return Result.success("更新成功", convertJobToMap(updatedJob));
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
    public Result<PageResult<Map<String, Object>>> getAdminJobs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        
        Page<Job> jobPage = jobService.getJobPageForAdmin(page, pageSize, keyword, auditStatus);
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Job job : jobPage.getRecords()) {
            list.add(convertJobToMap(job));
        }
        
        PageResult<Map<String, Object>> result = PageResult.of(
                list,
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
    
    private Map<String, Object> convertJobToMap(Job job) {
        Map<String, Object> map = new HashMap<>();
        BeanUtils.copyProperties(job, map);
        
        map.put("requirements", job.getRequirement());
        map.put("hrPosition", job.getHrTitle());
        
        if (job.getTags() != null && !job.getTags().isEmpty()) {
            try {
                if (job.getTags().startsWith("[") || job.getTags().startsWith("{")) {
                    List<String> welfare = objectMapper.readValue(job.getTags(), new TypeReference<List<String>>() {});
                    map.put("welfare", welfare);
                } else {
                    map.put("welfare", Arrays.asList(job.getTags()));
                }
            } catch (Exception e) {
                map.put("welfare", Arrays.asList(job.getTags()));
            }
        } else {
            map.put("welfare", new ArrayList<>());
        }
        
        Company company = null;
        if (job.getCompanyId() != null) {
            company = companyService.getById(job.getCompanyId());
        }
        
        if (company != null) {
            map.put("companySize", company.getScale());
        } else {
            map.put("companySize", "1000人以上");
        }
        
        return map;
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
