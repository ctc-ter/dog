package com.dogrescue.controller;

import com.dogrescue.dto.PageResult;
import com.dogrescue.dto.R;
import com.dogrescue.entity.Story;
import com.dogrescue.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @GetMapping
    public R<PageResult<Story>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.ok(storyService.listStories(page, size));
    }

    @GetMapping("/{id}")
    public R<Story> getById(@PathVariable Long id) {
        return R.ok(storyService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Story story) {
        storyService.addStory(story);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Story story) {
        story.setId(id);
        storyService.updateStory(story);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        storyService.removeById(id);
        return R.ok();
    }
}
