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
    //验证是否为管理员
    @GetMapping("/users")
    public Result<List<AdminUserVO>> listUsers(
        @RequestHeader("Authorization") String authorization){

        checkAdmin(authorization);
        return Result.success(adminService.listUsers());
    }
    //封禁用户
    @PutMapping("/users/{id}/ban")
    public Result<Void> banUser(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        Integer adminUserId=checkAdmin(authorization);
        adminService.banUser(adminUserId, id);
        return Result.success();
    }
    //解封用户
    @PutMapping("/users/{id}/unban")
    public Result<Void> unbanUser(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        Integer adminUserId=checkAdmin(authorization);
        adminService.unbanUser(adminUserId, id);
        return Result.success();
    }
    //删除失物
    @DeleteMapping("/lost-items/{id}")
    public Result<Void> deleteLostItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        checkAdmin(authorization);
        adminService.deleteLostItemByAdmin(id);
        return Result.success();
    }
    //删除拾物
    @DeleteMapping("/found-items/{id}")
    public Result<Void> deleteFoundItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        checkAdmin(authorization);
        adminService.deleteFoundItemByAdmin(id);
        return Result.success();
    }
    /**
 * 管理员获取平台统计数据接口
 * 支持按时间范围筛选统计信息，需要管理员权限
 *
 * @param authorization 请求头中的token，用于校验管理员身份
 * @param startTime 可选参数，统计开始时间，格式：yyyy-MM-dd HH:mm:ss
 * @param endTime 可选参数，统计结束时间，格式：yyyy-MM-dd HH:mm:ss
 * @return 平台统计数据视图对象
 */
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
    //通过举报
    @PutMapping("/reports/{id}/approve")
    public Result<Void> approveReport(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid ItemReportAuditDTO dto) {

        Integer adminUserId = checkAdmin(authorization);
        itemReportService.approveReport(adminUserId, id, dto);
        return Result.success();
    }
    //驳回举报
    @PutMapping("/reports/{id}/reject")
    public Result<Void> rejectReport(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid ItemReportAuditDTO dto) {

        Integer adminUserId = checkAdmin(authorization);
        itemReportService.rejectReport(adminUserId, id, dto);
        return Result.success();
    }
    //通过置顶请求
    @PutMapping("/top-requests/{id}/approve")
    public Result<Void> approveTopRequest(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer adminUserId = checkAdmin(authorization);
        topRequestService.approveTopRequest(adminUserId, id);
        return Result.success();
    }
    //驳回置顶请求
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
