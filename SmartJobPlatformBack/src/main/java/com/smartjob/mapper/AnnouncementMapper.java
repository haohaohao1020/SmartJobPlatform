package com.smartjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartjob.model.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
