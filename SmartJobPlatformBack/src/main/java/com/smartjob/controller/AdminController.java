package com.smartjob.controller;

import com.smartjob.common.Result;
import com.smartjob.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private FavoriteService favoriteService;
    
    @GetMapping("/dashboard/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userService.count();
        long totalJobs = jobService.count();
        long totalCompanies = companyService.count();
        long totalApplications = applicationService.count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalJobs", totalJobs);
        stats.put("totalCompanies", totalCompanies);
        stats.put("totalApplications", totalApplications);
        
        stats.put("todayUsers", 15);
        stats.put("todayJobs", 8);
        stats.put("todayApplications", 45);
        
        return Result.success(stats);
    }
    
    @GetMapping("/dashboard/charts")
    public Result<Map<String, Object>> getDashboardCharts() {
        Map<String, Object> charts = new HashMap<>();
        
        Map<String, List<Object>> userTrend = new HashMap<>();
        userTrend.put("dates", Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        userTrend.put("data", Arrays.asList(120, 132, 101, 134, 90, 230, 210));
        charts.put("userTrend", userTrend);
        
        Map<String, List<Object>> jobTrend = new HashMap<>();
        jobTrend.put("dates", Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        jobTrend.put("data", Arrays.asList(22, 18, 19, 23, 29, 33, 31));
        charts.put("jobTrend", jobTrend);
        
        Map<String, List<Object>> applicationTrend = new HashMap<>();
        applicationTrend.put("dates", Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        applicationTrend.put("data", Arrays.asList(150, 200, 180, 220, 250, 300, 280));
        charts.put("applicationTrend", applicationTrend);
        
        List<Map<String, Object>> jobDistribution = new ArrayList<>();
        String[] categories = {"技术", "产品", "设计", "运营", "市场", "人事", "财务", "其他"};
        int[] values = {350, 120, 80, 150, 100, 60, 40, 100};
        for (int i = 0; i < categories.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", categories[i]);
            item.put("value", values[i]);
            jobDistribution.add(item);
        }
        charts.put("jobDistribution", jobDistribution);
        
        List<Map<String, Object>> cityDistribution = new ArrayList<>();
        String[] cities = {"北京", "上海", "深圳", "杭州", "广州", "成都", "其他"};
        int[] cityValues = {450, 380, 320, 220, 180, 120, 330};
        for (int i = 0; i < cities.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", cities[i]);
            item.put("value", cityValues[i]);
            cityDistribution.add(item);
        }
        charts.put("cityDistribution", cityDistribution);
        
        return Result.success(charts);
    }
    
    @GetMapping("/dashboard/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        String[] types = {"注册", "投递", "收藏", "发布岗位", "企业入驻"};
        String[] users = {"张三", "李四", "王五", "赵六", "钱七"};
        String[] contents = {"新用户注册", "投递了Java开发工程师", "收藏了产品经理岗位", 
                            "发布了前端开发工程师岗位", "提交了企业入驻申请"};
        
        for (int i = 0; i < 10; i++) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", types[i % types.length]);
            activity.put("user", users[i % users.length]);
            activity.put("content", contents[i % contents.length]);
            activity.put("time", (i + 1) + "分钟前");
            activities.add(activity);
        }
        
        return Result.success(activities);
    }
    
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        overview.put("totalUsers", 2580);
        overview.put("activeUsers", 1892);
        overview.put("totalCompanies", 326);
        overview.put("verifiedCompanies", 289);
        overview.put("totalJobs", 1523);
        overview.put("activeJobs", 986);
        overview.put("totalApplications", 8526);
        overview.put("todayApplications", 156);
        
        return Result.success(overview);
    }
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return getDashboardStats();
    }
    
    @GetMapping("/user-distribution")
    public Result<Map<String, Object>> getUserDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("jobseeker", 2150);
        distribution.put("hr", 326);
        distribution.put("admin", 5);
        return Result.success(distribution);
    }
    
    @GetMapping("/audit-pending")
    public Result<Map<String, Object>> getAuditPending() {
        Map<String, Object> pending = new HashMap<>();
        pending.put("companies", 12);
        pending.put("jobs", 28);
        return Result.success(pending);
    }
}
