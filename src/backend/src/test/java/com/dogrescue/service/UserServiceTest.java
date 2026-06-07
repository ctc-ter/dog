package com.dogrescue.service;

import com.dogrescue.entity.User;
import com.dogrescue.mapper.UserMapper;
import com.dogrescue.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Spy
    @InjectMocks
    private UserService userService;

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswordHash("$2a$10$hashed");
        user.setRole("admin");

        doReturn(user).when(userService).getOne(any());
        when(passwordEncoder.matches("password123", "$2a$10$hashed")).thenReturn(true);
        doReturn(true).when(userService).updateById(any(User.class));
        when(jwtUtil.generateToken("admin")).thenReturn("mock-jwt-token");

        Map<String, Object> result = userService.login("admin", "password123");

        assertEquals("mock-jwt-token", result.get("token"));
        assertEquals("admin", result.get("role"));
        assertEquals("admin", result.get("username"));
        assertNotNull(user.getLastLogin());
    }

    @Test
    void testLoginWrongPassword() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("$2a$10$hashed");

        doReturn(user).when(userService).getOne(any());
        when(passwordEncoder.matches("wrong", "$2a$10$hashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("admin", "wrong"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        doReturn(null).when(userService).getOne(any());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("nobody", "pass"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setPasswordHash("plain_password");
        user.setRole("user");

        when(passwordEncoder.encode("plain_password")).thenReturn("$2a$10$encoded");
        doReturn(true).when(userService).save(any(User.class));

        userService.createUser(user);

        assertEquals("$2a$10$encoded", user.getPasswordHash());
        assertNotNull(user.getCreateTime());
        verify(userService).save(user);
    }
}
