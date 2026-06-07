package com.dogrescue.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RTest {

    @Test
    void testOkWithoutData() {
        R<Void> r = R.ok();
        assertEquals(200, r.getCode());
        assertEquals("success", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void testOkWithData() {
        String data = "test data";
        R<String> r = R.ok(data);
        assertEquals(200, r.getCode());
        assertEquals("success", r.getMessage());
        assertEquals("test data", r.getData());
    }

    @Test
    void testOkWithNullData() {
        R<Object> r = R.ok(null);
        assertEquals(200, r.getCode());
        assertNull(r.getData());
    }

    @Test
    void testErrorWithMessage() {
        R<Void> r = R.error("出错了");
        assertEquals(500, r.getCode());
        assertEquals("出错了", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void testErrorWithCodeAndMessage() {
        R<Void> r = R.error(404, "未找到");
        assertEquals(404, r.getCode());
        assertEquals("未找到", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void testSettersAndGetters() {
        R<Integer> r = new R<>();
        r.setCode(201);
        r.setMessage("created");
        r.setData(42);
        assertEquals(201, r.getCode());
        assertEquals("created", r.getMessage());
        assertEquals(42, r.getData());
    }
}
