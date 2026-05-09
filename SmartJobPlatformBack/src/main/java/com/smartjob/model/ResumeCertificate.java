package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_resume_certificate")
public class ResumeCertificate {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    private String certificateName;
    private String issuer;
    private String obtainDate;
    private String expireDate;
    private Integer isPermanent;
    private String certificateUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
