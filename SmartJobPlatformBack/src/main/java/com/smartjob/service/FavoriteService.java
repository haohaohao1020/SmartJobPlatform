package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.mapper.FavoriteMapper;
import com.smartjob.model.Favorite;
import com.smartjob.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService extends ServiceImpl<FavoriteMapper, Favorite> {
    
    @Autowired
    private JobService jobService;
    
    @Transactional
    public Favorite addFavorite(Favorite favorite, Long userId) {
        Favorite exist = this.getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getJobId, favorite.getJobId()));
        
        if (exist != null) {
            throw new IllegalArgumentException("该岗位已收藏");
        }
        
        Job job = jobService.getById(favorite.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("岗位不存在");
        }
        
        favorite.setUserId(userId);
        favorite.setJobTitle(job.getTitle());
        favorite.setCompanyName(job.getCompanyName());
        favorite.setCompanyLogo(job.getCompanyLogo());
        favorite.setSalaryMin(job.getSalaryMin());
        favorite.setSalaryMax(job.getSalaryMax());
        favorite.setCity(job.getCity());
        favorite.setWorkYears(job.getWorkYears());
        favorite.setEducation(job.getEducation());
        favorite.setTags(job.getTags());
        
        this.save(favorite);
        return favorite;
    }
    
    public Page<Map<String, Object>> getMyFavorites(Integer page, Integer pageSize, Long userId) {
        Page<Favorite> pageParam = new Page<>(page, pageSize);
        Page<Favorite> favoritePage = this.page(pageParam, 
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getId));
        
        List<Map<String, Object>> records = new ArrayList<>();
        for (Favorite f : favoritePage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("jobId", f.getJobId());
            map.put("userId", f.getUserId());
            map.put("jobTitle", f.getJobTitle());
            map.put("companyName", f.getCompanyName());
            map.put("companyLogo", f.getCompanyLogo());
            map.put("salaryMin", f.getSalaryMin());
            map.put("salaryMax", f.getSalaryMax());
            map.put("city", f.getCity());
            map.put("workYears", f.getWorkYears());
            map.put("education", f.getEducation());
            map.put("tags", f.getTags());
            map.put("createTime", f.getCreateTime());
            
            Job job = jobService.getById(f.getJobId());
            if (job != null) {
                map.put("status", job.getStatus());
                map.put("jobStatus", job.getStatus());
                map.put("jobInfo", job);
            } else {
                map.put("status", 0);
                map.put("jobStatus", 0);
            }
            records.add(map);
        }
        
        Page<Map<String, Object>> result = new Page<>();
        result.setRecords(records);
        result.setTotal(favoritePage.getTotal());
        result.setCurrent(favoritePage.getCurrent());
        result.setSize(favoritePage.getSize());
        return result;
    }
    
    @Transactional
    public void removeFavorite(Long id, Long userId) {
        Favorite favorite = this.getById(id);
        if (favorite == null) {
            throw new IllegalArgumentException("收藏记录不存在");
        }
        if (!favorite.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作");
        }
        this.removeById(id);
    }
    
    @Transactional
    public void removeFavoriteByJobId(Long jobId, Long userId) {
        this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getJobId, jobId));
    }
    
    public Map<String, Object> checkFavorite(Long userId, Long jobId) {
        Favorite exist = this.getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getJobId, jobId));
        
        Map<String, Object> result = new HashMap<>();
        if (exist != null) {
            result.put("isFavorite", true);
            result.put("favoriteId", exist.getId());
        } else {
            result.put("isFavorite", false);
            result.put("favoriteId", null);
        }
        return result;
    }
    
    public long getFavoriteCount(Long userId) {
        return this.count(new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId));
    }
    
    public Map<String, Integer> getFavoriteStatistics(Long userId) {
        List<Favorite> favorites = this.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId));
        
        int total = favorites.size();
        int active = 0;
        int expired = 0;
        
        for (Favorite f : favorites) {
            Job job = jobService.getById(f.getJobId());
            if (job != null && job.getStatus() != null && job.getStatus() == 1) {
                active++;
            } else {
                expired++;
            }
        }
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("expired", expired);
        return stats;
    }
    
    public Map<String, Object> getFavoriteJob(Long favoriteId, Long userId) {
        Favorite favorite = this.getById(favoriteId);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            return null;
        }
        
        Job job = jobService.getById(favorite.getJobId());
        if (job == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", favorite.getId());
        result.put("jobId", favorite.getJobId());
        result.put("jobTitle", favorite.getJobTitle());
        result.put("companyName", favorite.getCompanyName());
        result.put("companyLogo", favorite.getCompanyLogo());
        result.put("salaryMin", favorite.getSalaryMin());
        result.put("salaryMax", favorite.getSalaryMax());
        result.put("city", favorite.getCity());
        result.put("workYears", favorite.getWorkYears());
        result.put("education", favorite.getEducation());
        result.put("jobStatus", job.getStatus());
        result.put("favoriteTime", favorite.getCreateTime());
        result.put("description", job.getDescription());
        result.put("requirements", job.getRequirement());
        result.put("welfare", job.getTags());
        result.put("publishTime", job.getPublishTime());
        result.put("viewCount", job.getViewCount());
        result.put("applyCount", job.getApplyCount());
        result.put("hrId", job.getHrId());
        result.put("hrName", job.getHrName());
        result.put("hrPosition", job.getHrTitle());
        result.put("companyId", job.getCompanyId());
        
        return result;
    }
}
