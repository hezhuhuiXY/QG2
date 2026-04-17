package com.qg.lostfound.dto.contact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCommentAddDTO {

    @NotNull(message = "留言类型不能为空")
    private Integer itemType;

    @NotNull(message = "留言对象不能为空")
    private Integer itemId;

    @NotBlank(message = "留言内容不能为空")
    private String content;

    private String contactInfo;
}