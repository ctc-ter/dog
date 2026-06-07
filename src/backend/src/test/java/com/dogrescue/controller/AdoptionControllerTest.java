package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Adoption;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.service.AdoptionService;
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

@WebMvcTest(AdoptionController.class)
@Import(SecurityConfig.class)
class AdoptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdoptionService adoptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListAdoptions() throws Exception {
        Adoption a = new Adoption();
        a.setId(1L);
        a.setApplicantName("张三");
        a.setStatus("待审核");

        PageResult<Adoption> pageResult = new PageResult<>(1L, List.of(a));
        when(adoptionService.listAdoptions(1, 10, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/adoptions")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].applicantName").value("张三"));
    }

    @Test
    void testApply() throws Exception {
        Adoption adoption = new Adoption();
        adoption.setDogId(1L);
        adoption.setApplicantName("李四");
        adoption.setPhone("13800138000");
        adoption.setReason("想养狗");

        doNothing().when(adoptionService).apply(any(Adoption.class));

        mockMvc.perform(post("/api/adoptions/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoption)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adoptionService).apply(any(Adoption.class));
    }

    @Test
    void testAudit() throws Exception {
        doNothing().when(adoptionService).audit(eq(1L), eq("已通过"), eq("条件符合"));

        Map<String, String> params = Map.of("status", "已通过", "remark", "条件符合");

        mockMvc.perform(post("/api/adoptions/1/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adoptionService).audit(1L, "已通过", "条件符合");
    }

    @Test
    void testAuditNotFound() throws Exception {
        doThrow(new RuntimeException("申请记录不存在"))
                .when(adoptionService).audit(eq(999L), anyString(), anyString());

        Map<String, String> params = Map.of("status", "已通过", "remark", "");

        mockMvc.perform(post("/api/adoptions/999/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("申请记录不存在"));
    }
}
