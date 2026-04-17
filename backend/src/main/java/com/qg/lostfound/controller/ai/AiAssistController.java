package com.qg.lostfound.controller.ai;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.ai.AiAssistService;
import com.qg.lostfound.utils.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
/**
 * AI助手控制层
 * 功能：处理AI生成描述、重新生成描述、管理员数据总结接口
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiAssistController {
    //AI助手服务层
    private final AiAssistService aiAssistService;
/**
 * AI生成【失物】描述
 *
 * @param authorization 请求头中的token
 * @param id 失物ID
 * @return 生成后的描述文本
 */
    @PostMapping("/lost-items/{id}/generate-description")
    public Result<String> generateLostDescription(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer userId = getUserId(authorization);
        return Result.success(aiAssistService.generateLostItemDescription(userId, id));
    }
    /**
     * 重新生成【失物】描述（带权限校验）
     *
     * @param authorization 请求头中的token
     * @param id 失物ID
     * @return 重新生成后的描述文本
     */
    @PutMapping("/lost-items/{id}/regenerate-description")
    public Result<String> regenerateLostDescription(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer userId = getUserId(authorization);
        return Result.success(aiAssistService.regenerateLostItemDescription(userId, id));
    }
    /**
     * AI生成【拾物】描述
     *
     * @param authorization 请求头中的token
     * @param id 拾物ID
     * @return 生成后的描述文本
     */
    @PostMapping("/found-items/{id}/generate-description")
    public Result<String> generateFoundDescription(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer userId = getUserId(authorization);
        return Result.success(aiAssistService.generateFoundItemDescription(userId, id));
    }
    /**
     * 重新生成【拾物】描述（带权限校验）
     *
     * @param authorization 请求头中的token
     * @param id 拾物ID
     * @return 重新生成后的描述文本
     */
    @PutMapping("/found-items/{id}/regenerate-description")
    public Result<String> regenerateFoundDescription(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer userId = getUserId(authorization);
        return Result.success(aiAssistService.regenerateFoundItemDescription(userId, id));
    }
    /**
     * 管理员：生成平台数据统计总结（AI分析）
     * 仅管理员可访问
     *
     * @param authorization token
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return AI生成的平台总结文本
     */
    @GetMapping("/admin-summary")
    public Result<String> generateAdminSummary(
            //传了 → 按时间范围统计
            //不传 → 统计所有时间的数据
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Integer role = getRole(authorization);
        if (role == null || role != 1) {
            throw new BusinessException("无管理员权限");
        }

        return Result.success(aiAssistService.generateAdminSummary(startTime, endTime));
    }
    /**
     * 从请求头Authorization中解析用户ID
     * 自动处理 Bearer token 格式
     *
     * @param authorization 请求头
     * @return 用户ID
     */
    private Integer getUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization.startsWith("Bearer ")
                ? authorization.substring(7) : authorization;
        return JwtUtils.getUserId(token);
    }
    /**
     * 从请求头Authorization中解析用户角色
     *
     * @param authorization 请求头
     * @return 角色标识 1=管理员
     */
    private Integer getRole(String authorization) {
        String token = authorization.startsWith("Bearer ")
                ? authorization.substring(7) : authorization;
        return JwtUtils.getRole(token);
    }
}