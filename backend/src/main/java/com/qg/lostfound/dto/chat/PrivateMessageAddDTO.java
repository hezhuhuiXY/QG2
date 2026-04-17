package com.qg.lostfound.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrivateMessageAddDTO {

    @NotNull(message = "接收人不能为空")
    private Integer receiverId;

    @NotBlank(message = "消息内容不能为空")
    private String content;
}