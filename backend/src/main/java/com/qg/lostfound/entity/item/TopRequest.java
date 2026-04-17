package com.qg.lostfound.entity.item;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("top_request")
public class TopRequest {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer lostItemId;
    private Integer userId;
    private String reason;
    private Integer status;
    private Integer reviewAdminId;
    private Integer topHours;
    private LocalDateTime createTime;
    private LocalDateTime reviewTime;
}