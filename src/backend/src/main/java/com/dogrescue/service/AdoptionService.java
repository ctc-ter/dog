package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Adoption;
import com.dogrescue.mapper.AdoptionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdoptionService extends ServiceImpl<AdoptionMapper, Adoption> {

    public PageResult<Adoption> listAdoptions(Integer page, Integer size, String status) {
        Page<Adoption> p = new Page<>(page, size);
        LambdaQueryWrapper<Adoption> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Adoption::getStatus, status);
        }
        wrapper.orderByDesc(Adoption::getApplyTime);
        Page<Adoption> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public void apply(Adoption adoption) {
        adoption.setStatus("待审核");
        adoption.setApplyTime(LocalDateTime.now());
        save(adoption);
    }

    public void audit(Long id, String status, String remark) {
        Adoption adoption = getById(id);
        if (adoption == null) {
            throw new RuntimeException("申请记录不存在");
        }
        adoption.setStatus(status);
        adoption.setRemark(remark);
        adoption.setAuditTime(LocalDateTime.now());
        updateById(adoption);
    }
}
