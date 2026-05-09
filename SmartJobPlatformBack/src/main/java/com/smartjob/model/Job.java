package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_job")
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hrId;
    private Long companyId;
    private String companyName;
    private String companyLogo;
    private String title;
    private String description;
    private String requirement;
    private String keywords;
    private String industry;
    private String category;
    private String subCategory;
    private String province;
    private String city;
    private String district;
    private String address;
    private Integer salaryMin;
    private Integer salaryMax;
    private String salaryType;
    private Integer salaryMonths;
    private String workYears;
    private String education;
    private String jobType;
    private String tags;
    private String emergencyDegree;
    private String hrName;
    private String hrAvatar;
    private String hrTitle;
    private Integer viewCount;
    private Integer applyCount;
    private Integer collectCount;
    private Integer isHot;
    private LocalDateTime hotStartTime;
    private LocalDateTime hotEndTime;
    private Integer isTop;
    private LocalDateTime topStartTime;
    private LocalDateTime topEndTime;
    private Integer auditStatus;
    private String auditRemark;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
