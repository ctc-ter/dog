package com.dogrescue.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.entity.Dog;
import com.dogrescue.entity.Donation;
import com.dogrescue.entity.Volunteer;
import com.dogrescue.service.AdoptionService;
import com.dogrescue.service.DogService;
import com.dogrescue.service.DonationService;
import com.dogrescue.service.VolunteerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@Import(SecurityConfig.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogService dogService;

    @MockBean
    private AdoptionService adoptionService;

    @MockBean
    private VolunteerService volunteerService;

    @MockBean
    private DonationService donationService;

    @SuppressWarnings("unchecked")
    @Test
    void testGetHomeStats() throws Exception {
        // 模拟 dogService.count()
        when(dogService.count()).thenReturn(50L);

        // 模拟 dogService.lambdaQuery().eq(status, "已领养").count()
        LambdaQueryChainWrapper<Dog> dogChain = mock(LambdaQueryChainWrapper.class);
        when(dogService.lambdaQuery()).thenReturn(dogChain);
        when(dogChain.eq(any(), any())).thenReturn(dogChain);
        when(dogChain.count()).thenReturn(20L);

        // 模拟 volunteerService.lambdaQuery().in(status, ...).count()
        LambdaQueryChainWrapper<Volunteer> volChain = mock(LambdaQueryChainWrapper.class);
        when(volunteerService.lambdaQuery()).thenReturn(volChain);
        when(volChain.in(any(), any(java.util.Collection.class))).thenReturn(volChain);
        when(volChain.in(any(), any(Object[].class))).thenReturn(volChain);
        when(volChain.count()).thenReturn(15L);

        // 模拟 donationService.lambdaQuery().ge(...).eq(...).list()
        Donation d1 = new Donation();
        d1.setAmount(new BigDecimal("100.00"));
        Donation d2 = new Donation();
        d2.setAmount(new BigDecimal("200.00"));

        LambdaQueryChainWrapper<Donation> donChain = mock(LambdaQueryChainWrapper.class);
        when(donationService.lambdaQuery()).thenReturn(donChain);
        when(donChain.ge(any(), any())).thenReturn(donChain);
        when(donChain.eq(any(), any())).thenReturn(donChain);
        when(donChain.list()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/stats/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rescuedDogs").value(50))
                .andExpect(jsonPath("$.data.adoptedDogs").value(20))
                .andExpect(jsonPath("$.data.activeVolunteers").value(15))
                .andExpect(jsonPath("$.data.monthlyDonations").value(300.00));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetHomeStatsWithZeroDonations() throws Exception {
        when(dogService.count()).thenReturn(0L);

        LambdaQueryChainWrapper<Dog> dogChain = mock(LambdaQueryChainWrapper.class);
        when(dogService.lambdaQuery()).thenReturn(dogChain);
        when(dogChain.eq(any(), any())).thenReturn(dogChain);
        when(dogChain.count()).thenReturn(0L);

        LambdaQueryChainWrapper<Volunteer> volChain2 = mock(LambdaQueryChainWrapper.class);
        when(volunteerService.lambdaQuery()).thenReturn(volChain2);
        when(volChain2.in(any(), any(java.util.Collection.class))).thenReturn(volChain2);
        when(volChain2.in(any(), any(Object[].class))).thenReturn(volChain2);
        when(volChain2.count()).thenReturn(0L);

        LambdaQueryChainWrapper<Donation> donChain = mock(LambdaQueryChainWrapper.class);
        when(donationService.lambdaQuery()).thenReturn(donChain);
        when(donChain.ge(any(), any())).thenReturn(donChain);
        when(donChain.eq(any(), any())).thenReturn(donChain);
        when(donChain.list()).thenReturn(List.of());

        mockMvc.perform(get("/api/stats/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rescuedDogs").value(0))
                .andExpect(jsonPath("$.data.adoptedDogs").value(0))
                .andExpect(jsonPath("$.data.activeVolunteers").value(0))
                .andExpect(jsonPath("$.data.monthlyDonations").value(0));
    }
}
