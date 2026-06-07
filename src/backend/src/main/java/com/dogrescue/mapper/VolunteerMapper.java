package com.dogrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dogrescue.entity.Volunteer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VolunteerMapper extends BaseMapper<Volunteer> {
}
