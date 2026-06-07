package com.dogrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dogrescue.entity.Dog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * 狗狗识别服务 - 基于通义千问 VL 多模态大模型
 * 用户上传狗狗照片，AI 识别品种，并在数据库中匹配相似狗狗
 */
@Service
public class DogRecognitionService {

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Value("${dashscope.model}")
    private String model;

    @Value("${dashscope.base-url}")
    private String baseUrl;

    private final DogService dogService;

    public DogRecognitionService(DogService dogService) {
        this.dogService = dogService;
    }

    /**
     * 识别图片中的狗狗
     * @param imageBase64 图片的 Base64 编码
     * @param mimeType 图片 MIME 类型，如 image/jpeg
     * @return 识别结果 Map
     */
    public Map<String, Object> recognize(String imageBase64, String mimeType) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. 调用通义千问 VL 识别品种
        String breed = callQianwenVL(imageBase64, mimeType);
        result.put("recognizedBreed", breed);

        // 2. 在数据库中匹配同品种的狗狗
        List<Dog> matchedDogs = findMatchingDogs(breed);
        result.put("matchedDogs", matchedDogs);
        result.put("matchCount", matchedDogs.size());

        return result;
    }

    /**
     * 调用通义千问 VL API 识别狗狗品种
     */
    private String callQianwenVL(String imageBase64, String mimeType) {
        try {
            String dataUrl = "data:" + mimeType + ";base64," + imageBase64;

            // 构造 OpenAI 兼容格式的请求 JSON
            String requestBody = buildRequestBody(dataUrl);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                System.err.println("通义千问API返回错误: " + response.statusCode());
                System.err.println("响应内容: " + response.body());
                return "识别失败（API错误: " + response.statusCode() + "）";
            }
        } catch (Exception e) {
            System.out.println("通义千问API调用异常: " + e.getMessage());
            return "识别失败（" + e.getMessage() + "）";
        }
    }

    /**
     * 构造请求体 JSON
     */
    private String buildRequestBody(String dataUrl) {
        // 获取所有品种列表作为参考
        String[] breeds = {"中华田园犬", "金毛", "拉布拉多", "哈士奇", "萨摩耶", "泰迪", "比熊",
                "柯基", "边牧", "德牧", "阿拉斯加", "柴犬", "秋田犬", "斗牛犬", "法斗",
                "英斗", "雪纳瑞", "约克夏", "吉娃娃", "博美", "贵宾犬", "松狮", "巴哥",
                "罗威纳", "杜宾", "大白熊", "古代牧羊犬", "藏獒", "斑点狗"};
        String breedList = String.join("、", breeds);
    
        // 使用字符串拼接构造JSON，避免dataUrl中的特殊字符破坏JSON格式
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"model\": \"").append(model).append("\",\n");
        sb.append("  \"messages\": [\n");
        sb.append("    {\n");
        sb.append("      \"role\": \"system\",\n");
        sb.append("      \"content\": \"你是一个专业的犬类识别专家。用户会上传狗狗的照片，你需要识别照片中狗狗的品种。请只回答品种名称，不要回答其他内容。如果照片中有多个狗狗，请识别最明显的那一只。如果无法确定具体品种，请回答混合品种。\"\n");
        sb.append("    },\n");
        sb.append("    {\n");
        sb.append("      \"role\": \"user\",\n");
        sb.append("      \"content\": [\n");
        sb.append("        {\n");
        sb.append("          \"type\": \"image_url\",\n");
        sb.append("          \"image_url\": {\n");
        sb.append("            \"url\": \"").append(dataUrl).append("\"\n");
        sb.append("          }\n");
        sb.append("        },\n");
        sb.append("        {\n");
        sb.append("          \"type\": \"text\",\n");
        sb.append("          \"text\": \"请识别这只狗的品种，只回答品种名称。参考品种列表：").append(breedList).append("。如果是其他品种也可以回答。\"\n");
        sb.append("        }\n");
        sb.append("      ]\n");
        sb.append("    }\n");
        sb.append("  ],\n");
        sb.append("  \"max_tokens\": 50\n");
        sb.append("}");
    
        return sb.toString();
    }

    /**
     * 解析 API 响应，提取品种名称
     */
    private String parseResponse(String responseBody) {
        try {
            // 简单解析: 提取 choices[0].message.content
            int contentIdx = responseBody.indexOf("\"content\"");
            if (contentIdx < 0) return "无法解析结果";

            int colonIdx = responseBody.indexOf(":", contentIdx);
            int quoteStart = responseBody.indexOf("\"", colonIdx + 1);
            int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);

            if (quoteStart >= 0 && quoteEnd > quoteStart) {
                String content = responseBody.substring(quoteStart + 1, quoteEnd)
                        .replace("\\n", "").replace("\\r", "").trim();
                // 去掉可能的前缀和后缀
                content = content.replaceAll("品种[是为：:]", "").trim();
                if (content.isEmpty()) return "无法识别";
                return content;
            }
            return "无法解析结果";
        } catch (Exception e) {
            return "解析失败: " + e.getMessage();
        }
    }

    /**
     * 在数据库中查找匹配品种的狗狗
     */
    private List<Dog> findMatchingDogs(String breed) {
        if (breed == null || breed.contains("失败") || breed.contains("无法") || breed.contains("混合品种")) {
            return List.of();
        }

        // 模糊匹配：AI 返回的品种名可能和数据库中的略有不同
        LambdaQueryWrapper<Dog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Dog::getBreed, breed)
               .eq(Dog::getStatus, "待领养")
               .orderByDesc(Dog::getCreateTime)
               .last("LIMIT 10");

        List<Dog> dogs = dogService.list(wrapper);

        // 如果精确匹配没结果，尝试反向模糊匹配（数据库品种包含AI识别的品种关键字）
        if (dogs.isEmpty() && breed.length() >= 2) {
            LambdaQueryWrapper<Dog> reverseWrapper = new LambdaQueryWrapper<>();
            // 提取关键字，比如 "金毛犬" -> "金毛"
            String keyword = breed.replaceAll("(犬|狗)$", "");
            reverseWrapper.like(Dog::getBreed, keyword)
                    .eq(Dog::getStatus, "待领养")
                    .orderByDesc(Dog::getCreateTime)
                    .last("LIMIT 10");
            dogs = dogService.list(reverseWrapper);
        }

        return dogs;
    }
}
