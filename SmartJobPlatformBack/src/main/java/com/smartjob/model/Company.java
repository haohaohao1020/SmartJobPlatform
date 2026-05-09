package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_company")
public class Company {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String name;
    private String shortName;
    private String logo;
    private String licenseUrl;
    private String industry;
    private String scale;
    private String type;
    private String establishedDate;
    private String registeredCapital;
    private String legalPerson;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String province;
    private String city;
    private String district;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private String benefits;
    private String website;
    private Integer viewCount;
    private Integer jobCount;
    private Integer auditStatus;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
