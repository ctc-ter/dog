package com.dogrescue.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResultTest {

    @Test
    void testConstructorWithItems() {
        List<String> items = Arrays.asList("a", "b", "c");
        PageResult<String> result = new PageResult<>(3L, items);
        assertEquals(3L, result.getTotal());
        assertEquals(3, result.getList().size());
        assertEquals("a", result.getList().get(0));
    }

    @Test
    void testConstructorWithEmptyList() {
        PageResult<String> result = new PageResult<>(0L, Collections.emptyList());
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        PageResult<Integer> result = new PageResult<>(0L, Collections.emptyList());
        result.setTotal(10L);
        result.setList(Arrays.asList(1, 2, 3));
        assertEquals(10L, result.getTotal());
        assertEquals(3, result.getList().size());
    }

    @Test
    void testTotalCanBeLargerThanListSize() {
        // 模拟分页：总共100条，当前页返回10条
        List<String> partial = Collections.nCopies(10, "item");
        PageResult<String> result = new PageResult<>(100L, partial);
        assertEquals(100L, result.getTotal());
        assertEquals(10, result.getList().size());
    }
}
