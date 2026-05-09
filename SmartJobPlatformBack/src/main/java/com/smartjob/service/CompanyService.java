package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.common.Constants;
import com.smartjob.mapper.CompanyMapper;
import com.smartjob.model.Company;
import com.smartjob.model.User;
import com.smartjob.utils.CurrentUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CompanyService extends ServiceImpl<CompanyMapper, Company> {
    
    @Autowired
    private UserService userService;
    
    @Transactional
    public Company registerCompany(Company company, Long userId) {
        Company exist = this.getOne(new LambdaQueryWrapper<Company>()
                .eq(Company::getUserId, userId));
        
        if (exist != null) {
            throw new IllegalArgumentException("您已经入驻过企业");
        }
        
        exist = this.getOne(new LambdaQueryWrapper<Company>()
                .eq(Company::getName, company.getName()));
        
        if (exist != null) {
            throw new IllegalArgumentException("该企业名称已存在");
        }
        
        company.setUserId(userId);
        company.setAuditStatus(Constants.AUDIT_STATUS_PENDING);
        company.setStatus(1);
        company.setViewCount(0);
        company.setJobCount(0);
        
        this.save(company);
        return company;
    }
    
    @Transactional
    public Company updateCompany(Company company, Long userId) {
        Company exist = this.getOne(new LambdaQueryWrapper<Company>()
                .eq(Company::getUserId, userId));
        
        if (exist == null) {
            throw new IllegalArgumentException("企业信息不存在");
        }
        
        company.setId(exist.getId());
        company.setUserId(userId);
        company.setAuditStatus(Constants.AUDIT_STATUS_PENDING);
        
        this.updateById(company);
        return this.getById(exist.getId());
    }
    
    public Company getMyCompany(Long userId) {
        Company company = this.getOne(new LambdaQueryWrapper<Company>()
                .eq(Company::getUserId, userId));
        return company;
    }
    
    public Company getCompanyDetail(Long id) {
        Company company = this.getById(id);
        if (company != null) {
            this.update(new LambdaUpdateWrapper<Company>()
                    .eq(Company::getId, id)
                    .setSql("view_count = view_count + 1"));
        }
        return company;
    }
    
    public Page<Company> getCompanyPage(Integer page, Integer pageSize, String keyword, 
                                         String industry, Integer status) {
        Page<Company> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Company::getAuditStatus, Constants.AUDIT_STATUS_APPROVED)
                .eq(Company::getStatus, 1);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Company::getName, keyword);
        }
        
        if (StringUtils.hasText(industry) && !"不限".equals(industry)) {
            wrapper.eq(Company::getIndustry, industry);
        }
        
        if (status != null) {
            wrapper.eq(Company::getStatus, status);
        }
        
        wrapper.orderByDesc(Company::getId);
        return this.page(pageParam, wrapper);
    }
    
    public Page<Company> getCompanyPageForAdmin(Integer page, Integer pageSize, String keyword, 
                                                  String industry, Integer auditStatus, Integer status) {
        Page<Company> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Company::getName, keyword);
        }
        
        if (StringUtils.hasText(industry) && !"不限".equals(industry)) {
            wrapper.eq(Company::getIndustry, industry);
        }
        
        if (auditStatus != null) {
            wrapper.eq(Company::getAuditStatus, auditStatus);
        }
        
        if (status != null) {
            wrapper.eq(Company::getStatus, status);
        }
        
        wrapper.orderByDesc(Company::getId);
        return this.page(pageParam, wrapper);
    }
    
    @Transactional
    public void auditCompany(Long id, Integer auditStatus, String auditRemark) {
        Company company = this.getById(id);
        if (company == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        
        this.update(new LambdaUpdateWrapper<Company>()
                .eq(Company::getId, id)
                .set(Company::getAuditStatus, auditStatus)
                .set(Company::getAuditRemark, auditRemark));
        
        if (auditStatus == Constants.AUDIT_STATUS_APPROVED) {
            User user = userService.getById(company.getUserId());
            if (user != null && !Constants.ROLE_HR.equals(user.getRole())) {
                user.setRole(Constants.ROLE_HR);
                userService.updateById(user);
            }
        }
    }
    
    @Transactional
    public void updateCompanyStatus(Long id, Integer status) {
        this.update(new LambdaUpdateWrapper<Company>()
                .eq(Company::getId, id)
                .set(Company::getStatus, status));
    }
    
    public void incrementJobCount(Long companyId) {
        this.update(new LambdaUpdateWrapper<Company>()
                .eq(Company::getId, companyId)
                .setSql("job_count = job_count + 1"));
    }
    
    public void decrementJobCount(Long companyId) {
        this.update(new LambdaUpdateWrapper<Company>()
                .eq(Company::getId, companyId)
                .setSql("job_count = GREATEST(job_count - 1, 0)"));
    }
    
    public java.util.List<Company> getHotCompanies() {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getAuditStatus, Constants.AUDIT_STATUS_APPROVED)
                .eq(Company::getStatus, 1)
                .orderByDesc(Company::getViewCount)
                .last("limit 10");
        return this.list(wrapper);
    }
}
