package com.smartjob.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.smartjob.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {
    
    @Value("${smartjob.upload.path:E:/LLMcode/SmartJobPlatform/uploads/}")
    private String uploadPath;
    
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        return handleUpload(file, "files");
    }
    
    @PostMapping("/upload/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return handleUpload(file, "avatars");
    }
    
    @PostMapping("/upload/logo")
    public Result<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) {
        return handleUpload(file, "logos");
    }
    
    @PostMapping("/upload/license")
    public Result<Map<String, String>> uploadLicense(@RequestParam("file") MultipartFile file) {
        return handleUpload(file, "licenses");
    }
    
    @PostMapping("/upload/resume")
    public Result<Map<String, String>> uploadResumeFile(@RequestParam("file") MultipartFile file) {
        return handleUpload(file, "resumes");
    }
    
    private Result<Map<String, String>> handleUpload(MultipartFile file, String subDir) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);
            
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String dirPath = uploadPath + subDir + "/" + dateDir + "/";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String newFilename = IdUtil.simpleUUID() + "." + extension;
            File destFile = new File(dirPath + newFilename);
            file.transferTo(destFile);
            
            String accessUrl = "/uploads/" + subDir + "/" + dateDir + "/" + newFilename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", accessUrl);
            result.put("fileName", newFilename);
            result.put("originalName", originalFilename);
            result.put("filePath", destFile.getAbsolutePath());
            
            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
