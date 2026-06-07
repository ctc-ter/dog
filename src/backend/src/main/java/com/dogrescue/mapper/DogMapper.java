package com.dogrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dogrescue.entity.Dog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DogMapper extends BaseMapper<Dog> {
}
