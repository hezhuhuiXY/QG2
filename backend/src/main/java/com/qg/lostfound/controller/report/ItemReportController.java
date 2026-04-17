package com.qg.lostfound.controller.report;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.report.ItemReportAddDTO;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.report.ItemReportService;
import com.qg.lostfound.utils.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ItemReportController {
    private final ItemReportService itemReportService;
    @PostMapping
    public Result<Void> addReport(
            @RequestHeader("Authorization")String authorization,
            @RequestBody @Valid ItemReportAddDTO dto
    ){
        Integer userId=getUserId(authorization);
        itemReportService.addReport(userId,dto);
        return Result.success();
    }

    private Integer getUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization.startsWith("Bearer ")
                ? authorization.substring(7) : authorization;
        return JwtUtils.getUserId(token);
    }
}
