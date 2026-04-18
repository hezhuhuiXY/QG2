package com.qg.lostfound.controller.admin;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.admin.AdminService;
import com.qg.lostfound.utils.security.JwtUtils;
import com.qg.lostfound.vo.admin.AdminStatsVO;
import com.qg.lostfound.vo.admin.AdminUserVO;
import com.qg.lostfound.dto.report.ItemReportAuditDTO;
import com.qg.lostfound.service.report.ItemReportService;
import com.qg.lostfound.service.item.TopRequestService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ItemReportService itemReportService;
    private final TopRequestService topRequestService;

    @GetMapping("/users")
    public Result<List<AdminUserVO>> listUsers(
        @RequestHeader("Authorization") String authorization){

        checkAdmin(authorization);
        return Result.success(adminService.listUsers());
    }

    @PutMapping("/users/{id}/ban")
    public Result<Void> banUser(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        Integer adminUserId=checkAdmin(authorization);
        adminService.banUser(adminUserId, id);
        return Result.success();
    }

    @PutMapping("/users/{id}/unban")
    public Result<Void> unbanUser(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        Integer adminUserId=checkAdmin(authorization);
        adminService.unbanUser(adminUserId, id);
        return Result.success();
    }

    @DeleteMapping("/lost-items/{id}")
    public Result<Void> deleteLostItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        checkAdmin(authorization);
        adminService.deleteLostItemByAdmin(id);
        return Result.success();
    }

    @DeleteMapping("/found-items/{id}")
    public Result<Void> deleteFoundItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        checkAdmin(authorization);
        adminService.deleteFoundItemByAdmin(id);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<AdminStatsVO> getPlatformStats(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required=false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ){
        checkAdmin(authorization);
        return Result.success(adminService.getPlatformStats(startTime, endTime));
    }

    @PutMapping("/reports/{id}/approve")
    public Result<Void> approveReport(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid ItemReportAuditDTO dto) {

        Integer adminUserId = checkAdmin(authorization);
        itemReportService.approveReport(adminUserId, id, dto);
        return Result.success();
    }

    @PutMapping("/reports/{id}/reject")
    public Result<Void> rejectReport(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid ItemReportAuditDTO dto) {

        Integer adminUserId = checkAdmin(authorization);
        itemReportService.rejectReport(adminUserId, id, dto);
        return Result.success();
    }

    @PutMapping("/top-requests/{id}/approve")
    public Result<Void> approveTopRequest(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer adminUserId = checkAdmin(authorization);
        topRequestService.approveTopRequest(adminUserId, id);
        return Result.success();
    }

    @PutMapping("/top-requests/{id}/reject")
    public Result<Void> rejectTopRequest(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer adminUserId = checkAdmin(authorization);
        topRequestService.rejectTopRequest(adminUserId, id);
        return Result.success();
    }

    private Integer checkAdmin(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        Integer role = JwtUtils.getRole(token);
        if (role == null || role != 1) {
            throw new BusinessException("无管理员权限");
        }
        return JwtUtils.getUserId(token);
    }
}
