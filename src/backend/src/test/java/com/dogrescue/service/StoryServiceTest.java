package com.dogrescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Story;
import com.dogrescue.mapper.StoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoryServiceTest {

    @Mock
    private StoryMapper storyMapper;

    @Spy
    @InjectMocks
    private StoryService storyService;

    @Test
    void testListStories() {
        Story s = new Story();
        s.setId(1L);
        s.setTitle("救助小花的故事");
        s.setAuthor("志愿者A");

        Page<Story> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(s));
        mockPage.setTotal(1);

        doReturn(mockPage).when(storyService).page(any(Page.class), any());

        PageResult<Story> result = storyService.listStories(1, 10);
        assertEquals(1, result.getTotal());
        assertEquals("救助小花的故事", result.getList().get(0).getTitle());
    }

    @Test
    void testListStoriesEmpty() {
        Page<Story> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(storyService).page(any(Page.class), any());

        PageResult<Story> result = storyService.listStories(1, 10);
        assertEquals(0, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    void testAddStory() {
        Story story = new Story();
        story.setTitle("新故事");
        story.setContent("内容...");
        story.setAuthor("作者");

        doReturn(true).when(storyService).save(any(Story.class));

        storyService.addStory(story);

        assertNotNull(story.getPublishTime());
        verify(storyService).save(story);
    }

    @Test
    void testUpdateStory() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle("更新后的标题");

        doReturn(true).when(storyService).updateById(any(Story.class));

        storyService.updateStory(story);

        verify(storyService).updateById(story);
    }
}
