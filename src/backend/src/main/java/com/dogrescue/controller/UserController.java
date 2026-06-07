package com.dogrescue.controller;

import com.dogrescue.dto.R;
import com.dogrescue.entity.User;
import com.dogrescue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public R<List<User>> list() {
        return R.ok(userService.list());
    }

    @PostMapping
    public R<Void> create(@RequestBody User user) {
        userService.createUser(user);
        return R.ok();
    }
}
