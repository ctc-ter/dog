package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Story;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.service.StoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoryController.class)
@Import(SecurityConfig.class)
class StoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoryService storyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListStories() throws Exception {
        Story s = new Story();
        s.setId(1L);
        s.setTitle("救助小花");
        s.setAuthor("志愿者A");

        PageResult<Story> pageResult = new PageResult<>(1L, List.of(s));
        when(storyService.listStories(1, 10)).thenReturn(pageResult);

        mockMvc.perform(get("/api/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("救助小花"));
    }

    @Test
    void testGetById() throws Exception {
        Story s = new Story();
        s.setId(1L);
        s.setTitle("救助小花");
        s.setContent("故事内容...");

        when(storyService.getById(1L)).thenReturn(s);

        mockMvc.perform(get("/api/stories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("救助小花"));
    }

    @Test
    void testAddStory() throws Exception {
        Story story = new Story();
        story.setTitle("新故事");
        story.setContent("内容");
        story.setAuthor("作者");

        doNothing().when(storyService).addStory(any(Story.class));

        mockMvc.perform(post("/api/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(story)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(storyService).addStory(any(Story.class));
    }

    @Test
    void testUpdateStory() throws Exception {
        Story story = new Story();
        story.setTitle("更新标题");

        doNothing().when(storyService).updateStory(any(Story.class));

        mockMvc.perform(put("/api/stories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(story)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(storyService).updateStory(argThat(s -> s.getId().equals(1L)));
    }

    @Test
    void testDeleteStory() throws Exception {
        when(storyService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/stories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(storyService).removeById(1L);
    }
}
