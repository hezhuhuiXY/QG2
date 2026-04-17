package com.qg.lostfound.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoundItemSaveDTO {

    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称长度不能超过100")
    private String itemName;

    @NotBlank(message = "拾取地点不能为空")
    @Size(max = 255, message = "拾取地点长度不能超过255")
    private String foundLocation;

    @NotNull(message = "拾取时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime foundTime;

    private String description;

    @Size(max = 255, message = "图片地址长度不能超过255")
    private String imageUrl;

    @Size(max = 255, message = "联系方式长度不能超过255")
    private String contactInfo;
}