package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.LostFound;
import com.dogrescue.mapper.LostFoundMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LostFoundService extends ServiceImpl<LostFoundMapper, LostFound> {

    public PageResult<LostFound> listItems(Integer page, Integer size, String type, String status) {
        Page<LostFound> p = new Page<>(page, size);
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(LostFound::getType, type);
        }
        if (status != null) {
            wrapper.eq(LostFound::getStatus, status);
        }
        wrapper.orderByDesc(LostFound::getCreateTime);
        Page<LostFound> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public void publish(LostFound item) {
        item.setStatus("进行中");
        item.setCreateTime(LocalDateTime.now());
        save(item);
    }

    public void updateStatus(Long id, String status) {
        LostFound item = getById(id);
        if (item == null) {
            throw new RuntimeException("记录不存在");
        }
        item.setStatus(status);
        updateById(item);
    }
}
