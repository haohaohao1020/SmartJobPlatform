package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.Company;
import com.smartjob.service.CompanyService;
import com.smartjob.utils.CurrentUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @PostMapping("/companies/register")
    public Result<Company> registerCompany(@RequestBody Company company) {
        Long userId = CurrentUserHolder.getUserId();
        Company result = companyService.registerCompany(company, userId);
        return Result.success("企业入驻申请已提交，请等待审核", result);
    }
    
    @PostMapping("/hr/company")
    public Result<Company> saveOrUpdateCompany(@RequestBody Company company) {
        Long userId = CurrentUserHolder.getUserId();
        Company exist = companyService.getMyCompany(userId);
        if (exist == null) {
            Company result = companyService.registerCompany(company, userId);
            return Result.success("企业入驻申请已提交，请等待审核", result);
        } else {
            Company result = companyService.updateCompany(company, userId);
            return Result.success("企业信息更新成功，请等待审核", result);
        }
    }
    
    @GetMapping("/hr/company")
    public Result<Company> getMyCompany() {
        Long userId = CurrentUserHolder.getUserId();
        Company company = companyService.getMyCompany(userId);
        return Result.success(company);
    }
    
    @PutMapping("/hr/company")
    public Result<Company> updateMyCompany(@RequestBody Company company) {
        Long userId = CurrentUserHolder.getUserId();
        Company result = companyService.updateCompany(company, userId);
        return Result.success("更新成功", result);
    }
    
    @GetMapping("/companies/{id}")
    public Result<Company> getCompanyDetail(@PathVariable Long id) {
        Company company = companyService.getCompanyDetail(id);
        if (company == null) {
            return Result.error("企业不存在");
        }
        return Result.success(company);
    }
    
    @GetMapping("/companies")
    public Result<PageResult<Company>> getCompanyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) Integer status) {
        
        Page<Company> companyPage = companyService.getCompanyPage(page, pageSize, keyword, industry, status);
        
        PageResult<Company> result = PageResult.of(
                companyPage.getRecords(),
                companyPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/admin/companies")
    public Result<PageResult<Company>> getAdminCompanyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer status) {
        
        Page<Company> companyPage = companyService.getCompanyPageForAdmin(
                page, pageSize, keyword, industry, auditStatus, status);
        
        PageResult<Company> result = PageResult.of(
                companyPage.getRecords(),
                companyPage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @PutMapping("/admin/companies/{id}/audit")
    public Result<Void> auditCompany(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        
        Integer auditStatus = (Integer) params.get("auditStatus");
        String auditRemark = (String) params.get("auditRemark");
        
        companyService.auditCompany(id, auditStatus, auditRemark);
        return Result.success("审核成功", null);
    }
    
    @PutMapping("/admin/companies/{id}/status")
    public Result<Void> updateCompanyStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        
        Integer status = params.get("status");
        companyService.updateCompanyStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    @GetMapping("/admin/companies/{id}")
    public Result<Company> getAdminCompanyDetail(@PathVariable Long id) {
        Company company = companyService.getById(id);
        return Result.success(company);
    }
    
    @GetMapping("/companies/hot")
    public Result<java.util.List<Company>> getHotCompanies() {
        java.util.List<Company> companies = companyService.getHotCompanies();
        return Result.success(companies);
    }
}
