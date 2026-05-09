package com.smartjob.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartjob.common.PageResult;
import com.smartjob.common.Result;
import com.smartjob.model.Favorite;
import com.smartjob.service.FavoriteService;
import com.smartjob.utils.CurrentUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/favorites")
    public Result<Favorite> addFavorite(@RequestBody Favorite favorite) {
        Long userId = CurrentUserHolder.getUserId();
        Favorite result = favoriteService.addFavorite(favorite, userId);
        return Result.success("收藏成功", result);
    }
    
    @PostMapping("/jobs/{id}/favorite")
    public Result<Favorite> addFavoritePost(@PathVariable Long id) {
        Long userId = CurrentUserHolder.getUserId();
        Favorite favorite = new Favorite();
        favorite.setJobId(id);
        Favorite result = favoriteService.addFavorite(favorite, userId);
        return Result.success("收藏成功", result);
    }
    
    @GetMapping("/favorites/my")
    public Result<PageResult<Map<String, Object>>> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long userId = CurrentUserHolder.getUserId();
        Page<Map<String, Object>> favoritePage = favoriteService.getMyFavorites(page, pageSize, userId);
        
        PageResult<Map<String, Object>> result = PageResult.of(
                favoritePage.getRecords(),
                favoritePage.getTotal(),
                page,
                pageSize);
        
        return Result.success(result);
    }
    
    @GetMapping("/favorites/list")
    public Result<PageResult<Map<String, Object>>> getFavoritesList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return getMyFavorites(page, pageSize);
    }
    
    @DeleteMapping("/favorites/{id}")
    public Result<Void> removeFavorite(@PathVariable Long id) {
        Long userId = CurrentUserHolder.getUserId();
        favoriteService.removeFavorite(id, userId);
        return Result.success("取消收藏成功", null);
    }
    
    @DeleteMapping("/jobs/{id}/favorite")
    public Result<Void> removeFavoriteByJobId(@PathVariable Long id) {
        Long userId = CurrentUserHolder.getUserId();
        favoriteService.removeFavoriteByJobId(id, userId);
        return Result.success("取消收藏成功", null);
    }
    
    @GetMapping("/favorites/check")
    public Result<Map<String, Object>> checkFavorite(@RequestParam Long jobId) {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Object> result = favoriteService.checkFavorite(userId, jobId);
        return Result.success(result);
    }
    
    @GetMapping("/jobs/{id}/favorite-status")
    public Result<Map<String, Object>> getFavoriteStatus(@PathVariable Long id) {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Object> result = favoriteService.checkFavorite(userId, id);
        return Result.success(result);
    }
    
    @PostMapping("/favorites/add")
    public Result<Favorite> addFavoritePost2(@RequestBody Map<String, Long> params) {
        Long jobId = params.get("jobId");
        Long userId = CurrentUserHolder.getUserId();
        Favorite favorite = new Favorite();
        favorite.setJobId(jobId);
        Favorite result = favoriteService.addFavorite(favorite, userId);
        return Result.success("收藏成功", result);
    }
    
    @PostMapping("/favorites/remove")
    public Result<Void> removeFavoritePost(@RequestBody Map<String, Long> params) {
        Long jobId = params.get("jobId");
        Long userId = CurrentUserHolder.getUserId();
        favoriteService.removeFavoriteByJobId(jobId, userId);
        return Result.success("取消收藏成功", null);
    }
    
    @DeleteMapping("/favorites/batch")
    public Result<Void> batchRemoveFavorite(@RequestBody Map<String, Object> params) {
        java.util.List<Long> ids = (java.util.List<Long>) params.get("ids");
        Long userId = CurrentUserHolder.getUserId();
        for (Long id : ids) {
            favoriteService.removeFavorite(id, userId);
        }
        return Result.success("批量取消收藏成功", null);
    }
    
    @GetMapping("/favorites/statistics")
    public Result<Map<String, Integer>> getFavoriteStatistics() {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Integer> stats = favoriteService.getFavoriteStatistics(userId);
        return Result.success(stats);
    }
    
    @GetMapping("/favorites/{id}/job")
    public Result<Map<String, Object>> getFavoriteJob(@PathVariable Long id) {
        Long userId = CurrentUserHolder.getUserId();
        Map<String, Object> job = favoriteService.getFavoriteJob(id, userId);
        if (job == null) {
            return Result.error("收藏不存在");
        }
        return Result.success(job);
    }
}
