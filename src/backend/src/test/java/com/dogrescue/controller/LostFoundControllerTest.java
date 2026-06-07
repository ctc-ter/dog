package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.LostFound;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.service.LostFoundService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LostFoundController.class)
@Import(SecurityConfig.class)
class LostFoundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LostFoundService lostFoundService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListItems() throws Exception {
        LostFound item = new LostFound();
        item.setId(1L);
        item.setType("寻狗");
        item.setTitle("寻找旺财");
        item.setStatus("进行中");

        PageResult<LostFound> pageResult = new PageResult<>(1L, List.of(item));
        when(lostFoundService.listItems(1, 10, null, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/lost-found"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("寻找旺财"));
    }

    @Test
    void testListItemsWithFilters() throws Exception {
        PageResult<LostFound> pageResult = new PageResult<>(0L, List.of());
        when(lostFoundService.listItems(1, 10, "招领", "已结束")).thenReturn(pageResult);

        mockMvc.perform(get("/api/lost-found")
                        .param("type", "招领")
                        .param("status", "已结束"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void testPublish() throws Exception {
        LostFound item = new LostFound();
        item.setType("寻狗");
        item.setTitle("寻找走失的Lucky");
        item.setContactName("张三");
        item.setContactPhone("13800138000");

        doNothing().when(lostFoundService).publish(any(LostFound.class));

        mockMvc.perform(post("/api/lost-found")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lostFoundService).publish(any(LostFound.class));
    }

    @Test
    void testUpdateStatus() throws Exception {
        doNothing().when(lostFoundService).updateStatus(eq(1L), eq("已结束"));

        Map<String, String> params = Map.of("status", "已结束");

        mockMvc.perform(post("/api/lost-found/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lostFoundService).updateStatus(1L, "已结束");
    }

    @Test
    void testUpdateStatusNotFound() throws Exception {
        doThrow(new RuntimeException("记录不存在"))
                .when(lostFoundService).updateStatus(eq(999L), anyString());

        Map<String, String> params = Map.of("status", "已结束");

        mockMvc.perform(post("/api/lost-found/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("记录不存在"));
    }
}
