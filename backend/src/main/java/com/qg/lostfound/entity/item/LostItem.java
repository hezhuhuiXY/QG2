package com.qg.lostfound.entity.item;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lost_item")
public class LostItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String itemName;

    private String lostLocation;

    private LocalDateTime lostTime;

    private String description;

    private String imageUrl;

    private String contactInfo;

    private Integer status;

    private Integer isTop;

    private LocalDateTime topExpireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}