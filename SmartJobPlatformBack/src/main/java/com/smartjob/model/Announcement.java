package com.smartjob.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String content;
    private Integer type;
    private String typeName;
    private String coverImage;
    private String summary;
    private String author;
    private Integer isTop;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime publishTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
