package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_resume")
public class Resume {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String realName;
    private Integer gender;
    private LocalDate birthday;
    private String avatar;
    private String phone;
    private String email;
    private String currentCity;
    private String workStatus;
    private String workYears;
    private String jobIntention;
    private Integer expectedSalaryMin;
    private Integer expectedSalaryMax;
    private String expectedCity;
    private String currentSalary;
    private String selfEvaluation;
    private Integer isPublic;
    private LocalDateTime refreshTime;
    private Integer viewCount;
    
    @TableField(exist = false)
    private List<ResumeEducation> educations;
    
    @TableField(exist = false)
    private List<ResumeWorkExperience> workExperiences;
    
    @TableField(exist = false)
    private List<ResumeProjectExperience> projectExperiences;
    
    @TableField(exist = false)
    private List<ResumeSkill> skills;
    
    @TableField(exist = false)
    private List<ResumeCertificate> certificates;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
