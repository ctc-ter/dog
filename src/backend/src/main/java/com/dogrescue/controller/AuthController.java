package com.dogrescue.controller;

import com.dogrescue.dto.R;
import com.dogrescue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = userService.login(params.get("username"), params.get("password"));
        return R.ok(result);
    }
}
