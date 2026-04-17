package com.qg.lostfound.entity.item;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("found_item")
public class FoundItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String itemName;

    private String foundLocation;

    private LocalDateTime foundTime;

    private String description;

    private String imageUrl;

    private String contactInfo;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}