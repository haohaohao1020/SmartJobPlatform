package com.smartjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartjob.model.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
