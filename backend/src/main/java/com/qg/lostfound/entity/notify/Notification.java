package com.qg.lostfound.entity.notify;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String bizType;
    private Integer bizId;
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}