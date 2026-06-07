package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Story;
import com.dogrescue.mapper.StoryMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StoryService extends ServiceImpl<StoryMapper, Story> {

    public PageResult<Story> listStories(Integer page, Integer size) {
        Page<Story> p = new Page<>(page, size);
        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Story::getPublishTime);
        Page<Story> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public void addStory(Story story) {
        story.setPublishTime(LocalDateTime.now());
        save(story);
    }

    public void updateStory(Story story) {
        updateById(story);
    }
}
