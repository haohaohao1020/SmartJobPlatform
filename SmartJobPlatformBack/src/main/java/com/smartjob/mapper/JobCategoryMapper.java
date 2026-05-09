package com.smartjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartjob.model.JobCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobCategoryMapper extends BaseMapper<JobCategory> {
}
