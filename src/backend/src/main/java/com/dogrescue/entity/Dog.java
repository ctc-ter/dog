package com.dogrescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dogs")
public class Dog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String breed;
    private String gender;
    private Integer age;
    private String healthStatus;
    private String description;
    private String imageUrls;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
