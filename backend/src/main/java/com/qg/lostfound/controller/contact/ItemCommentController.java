package com.qg.lostfound.controller.contact;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.contact.ItemCommentAddDTO;
import com.qg.lostfound.entity.contact.ItemComment;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.contact.ItemCommentService;
import com.qg.lostfound.utils.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 物品评论/留言控制器
 * 功能：处理失物/拾物的留言发布、留言列表查询
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ItemCommentController {

    private final ItemCommentService itemCommentService;
    /**
     * 发布物品留言/评论
     * 请求路径：POST /comments
     *
     * @param authorization 请求头中的token，用于获取当前登录用户
     * @param dto 留言信息：物品类型、物品ID、留言内容、联系方式
     * @return 统一返回结果
     */
    @PostMapping
    public Result<Void> addComment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid ItemCommentAddDTO dto) {

        Integer userId = getUserId(authorization);
        itemCommentService.addComment(userId, dto);
        return Result.success();
    }
    /**
     * 根据物品类型和物品ID查询对应的留言列表
     * 请求路径：GET /comments?itemType=xx&itemId=xx
     *
     * @param itemType 物品类型 1-失物 2-拾物
     * @param itemId 物品ID
     * @return 该物品下的所有留言列表
     */
    @GetMapping
    public Result<List<ItemComment>> listComments(
            @RequestParam Integer itemType,
            @RequestParam Integer itemId) {

        return Result.success(itemCommentService.listComments(itemType, itemId));
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