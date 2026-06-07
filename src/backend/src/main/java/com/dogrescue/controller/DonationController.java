package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.dto.R;
import com.dogrescue.entity.Donation;
import com.dogrescue.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping
    public R<PageResult<Donation>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return R.ok(donationService.listDonations(page, size, status));
    }

    @PostMapping
    public R<Void> create(@RequestBody Donation donation) {
        donationService.createDonation(donation);
        return R.ok();
    }

    @PostMapping("/{id}/callback")
    public R<Void> callback(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        donationService.updateStatus(id, params.get("status"), params.get("transactionId"));
        return R.ok();
    }
}
