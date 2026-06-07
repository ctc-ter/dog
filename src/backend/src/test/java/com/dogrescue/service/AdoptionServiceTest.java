package com.dogrescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Adoption;
import com.dogrescue.mapper.AdoptionMapper;
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
class AdoptionServiceTest {

    @Mock
    private AdoptionMapper adoptionMapper;

    @Spy
    @InjectMocks
    private AdoptionService adoptionService;

    @Test
    void testListAdoptionsNoFilter() {
        Adoption a = new Adoption();
        a.setId(1L);
        a.setDogId(1L);
        a.setApplicantName("张三");
        a.setStatus("待审核");

        Page<Adoption> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(a));
        mockPage.setTotal(1);

        doReturn(mockPage).when(adoptionService).page(any(Page.class), any());

        PageResult<Adoption> result = adoptionService.listAdoptions(1, 10, null);
        assertEquals(1, result.getTotal());
        assertEquals("张三", result.getList().get(0).getApplicantName());
    }

    @Test
    void testListAdoptionsWithStatusFilter() {
        Page<Adoption> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(adoptionService).page(any(Page.class), any());

        PageResult<Adoption> result = adoptionService.listAdoptions(1, 10, "已通过");
        assertEquals(0, result.getTotal());
    }

    @Test
    void testApply() {
        Adoption adoption = new Adoption();
        adoption.setDogId(1L);
        adoption.setApplicantName("李四");
        adoption.setPhone("13800138000");
        adoption.setReason("想养狗");

        doReturn(true).when(adoptionService).save(any(Adoption.class));

        adoptionService.apply(adoption);

        assertEquals("待审核", adoption.getStatus());
        assertNotNull(adoption.getApplyTime());
        verify(adoptionService).save(adoption);
    }

    @Test
    void testAuditSuccess() {
        Adoption adoption = new Adoption();
        adoption.setId(1L);
        adoption.setStatus("待审核");

        doReturn(adoption).when(adoptionService).getById(1L);
        doReturn(true).when(adoptionService).updateById(any(Adoption.class));

        adoptionService.audit(1L, "已通过", "条件符合");

        assertEquals("已通过", adoption.getStatus());
        assertEquals("条件符合", adoption.getRemark());
        assertNotNull(adoption.getAuditTime());
        verify(adoptionService).updateById(adoption);
    }

    @Test
    void testAuditNotFound() {
        doReturn(null).when(adoptionService).getById(999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adoptionService.audit(999L, "已通过", ""));
        assertEquals("申请记录不存在", ex.getMessage());
    }

    @Test
    void testAuditReject() {
        Adoption adoption = new Adoption();
        adoption.setId(1L);
        adoption.setStatus("待审核");

        doReturn(adoption).when(adoptionService).getById(1L);
        doReturn(true).when(adoptionService).updateById(any(Adoption.class));

        adoptionService.audit(1L, "已拒绝", "住房不适合");

        assertEquals("已拒绝", adoption.getStatus());
        assertEquals("住房不适合", adoption.getRemark());
        assertNotNull(adoption.getAuditTime());
    }
}
