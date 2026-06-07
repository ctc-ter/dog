package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Donation;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.service.DonationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationController.class)
@Import(SecurityConfig.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonationService donationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListDonations() throws Exception {
        Donation d = new Donation();
        d.setId(1L);
        d.setDonorName("王五");
        d.setAmount(new BigDecimal("100.00"));
        d.setStatus("已支付");

        PageResult<Donation> pageResult = new PageResult<>(1L, List.of(d));
        when(donationService.listDonations(1, 10, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].donorName").value("王五"));
    }

    @Test
    void testCreateDonation() throws Exception {
        Donation donation = new Donation();
        donation.setDonorName("赵六");
        donation.setAmount(new BigDecimal("50.00"));
        donation.setPaymentMethod("支付宝");

        doNothing().when(donationService).createDonation(any(Donation.class));

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(donationService).createDonation(any(Donation.class));
    }

    @Test
    void testCallback() throws Exception {
        doNothing().when(donationService).updateStatus(eq(1L), eq("已支付"), eq("TXN123"));

        Map<String, String> params = Map.of("status", "已支付", "transactionId", "TXN123");

        mockMvc.perform(post("/api/donations/1/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(donationService).updateStatus(1L, "已支付", "TXN123");
    }

    @Test
    void testCallbackNotFound() throws Exception {
        doThrow(new RuntimeException("捐赠记录不存在"))
                .when(donationService).updateStatus(eq(999L), anyString(), anyString());

        Map<String, String> params = Map.of("status", "已支付", "transactionId", "TXN");

        mockMvc.perform(post("/api/donations/999/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("捐赠记录不存在"));
    }
}
