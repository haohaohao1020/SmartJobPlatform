package com.smartjob.controller;

import com.smartjob.common.Constants;
import com.smartjob.common.Result;
import com.smartjob.dto.ChangePasswordDTO;
import com.smartjob.dto.LoginDTO;
import com.smartjob.dto.LoginResultVO;
import com.smartjob.dto.RegisterDTO;
import com.smartjob.dto.UserInfoVO;
import com.smartjob.model.User;
import com.smartjob.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix}")
    private String urlPrefix;
    
    @PostMapping("/jobseeker/login")
    public Result<LoginResultVO> jobSeekerLogin(@RequestBody LoginDTO dto) {
        LoginResultVO result = userService.login(dto, Constants.ROLE_JOBSEEKER);
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/hr/login")
    public Result<LoginResultVO> hrLogin(@RequestBody LoginDTO dto) {
        LoginResultVO result = userService.login(dto, Constants.ROLE_HR);
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/company/login")
    public Result<LoginResultVO> companyLogin(@RequestBody LoginDTO dto) {
        LoginResultVO result = userService.login(dto, Constants.ROLE_HR);
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/admin/login")
    public Result<LoginResultVO> adminLogin(@RequestBody LoginDTO dto) {
        LoginResultVO result = userService.login(dto, Constants.ROLE_ADMIN);
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/user/login")
    public Result<LoginResultVO> userLogin(@RequestBody LoginDTO dto) {
        LoginResultVO result = userService.login(dto, Constants.ROLE_JOBSEEKER);
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/user/register")
    public Result<LoginResultVO> register(@RequestBody RegisterDTO dto) {
        LoginResultVO result = userService.register(dto);
        return Result.success("注册成功", result);
    }
    
    @GetMapping("/user/info")
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO result = userService.getCurrentUserInfo();
        return Result.success(result);
    }
    
    @GetMapping("/user/info/hr")
    public Result<UserInfoVO> getHrInfo() {
        UserInfoVO result = userService.getCurrentUserInfo();
        return Result.success(result);
    }
    
    @GetMapping("/user/info/admin")
    public Result<UserInfoVO> getAdminInfo() {
        UserInfoVO result = userService.getCurrentUserInfo();
        return Result.success(result);
    }
    
    @PutMapping("/user/profile")
    public Result<UserInfoVO> updateProfile(@RequestBody User user) {
        UserInfoVO result = userService.updateProfile(user);
        return Result.success("更新成功", result);
    }
    
    @PostMapping("/user/update")
    public Result<UserInfoVO> updateProfilePost(@RequestBody User user) {
        UserInfoVO result = userService.updateProfile(user);
        return Result.success("更新成功", result);
    }
    
    @PutMapping("/user/password")
    public Result<Void> changePasswordPut(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success("密码修改成功", null);
    }
    
    @PostMapping("/user/change-password")
    public Result<Void> changePasswordPost(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success("密码修改成功", null);
    }
    
    @PostMapping("/user/avatar")
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        if (!".jpg".equalsIgnoreCase(ext) && !".jpeg".equalsIgnoreCase(ext) && !".png".equalsIgnoreCase(ext)) {
            return Result.error("只支持 JPG、PNG 格式");
        }
        
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File uploadDir = new File(uploadPath + "avatar/" + datePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        File destFile = new File(uploadDir, fileName);
        file.transferTo(destFile);
        
        String fileUrl = urlPrefix + "avatar/" + datePath + "/" + fileName;
        java.util.Map<String, String> result = userService.uploadAvatar(fileUrl);
        
        return Result.success("上传成功", result);
    }
}
