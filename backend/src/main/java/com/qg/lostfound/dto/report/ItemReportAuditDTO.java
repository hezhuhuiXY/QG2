package com.qg.lostfound.dto.report;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemReportAuditDTO {

    @NotBlank(message = "审核备注不能为空")
    private String reviewRemark;
}