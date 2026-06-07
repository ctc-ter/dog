package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.dto.R;
import com.dogrescue.entity.Volunteer;
import com.dogrescue.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping
    public R<PageResult<Volunteer>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return R.ok(volunteerService.listVolunteers(page, size, status));
    }

    @PostMapping("/apply")
    public R<Void> apply(@RequestBody Volunteer volunteer) {
        volunteerService.apply(volunteer);
        return R.ok();
    }

    @PostMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        volunteerService.audit(id, params.get("status"));
        return R.ok();
    }
}
