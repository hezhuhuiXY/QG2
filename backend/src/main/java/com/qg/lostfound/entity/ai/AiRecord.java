package com.qg.lostfound.entity.ai;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_record")
public class AiRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer itemType;
    private Integer itemId;
    private Integer userId;
    private Integer aiType;
    private String inputText;
    private String outputText;
    private String modelName;
    private LocalDateTime createTime;
}