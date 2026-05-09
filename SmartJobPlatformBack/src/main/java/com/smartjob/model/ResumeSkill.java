package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_resume_skill")
public class ResumeSkill {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    private String skillName;
    private String proficiency;
    private Integer years;
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
