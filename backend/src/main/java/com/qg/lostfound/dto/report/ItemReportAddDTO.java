package com.qg.lostfound.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemReportAddDTO {

    @NotNull(message = "举报类型不能为空")
    private Integer itemType;
    @NotNull(message = "举报对象不能为空")
    private Integer itemId;
    @NotBlank(message = "举报理由不能为空")
    private String reason;
}
