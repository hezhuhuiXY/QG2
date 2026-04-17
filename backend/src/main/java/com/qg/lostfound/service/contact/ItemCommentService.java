package com.qg.lostfound.service.contact;

import com.qg.lostfound.dto.contact.ItemCommentAddDTO;
import com.qg.lostfound.entity.contact.ItemComment;

import java.util.List;
/**
 * 物品评论服务接口
 * 功能：提供失物/拾物的评论发布、评论列表查询等操作
 */
public interface ItemCommentService {
    /**
     * 发布物品评论
     *
     * @param userId 当前登录用户ID
     * @param dto    评论内容相关参数
     */
    void addComment(Integer userId, ItemCommentAddDTO dto);
    /**
     * 根据物品类型和物品ID查询对应的评论列表
     *
     * @param itemType 物品类型（1-失物 2-拾物）
     * @param itemId   物品ID
     * @return 该物品下的所有评论列表
     */
    List<ItemComment> listComments(Integer itemType, Integer itemId);
}