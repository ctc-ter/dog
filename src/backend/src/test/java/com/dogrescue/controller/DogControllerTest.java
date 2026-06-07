package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Dog;
import com.dogrescue.config.SecurityConfig;
import com.dogrescue.service.DogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DogController.class)
@Import(SecurityConfig.class)
class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogService dogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListDogs() throws Exception {
        Dog dog = new Dog();
        dog.setId(1L);
        dog.setName("旺财");
        dog.setBreed("中华田园犬");
        dog.setStatus("可领养");

        PageResult<Dog> pageResult = new PageResult<>(1L, List.of(dog));
        when(dogService.listDogs(1, 10, null, null, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/dogs")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("旺财"));
    }

    @Test
    void testListDogsWithFilters() throws Exception {
        PageResult<Dog> pageResult = new PageResult<>(0L, List.of());
        when(dogService.listDogs(1, 10, "金毛", "公", "可领养")).thenReturn(pageResult);

        mockMvc.perform(get("/api/dogs")
                        .param("breed", "金毛")
                        .param("gender", "公")
                        .param("status", "可领养"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void testGetById() throws Exception {
        Dog dog = new Dog();
        dog.setId(1L);
        dog.setName("旺财");
        dog.setBreed("中华田园犬");

        when(dogService.getById(1L)).thenReturn(dog);

        mockMvc.perform(get("/api/dogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("旺财"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(dogService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/dogs/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddDog() throws Exception {
        Dog dog = new Dog();
        dog.setName("小白");
        dog.setBreed("萨摩耶");
        dog.setGender("母");
        dog.setAge(1);
        dog.setStatus("可领养");

        doNothing().when(dogService).addDog(any(Dog.class));

        mockMvc.perform(post("/api/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(dogService).addDog(any(Dog.class));
    }

    @Test
    void testUpdateDog() throws Exception {
        Dog dog = new Dog();
        dog.setName("旺财");
        dog.setStatus("已领养");

        doNothing().when(dogService).updateDog(any(Dog.class));

        mockMvc.perform(put("/api/dogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(dogService).updateDog(argThat(d -> d.getId().equals(1L)));
    }

    @Test
    void testDeleteDog() throws Exception {
        when(dogService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/dogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(dogService).removeById(1L);
    }
}
