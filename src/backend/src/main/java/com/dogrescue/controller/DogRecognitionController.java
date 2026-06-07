package com.dogrescue.controller;

import com.dogrescue.dto.R;
import com.dogrescue.service.DogRecognitionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

/**
 * 狗狗识别接口 - AI 图像识别狗狗品种
 */
@RestController
@RequestMapping("/api/recognition")
public class DogRecognitionController {

    private static final int MAX_IMAGE_SIZE = 1024; // 最大边长
    private static final float JPEG_QUALITY = 0.7f;  // JPEG压缩质量

    private final DogRecognitionService recognitionService;

    public DogRecognitionController(DogRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    /**
     * 上传狗狗照片进行识别
     * POST /api/recognition/recognize
     * Content-Type: multipart/form-data
     */
    @PostMapping("/recognize")
    public R<Map<String, Object>> recognize(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return R.error("请上传一张狗狗照片");
        }

        // 校验文件类型
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return R.error("请上传图片文件（jpg、png、webp）");
        }

        // 校验文件大小（最大 10MB）
        if (image.getSize() > 10 * 1024 * 1024) {
            return R.error("图片大小不能超过10MB");
        }

        try {
            // 读取、压缩、缩放图片后再转Base64，避免API请求体过大导致400错误
            BufferedImage originalImage = ImageIO.read(image.getInputStream());
            if (originalImage == null) {
                return R.error("无法读取图片，请确认图片格式正确");
            }

            byte[] compressedBytes = compressImage(originalImage);
            String base64 = Base64.getEncoder().encodeToString(compressedBytes);

            System.out.println("图片压缩后大小: " + compressedBytes.length / 1024 + "KB, Base64长度: " + base64.length());

            // 调用识别服务
            Map<String, Object> result = recognitionService.recognize(base64, "image/jpeg");
            return R.ok(result);
        } catch (Exception e) {
            System.out.println("识别异常: " + e.getMessage());
            e.printStackTrace();
            return R.error("识别失败: " + e.getMessage());
        }
    }

    /**
     * 压缩图片：缩放到最大1024px并以JPEG 0.7质量输出
     */
    private byte[] compressImage(BufferedImage original) throws Exception {
        int width = original.getWidth();
        int height = original.getHeight();

        // 按比例缩放
        if (width > MAX_IMAGE_SIZE || height > MAX_IMAGE_SIZE) {
            double scale = (double) MAX_IMAGE_SIZE / Math.max(width, height);
            width = (int) (width * scale);
            height = (int) (height * scale);
        }

        // 创建缩放后的图片（使用RGB模式，去掉alpha通道以兼容JPEG）
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();

        // JPEG压缩输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        var writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (writers.hasNext()) {
            var writer = writers.next();
            var writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(JPEG_QUALITY);
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new javax.imageio.IIOImage(resized, null, null), writeParam);
            writer.dispose();
        } else {
            ImageIO.write(resized, "jpeg", baos);
        }

        return baos.toByteArray();
    }
}
