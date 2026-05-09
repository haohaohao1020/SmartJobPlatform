package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_resume_work_experience")
public class ResumeWorkExperience {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    private String companyName;
    private String department;
    private String position;
    private String startDate;
    private String endDate;
    private Integer isCurrent;
    private String salary;
    private String workDescription;
    private String achievements;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
