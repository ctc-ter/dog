package com.dogrescue.controller;

import com.dogrescue.dto.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            Path dir = Paths.get(uploadPath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String original = file.getOriginalFilename();
            String ext = original != null ? original.substring(original.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
            return R.ok("/uploads/" + filename);
        } catch (IOException e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }
}
