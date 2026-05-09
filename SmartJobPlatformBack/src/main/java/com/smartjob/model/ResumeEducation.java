package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_resume_education")
public class ResumeEducation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    private String schoolName;
    private String major;
    private String education;
    private String startDate;
    private String endDate;
    private Integer isCurrent;
    private String gpa;
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
