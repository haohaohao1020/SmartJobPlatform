package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_resume_project_experience")
public class ResumeProjectExperience {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    private String projectName;
    private String role;
    private String startDate;
    private String endDate;
    private Integer isCurrent;
    private String projectLink;
    private String projectDescription;
    private String responsibilities;
    private String achievements;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
