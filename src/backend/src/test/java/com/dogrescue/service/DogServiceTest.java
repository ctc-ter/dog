package com.dogrescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dogrescue.dto.PageResult;
import com.dogrescue.entity.Dog;
import com.dogrescue.mapper.DogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogMapper dogMapper;

    @Spy
    @InjectMocks
    private DogService dogService;

    private Dog buildDog(Long id, String name, String breed, String status) {
        Dog dog = new Dog();
        dog.setId(id);
        dog.setName(name);
        dog.setBreed(breed);
        dog.setGender("公");
        dog.setAge(2);
        dog.setStatus(status);
        return dog;
    }

    @Test
    void testListDogsNoFilter() {
        List<Dog> dogs = Arrays.asList(
                buildDog(1L, "旺财", "中华田园犬", "可领养"),
                buildDog(2L, "Lucky", "金毛", "已领养")
        );
        Page<Dog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(dogs);
        mockPage.setTotal(2);

        doReturn(mockPage).when(dogService).page(any(Page.class), any());

        PageResult<Dog> result = dogService.listDogs(1, 10, null, null, null);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getList().size());
        assertEquals("旺财", result.getList().get(0).getName());
    }

    @Test
    void testListDogsWithBreedFilter() {
        List<Dog> dogs = List.of(buildDog(1L, "旺财", "中华田园犬", "可领养"));
        Page<Dog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(dogs);
        mockPage.setTotal(1);

        doReturn(mockPage).when(dogService).page(any(Page.class), any());

        PageResult<Dog> result = dogService.listDogs(1, 10, "中华田园犬", null, null);
        assertEquals(1, result.getTotal());
        assertEquals("中华田园犬", result.getList().get(0).getBreed());
    }

    @Test
    void testListDogsWithGenderFilter() {
        Page<Dog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(buildDog(1L, "旺财", "中华田园犬", "可领养")));
        mockPage.setTotal(1);

        doReturn(mockPage).when(dogService).page(any(Page.class), any());

        PageResult<Dog> result = dogService.listDogs(1, 10, null, "公", null);
        assertEquals(1, result.getTotal());
    }

    @Test
    void testListDogsWithStatusFilter() {
        Page<Dog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(buildDog(1L, "Lucky", "金毛", "已领养")));
        mockPage.setTotal(1);

        doReturn(mockPage).when(dogService).page(any(Page.class), any());

        PageResult<Dog> result = dogService.listDogs(1, 10, null, null, "已领养");
        assertEquals(1, result.getTotal());
    }

    @Test
    void testListDogsEmptyResult() {
        Page<Dog> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        doReturn(mockPage).when(dogService).page(any(Page.class), any());

        PageResult<Dog> result = dogService.listDogs(1, 10, null, null, null);
        assertEquals(0, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    void testAddDogSetsTimeAndSaves() {
        Dog dog = buildDog(null, "小白", "萨摩耶", "可领养");

        doReturn(true).when(dogService).save(any(Dog.class));

        dogService.addDog(dog);

        assertNotNull(dog.getCreateTime());
        assertNotNull(dog.getUpdateTime());
        verify(dogService).save(dog);
    }

    @Test
    void testUpdateDogSetsUpdateTime() {
        Dog dog = buildDog(1L, "旺财", "中华田园犬", "可领养");

        doReturn(true).when(dogService).updateById(any(Dog.class));

        dogService.updateDog(dog);

        assertNotNull(dog.getUpdateTime());
        verify(dogService).updateById(dog);
    }
}
