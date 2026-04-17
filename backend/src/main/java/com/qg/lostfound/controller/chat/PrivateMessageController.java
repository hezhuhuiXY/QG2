package com.qg.lostfound.controller.chat;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.chat.PrivateMessageAddDTO;
import com.qg.lostfound.entity.chat.PrivateMessage;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.chat.PrivateMessageService;
import com.qg.lostfound.utils.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 私聊消息控制器
 * 功能：处理用户私信发送、聊天记录查询、消息已读标记
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor

public class PrivateMessageController {

    private final PrivateMessageService privateMessageService;
    /**
     * 发送私聊消息
     * 请求路径：POST /messages
     *
     * @param authorization 请求头token，用于获取当前登录用户ID
     * @param dto 消息接收者、消息内容等参数
     * @return 发送结果
     */
    @PostMapping
    public Result<Void> sendMessage(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid PrivateMessageAddDTO dto) {

        Integer userId = getUserId(authorization);
        privateMessageService.sendMessage(userId, dto);
        return Result.success();
    }
    /**
     * 查询与指定用户的聊天记录
     * 请求路径：GET /messages/{otherUserId}
     *
     * @param authorization 请求头token
     * @param otherUserId 对方用户ID
     * @return 两人的聊天记录列表
     */
    @GetMapping("/{otherUserId}")
    public Result<List<PrivateMessage>> listConversation(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer otherUserId) {

        Integer userId = getUserId(authorization);
        privateMessageService.readMyMessages(userId, otherUserId);
        return Result.success(privateMessageService.listConversation(userId, otherUserId));
    }
    /**
     * 从请求头解析用户ID
     * 自动处理 Bearer 格式token
     */
    private Integer getUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization.startsWith("Bearer ")
                ? authorization.substring(7) : authorization;
        return JwtUtils.getUserId(token);
    }
}