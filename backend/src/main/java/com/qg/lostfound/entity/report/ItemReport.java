package com.qg.lostfound.entity.report;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("item_report")
public class ItemReport {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer itemType;
    private Integer itemId;
    private Integer reporterId;
    private String reason;
    private Integer status;
    private Integer reviewAdminId;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime reviewTime;
}