package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Dog;
import com.dogrescue.mapper.DogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DogService extends ServiceImpl<DogMapper, Dog> {

    public PageResult<Dog> listDogs(Integer page, Integer size, String breed, String gender, String status) {
        Page<Dog> p = new Page<>(page, size);
        LambdaQueryWrapper<Dog> wrapper = new LambdaQueryWrapper<>();
        if (breed != null && !breed.isBlank()) {
            wrapper.eq(Dog::getBreed, breed);
        }
        if (gender != null && !gender.isBlank()) {
            wrapper.eq(Dog::getGender, gender);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(Dog::getStatus, status);
        }
        wrapper.orderByDesc(Dog::getCreateTime);
        Page<Dog> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public void addDog(Dog dog) {
        dog.setCreateTime(LocalDateTime.now());
        dog.setUpdateTime(LocalDateTime.now());
        save(dog);
    }

    public void updateDog(Dog dog) {
        dog.setUpdateTime(LocalDateTime.now());
        updateById(dog);
    }
}
