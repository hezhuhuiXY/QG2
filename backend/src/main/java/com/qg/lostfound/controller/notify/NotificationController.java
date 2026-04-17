package com.qg.lostfound.controller.notify;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.entity.notify.Notification;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.notify.NotificationService;
import com.qg.lostfound.utils.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 消息通知控制器
 * 功能：提供当前登录用户的通知列表查询、标记通知已读等接口
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    /**
     * 查询当前登录用户的所有通知列表
     * 请求路径：GET /notifications
     *
     * @param authorization 请求头中的token
     * @return 当前用户的通知列表
     */
    @GetMapping
    public Result<List<Notification>> listMyNotifications(
            @RequestHeader("Authorization") String authorization) {

        Integer userId = getUserId(authorization);
        return Result.success(notificationService.listMyNotifications(userId));
    }
    /**
     * 将指定ID的通知标记为已读
     * 请求路径：PUT /notifications/{id}/read
     *
     * @param authorization 请求头中的token
     * @param id 通知ID
     * @return 操作结果
     */
    @PutMapping("/{id}/read")
    public Result<Void> readNotification(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer userId = getUserId(authorization);
        notificationService.readNotification(userId, id);
        return Result.success();
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
        // 去除 "Bearer " 前缀，获取纯token
        String token = authorization.startsWith("Bearer ")
                ? authorization.substring(7) : authorization;
        return JwtUtils.getUserId(token);
    }
}