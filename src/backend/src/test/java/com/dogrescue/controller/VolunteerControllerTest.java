package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Volunteer;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.service.VolunteerService;
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

@WebMvcTest(VolunteerController.class)
@Import(SecurityConfig.class)
class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListVolunteers() throws Exception {
        Volunteer v = new Volunteer();
        v.setId(1L);
        v.setName("陈七");
        v.setStatus("已通过");

        PageResult<Volunteer> pageResult = new PageResult<>(1L, List.of(v));
        when(volunteerService.listVolunteers(1, 10, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("陈七"));
    }

    @Test
    void testApply() throws Exception {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("周八");
        volunteer.setPhone("13700137000");
        volunteer.setEmail("zhou@example.com");
        volunteer.setSkills("动物护理");

        doNothing().when(volunteerService).apply(any(Volunteer.class));

        mockMvc.perform(post("/api/volunteers/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(volunteer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(volunteerService).apply(any(Volunteer.class));
    }

    @Test
    void testAudit() throws Exception {
        doNothing().when(volunteerService).audit(eq(1L), eq("已通过"));

        Map<String, String> params = Map.of("status", "已通过");

        mockMvc.perform(post("/api/volunteers/1/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(volunteerService).audit(1L, "已通过");
    }

    @Test
    void testAuditNotFound() throws Exception {
        doThrow(new RuntimeException("记录不存在"))
                .when(volunteerService).audit(eq(999L), anyString());

        Map<String, String> params = Map.of("status", "已通过");

        mockMvc.perform(post("/api/volunteers/999/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("记录不存在"));
    }
}
