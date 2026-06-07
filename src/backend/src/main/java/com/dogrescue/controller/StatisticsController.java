package com.dogrescue.controller;

import com.dogrescue.dto.R;
import com.dogrescue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private DogService dogService;
    
    @Autowired
    private AdoptionService adoptionService;
    
    @Autowired
    private VolunteerService volunteerService;
    
    @Autowired
    private DonationService donationService;

    @GetMapping("/home")
    public R<Map<String, Object>> getHomeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 已救助狗狗总数
        stats.put("rescuedDogs", dogService.count());
        
        // 成功领养数量（状态为"已领养"的狗狗）
        long adoptedDogs = dogService.lambdaQuery()
                .eq(com.dogrescue.entity.Dog::getStatus, "已领养")
                .count();
        stats.put("adoptedDogs", adoptedDogs);
        
        // 活跃志愿者（状态为"已通过"或"活跃中"）
        long activeVolunteers = volunteerService.lambdaQuery()
                .in(com.dogrescue.entity.Volunteer::getStatus, "已通过", "活跃中")
                .count();
        stats.put("activeVolunteers", activeVolunteers);
        
        // 本月捐款总额
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal monthlyDonations = donationService.lambdaQuery()
                .ge(com.dogrescue.entity.Donation::getDonateTime, startOfMonth)
                .eq(com.dogrescue.entity.Donation::getStatus, "已支付")
                .list()
                .stream()
                .map(com.dogrescue.entity.Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("monthlyDonations", monthlyDonations);
        
        return R.ok(stats);
    }
}
