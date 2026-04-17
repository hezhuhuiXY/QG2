package com.qg.lostfound.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TopRequestAddDTO {

    @NotNull(message = "失物ID不能为空")
    private Integer lostItemId;

    @NotBlank(message = "申请原因不能为空")
    private String reason;

    private Integer topHours = 24;
}