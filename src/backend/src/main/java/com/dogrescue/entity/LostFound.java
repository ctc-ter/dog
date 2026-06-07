package com.dogrescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lost_found")
public class LostFound {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String title;
    private String description;
    private String location;
    private String contactName;
    private String contactPhone;
    private String imageUrls;
    private String status;
    private LocalDateTime eventTime;
    private LocalDateTime createTime;
}
