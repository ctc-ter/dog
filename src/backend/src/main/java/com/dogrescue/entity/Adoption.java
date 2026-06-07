package com.dogrescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("adoptions")
public class Adoption {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dogId;
    private String applicantName;
    private String phone;
    private String address;
    private String housingType;
    private Boolean hasExperience;
    private String reason;
    private String status;
    private String remark;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
}
