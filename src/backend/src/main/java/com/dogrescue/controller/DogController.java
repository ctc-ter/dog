package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.dto.R;
import com.dogrescue.entity.Dog;
import com.dogrescue.service.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

    @Autowired
    private DogService dogService;

    @GetMapping
    public R<PageResult<Dog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String status) {
        return R.ok(dogService.listDogs(page, size, breed, gender, status));
    }

    @GetMapping("/{id}")
    public R<Dog> getById(@PathVariable Long id) {
        return R.ok(dogService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Dog dog) {
        dogService.addDog(dog);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Dog dog) {
        dog.setId(id);
        dogService.updateDog(dog);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dogService.removeById(id);
        return R.ok();
    }
}
