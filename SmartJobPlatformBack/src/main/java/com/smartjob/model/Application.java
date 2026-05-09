package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_application")
public class Application {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long jobId;
    private String jobTitle;
    private Long resumeId;
    private Long hrId;
    private String hrName;
    private Long companyId;
    private String companyName;
    private String companyLogo;
    private Integer salaryMin;
    private Integer salaryMax;
    private String city;
    private String workYears;
    private String education;
    private String applyTime;
    private Integer status;
    private String statusName;
    private Integer isRead;
    private LocalDateTime interviewTime;
    private String interviewAddress;
    private String interviewContact;
    private String interviewPhone;
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
