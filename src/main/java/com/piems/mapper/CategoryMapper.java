package com.piems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piems.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}