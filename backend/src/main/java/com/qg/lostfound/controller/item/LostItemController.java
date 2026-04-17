package com.qg.lostfound.controller.item;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.item.ItemQueryDTO;
import com.qg.lostfound.dto.item.LostItemSaveDTO;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.item.LostItemService;
import com.qg.lostfound.utils.security.JwtUtils;
import com.qg.lostfound.dto.item.LostItemSaveDTO;
import com.qg.lostfound.entity.item.LostItem;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * 失物信息控制器
 * 功能：处理失物的发布、修改、删除、查询、标记已找回等接口
 */
@RestController
@RequestMapping("/lost-items")
@RequiredArgsConstructor
public class LostItemController {
    // 失物业务服务
    private final LostItemService lostItemService;
    /**
     * 发布失物信息
     */
    @PostMapping
    public Result<Void> addLostItem(
        @RequestHeader("Authorization") String authorization,
        @RequestBody @Valid LostItemSaveDTO lostItemSaveDTO
    ){
        Integer userId=getUserIdFromHeader(authorization);
        lostItemService.addLostItem(userId, lostItemSaveDTO);
        return Result.success();
    }
    /**
     * 修改失物信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateLostItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid LostItemSaveDTO lostItemSaveDTO
    ){
        Integer userId=getUserIdFromHeader(authorization);
        lostItemService.updateLostItem(userId, id, lostItemSaveDTO);
        return Result.success();
    }
    /**
     * 删除失物信息
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteLostItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id

    ){
        Integer userId=getUserIdFromHeader(authorization);
        lostItemService.deleteLostItem(userId, id);
        return Result.success();
    }
    /**
     * 条件查询失物列表（支持关键词、分类、时间筛选）
     */
    @GetMapping
    public Result<List<LostItem>> listLostItem(ItemQueryDTO queryDTO){
        return Result.success(lostItemService.listLostItems(queryDTO));
    }
    /**
     * 标记失物为“已找回”状态
     */
    @PutMapping("/{id}/resolve")
    public Result<Void> resolveLostItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id
    ){
        Integer userId=getUserIdFromHeader(authorization);
        lostItemService.resolveLostItem(userId, id);
        return Result.success();
    }
    private Integer getUserIdFromHeader(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        return JwtUtils.getUserId(token);
    }

}
