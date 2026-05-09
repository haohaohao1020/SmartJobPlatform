package com.smartjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartjob.model.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobMapper extends BaseMapper<Job> {
}
