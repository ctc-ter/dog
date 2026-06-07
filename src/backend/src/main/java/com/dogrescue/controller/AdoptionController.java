package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.dto.R;
import com.dogrescue.entity.Adoption;
import com.dogrescue.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @GetMapping
    public R<PageResult<Adoption>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return R.ok(adoptionService.listAdoptions(page, size, status));
    }

    @PostMapping("/apply")
    public R<Void> apply(@RequestBody Adoption adoption) {
        adoptionService.apply(adoption);
        return R.ok();
    }

    @PostMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        adoptionService.audit(id, params.get("status"), params.get("remark"));
        return R.ok();
    }
}
