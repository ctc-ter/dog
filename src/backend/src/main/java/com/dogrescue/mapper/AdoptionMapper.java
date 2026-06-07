package com.dogrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dogrescue.entity.Adoption;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdoptionMapper extends BaseMapper<Adoption> {
}
