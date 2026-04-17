package com.qg.lostfound.entity.contact;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("item_comment")
public class ItemComment {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer itemType;
    private Integer itemId;
    private Integer userId;
    private String content;
    private String contactInfo;
    private Integer isRead;
    private LocalDateTime createTime;
}