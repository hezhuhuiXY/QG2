package com.qg.lostfound.controller.item;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.item.FoundItemSaveDTO;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.item.FoundItemService;
import com.qg.lostfound.utils.security.JwtUtils;
import com.qg.lostfound.dto.item.ItemQueryDTO;
import com.qg.lostfound.entity.item.FoundItem;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
/**
 * 拾物信息控制器
 * 功能：处理拾物的发布、修改、删除、查询、认领等接口
 */
@RestController
@RequestMapping("/found-items")
@RequiredArgsConstructor
public class FoundItemController {
    /**
     * 拾物业务服务
     */
    private final FoundItemService foundItemService;
    /**
     * 发布拾物信息
     * 请求方式：POST /found-items
     *
     * @param authorization      请求头token
     * @param foundItemSaveDTO   拾物信息表单
     * @return 统一返回结果
     */
    @PostMapping
    public Result<Void> addFoundItem(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid FoundItemSaveDTO foundItemSaveDTO) {

        Integer userId = getUserIdFromHeader(authorization);
        foundItemService.addFoundItem(userId, foundItemSaveDTO);
        return Result.success();
    }
    /**
     * 修改拾物信息
     * 请求方式：PUT /found-items/{id}
     *
     * @param authorization      请求头token
     * @param id                 拾物ID
     * @param foundItemSaveDTO   修改后的拾物信息
     * @return 统一返回结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateFoundItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid FoundItemSaveDTO foundItemSaveDTO) {

        Integer userId = getUserIdFromHeader(authorization);
        foundItemService.updateFoundItem(userId, id, foundItemSaveDTO);
        return Result.success();
    }
    /**
     * 删除拾物信息
     * 请求方式：DELETE /found-items/{id}
     *
     * @param authorization 请求头token
     * @param id            拾物ID
     * @return 统一返回结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFoundItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {

        Integer userId = getUserIdFromHeader(authorization);
        foundItemService.deleteFoundItem(userId, id);
        return Result.success();
    }
    /**
     * 分页/条件查询拾物列表
     * 请求方式：GET /found-items
     *
     * @param queryDTO 查询条件（关键词、分类、时间等）
     * @return 拾物列表
     */
    @GetMapping
    public Result<List<FoundItem>> listFoundItems(ItemQueryDTO queryDTO){
        return Result.success(foundItemService.listFoundItems(queryDTO));
    }
    /**
     * 认领拾物（标记为已认领）
     * 请求方式：PUT /found-items/{id}/claim
     *
     * @param authorization 请求头token
     * @param id            拾物ID
     * @return 统一返回结果
     */
    @PutMapping("/{id}/claim")
    public Result<Void> claimFoundItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer id) {
        Integer userId = getUserIdFromHeader(authorization);
        foundItemService.claimFoundItem(userId, id);
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