package com.dogrescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Volunteer;
import com.dogrescue.mapper.VolunteerMapper;
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
class VolunteerServiceTest {

    @Mock
    private VolunteerMapper volunteerMapper;

    @Spy
    @InjectMocks
    private VolunteerService volunteerService;

    @Test
    void testListVolunteersNoFilter() {
        Volunteer v = new Volunteer();
        v.setId(1L);
        v.setName("陈七");
        v.setPhone("13900139000");
        v.setStatus("待审核");

        Page<Volunteer> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(v));
        mockPage.setTotal(1);

        doReturn(mockPage).when(volunteerService).page(any(Page.class), any());

        PageResult<Volunteer> result = volunteerService.listVolunteers(1, 10, null);
        assertEquals(1, result.getTotal());
        assertEquals("陈七", result.getList().get(0).getName());
    }

    @Test
    void testListVolunteersWithStatusFilter() {
        Page<Volunteer> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(volunteerService).page(any(Page.class), any());

        PageResult<Volunteer> result = volunteerService.listVolunteers(1, 10, "已通过");
        assertEquals(0, result.getTotal());
    }

    @Test
    void testApply() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("周八");
        volunteer.setPhone("13700137000");
        volunteer.setEmail("zhou@example.com");
        volunteer.setSkills("动物护理");

        doReturn(true).when(volunteerService).save(any(Volunteer.class));

        volunteerService.apply(volunteer);

        assertEquals("待审核", volunteer.getStatus());
        assertNotNull(volunteer.getApplyTime());
        verify(volunteerService).save(volunteer);
    }

    @Test
    void testAuditSuccess() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setStatus("待审核");

        doReturn(volunteer).when(volunteerService).getById(1L);
        doReturn(true).when(volunteerService).updateById(any(Volunteer.class));

        volunteerService.audit(1L, "已通过");

        assertEquals("已通过", volunteer.getStatus());
        verify(volunteerService).updateById(volunteer);
    }

    @Test
    void testAuditReject() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setStatus("待审核");

        doReturn(volunteer).when(volunteerService).getById(1L);
        doReturn(true).when(volunteerService).updateById(any(Volunteer.class));

        volunteerService.audit(1L, "已拒绝");

        assertEquals("已拒绝", volunteer.getStatus());
    }

    @Test
    void testAuditNotFound() {
        doReturn(null).when(volunteerService).getById(999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> volunteerService.audit(999L, "已通过"));
        assertEquals("记录不存在", ex.getMessage());
    }
}
