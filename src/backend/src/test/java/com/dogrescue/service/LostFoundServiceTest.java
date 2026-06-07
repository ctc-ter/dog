package com.dogrescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.LostFound;
import com.dogrescue.mapper.LostFoundMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LostFoundServiceTest {

    @Mock
    private LostFoundMapper lostFoundMapper;

    @Spy
    @InjectMocks
    private LostFoundService lostFoundService;

    @Test
    void testListItemsNoFilter() {
        LostFound item = new LostFound();
        item.setId(1L);
        item.setType("寻狗");
        item.setTitle("寻找走失的旺财");
        item.setStatus("进行中");

        Page<LostFound> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(item));
        mockPage.setTotal(1);

        doReturn(mockPage).when(lostFoundService).page(any(Page.class), any());

        PageResult<LostFound> result = lostFoundService.listItems(1, 10, null, null);
        assertEquals(1, result.getTotal());
        assertEquals("寻找走失的旺财", result.getList().get(0).getTitle());
    }

    @Test
    void testListItemsWithTypeFilter() {
        Page<LostFound> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(lostFoundService).page(any(Page.class), any());

        PageResult<LostFound> result = lostFoundService.listItems(1, 10, "招领", null);
        assertEquals(0, result.getTotal());
    }

    @Test
    void testListItemsWithStatusFilter() {
        Page<LostFound> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(lostFoundService).page(any(Page.class), any());

        PageResult<LostFound> result = lostFoundService.listItems(1, 10, null, "已结束");
        assertEquals(0, result.getTotal());
    }

    @Test
    void testPublish() {
        LostFound item = new LostFound();
        item.setType("寻狗");
        item.setTitle("寻找走失的Lucky");
        item.setContactName("张三");
        item.setContactPhone("13800138000");

        doReturn(true).when(lostFoundService).save(any(LostFound.class));

        lostFoundService.publish(item);

        assertEquals("进行中", item.getStatus());
        assertNotNull(item.getCreateTime());
        verify(lostFoundService).save(item);
    }

    @Test
    void testUpdateStatusSuccess() {
        LostFound item = new LostFound();
        item.setId(1L);
        item.setStatus("进行中");

        doReturn(item).when(lostFoundService).getById(1L);
        doReturn(true).when(lostFoundService).updateById(any(LostFound.class));

        lostFoundService.updateStatus(1L, "已结束");

        assertEquals("已结束", item.getStatus());
        verify(lostFoundService).updateById(item);
    }

    @Test
    void testUpdateStatusNotFound() {
        doReturn(null).when(lostFoundService).getById(999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lostFoundService.updateStatus(999L, "已结束"));
        assertEquals("记录不存在", ex.getMessage());
    }
}
