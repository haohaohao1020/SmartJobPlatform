package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String companyLogo;
    private Integer salaryMin;
    private Integer salaryMax;
    private String city;
    private String workYears;
    private String education;
    private String tags;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
