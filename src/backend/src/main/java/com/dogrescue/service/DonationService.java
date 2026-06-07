package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Donation;
import com.dogrescue.mapper.DonationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DonationService extends ServiceImpl<DonationMapper, Donation> {

    public PageResult<Donation> listDonations(Integer page, Integer size, String status) {
        Page<Donation> p = new Page<>(page, size);
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Donation::getStatus, status);
        }
        wrapper.orderByDesc(Donation::getDonateTime);
        Page<Donation> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public void createDonation(Donation donation) {
        donation.setStatus("待支付");
        donation.setDonateTime(LocalDateTime.now());
        save(donation);
    }

    public void updateStatus(Long id, String status, String transactionId) {
        Donation donation = getById(id);
        if (donation == null) {
            throw new RuntimeException("捐赠记录不存在");
        }
        donation.setStatus(status);
        donation.setTransactionId(transactionId);
        updateById(donation);
    }
}
