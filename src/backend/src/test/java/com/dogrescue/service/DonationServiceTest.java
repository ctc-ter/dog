package com.dogrescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Donation;
import com.dogrescue.mapper.DonationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationMapper donationMapper;

    @Spy
    @InjectMocks
    private DonationService donationService;

    @Test
    void testListDonationsNoFilter() {
        Donation d = new Donation();
        d.setId(1L);
        d.setDonorName("王五");
        d.setAmount(new BigDecimal("100.00"));
        d.setStatus("已支付");

        Page<Donation> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(d));
        mockPage.setTotal(1);

        doReturn(mockPage).when(donationService).page(any(Page.class), any());

        PageResult<Donation> result = donationService.listDonations(1, 10, null);
        assertEquals(1, result.getTotal());
        assertEquals("王五", result.getList().get(0).getDonorName());
    }

    @Test
    void testListDonationsWithStatusFilter() {
        Page<Donation> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(donationService).page(any(Page.class), any());

        PageResult<Donation> result = donationService.listDonations(1, 10, "待支付");
        assertEquals(0, result.getTotal());
    }

    @Test
    void testCreateDonation() {
        Donation donation = new Donation();
        donation.setDonorName("赵六");
        donation.setAmount(new BigDecimal("50.00"));
        donation.setPaymentMethod("支付宝");

        doReturn(true).when(donationService).save(any(Donation.class));

        donationService.createDonation(donation);

        assertEquals("待支付", donation.getStatus());
        assertNotNull(donation.getDonateTime());
        verify(donationService).save(donation);
    }

    @Test
    void testUpdateStatusSuccess() {
        Donation donation = new Donation();
        donation.setId(1L);
        donation.setStatus("待支付");

        doReturn(donation).when(donationService).getById(1L);
        doReturn(true).when(donationService).updateById(any(Donation.class));

        donationService.updateStatus(1L, "已支付", "TXN123456");

        assertEquals("已支付", donation.getStatus());
        assertEquals("TXN123456", donation.getTransactionId());
        verify(donationService).updateById(donation);
    }

    @Test
    void testUpdateStatusNotFound() {
        doReturn(null).when(donationService).getById(999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> donationService.updateStatus(999L, "已支付", "TXN"));
        assertEquals("捐赠记录不存在", ex.getMessage());
    }
}
