package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.dto.R;
import com.dogrescue.entity.LostFound;
import com.dogrescue.service.LostFoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lost-found")
public class LostFoundController {

    @Autowired
    private LostFoundService lostFoundService;

    @GetMapping
    public R<PageResult<LostFound>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return R.ok(lostFoundService.listItems(page, size, type, status));
    }

    @PostMapping
    public R<Void> publish(@RequestBody LostFound item) {
        lostFoundService.publish(item);
        return R.ok();
    }

    @PostMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        lostFoundService.updateStatus(id, params.get("status"));
        return R.ok();
    }
}
