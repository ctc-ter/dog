package com.dogrescue.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "dog-rescue-jwt-secret-key-2024-very-long-and-secure");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void testGenerateTokenNotNull() {
        String token = jwtUtil.generateToken("admin");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtUtil.generateToken("testuser");
        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void testValidateTokenReturnsTrue() {
        String token = jwtUtil.generateToken("admin");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateTokenReturnsFalseForInvalid() {
        assertFalse(jwtUtil.validateToken("invalid.token.string"));
    }

    @Test
    void testValidateTokenReturnsFalseForNull() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void testValidateTokenReturnsFalseForEmpty() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void testDifferentUsernamesProduceDifferentTokens() {
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");
        assertNotEquals(token1, token2);
    }

    @Test
    void testExpiredTokenIsInvalid() {
        // 设置过期时间为0，使token立即过期
        ReflectionTestUtils.setField(jwtUtil, "expiration", 0L);
        String token = jwtUtil.generateToken("admin");
        // token立即过期，验证应返回false
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void testSameUsernameGeneratesDifferentTokens() {
        // 由于时间戳不同，同一用户两次生成的token应不同
        String token1 = jwtUtil.generateToken("admin");
        // 确保时间不同
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        String token2 = jwtUtil.generateToken("admin");
        assertNotEquals(token1, token2);
        // 但都能解析出同一用户名
        assertEquals("admin", jwtUtil.getUsernameFromToken(token1));
        assertEquals("admin", jwtUtil.getUsernameFromToken(token2));
    }
}
