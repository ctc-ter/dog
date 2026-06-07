package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Volunteer;
import com.dogrescue.mapper.VolunteerMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VolunteerService extends ServiceImpl<VolunteerMapper, Volunteer> {

    public PageResult<Volunteer> listVolunteers(Integer page, Integer size, String status) {
        Page<Volunteer> p = new Page<>(page, size);
        LambdaQueryWrapper<Volunteer> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Volunteer::getStatus, status);
        }
        wrapper.orderByDesc(Volunteer::getApplyTime);
        Page<Volunteer> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public void apply(Volunteer volunteer) {
        volunteer.setStatus("待审核");
        volunteer.setApplyTime(LocalDateTime.now());
        save(volunteer);
    }

    public void audit(Long id, String status) {
        Volunteer volunteer = getById(id);
        if (volunteer == null) {
            throw new RuntimeException("记录不存在");
        }
        volunteer.setStatus(status);
        updateById(volunteer);
    }
}
