package com.qg.lostfound.service.report;

import com.qg.lostfound.dto.report.ItemReportAddDTO;
import com.qg.lostfound.dto.report.ItemReportAuditDTO;

public interface ItemReportService {
    void addReport(Integer userId, ItemReportAddDTO dto);
    void approveReport(Integer adminUserId, Integer reportId,ItemReportAuditDTO dto);
    void rejectReport(Integer adminUserId, Integer reportId,ItemReportAuditDTO dto);

}
