package com.qg.lostfound.service.impl.contact;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.dto.contact.ItemCommentAddDTO;
import com.qg.lostfound.entity.contact.ItemComment;
import com.qg.lostfound.entity.item.FoundItem;
import com.qg.lostfound.entity.item.LostItem;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.FoundItemMapper;
import com.qg.lostfound.mapper.ItemCommentMapper;
import com.qg.lostfound.mapper.LostItemMapper;
import com.qg.lostfound.service.contact.ItemCommentService;
import com.qg.lostfound.service.notify.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 物品评论/留言业务实现类
 * 功能：发布物品留言、查询物品留言列表
 */
@Service
@RequiredArgsConstructor
public class ItemCommentServiceImpl implements ItemCommentService {
    // 评论留言Mapper
    private final ItemCommentMapper itemCommentMapper;
    // 失物Mapper
    private final LostItemMapper lostItemMapper;
    // 拾物Mapper
    private final FoundItemMapper foundItemMapper;
    // 消息通知服务
    private final NotificationService notificationService;
    /**
     * 发布物品评论/留言
     * @param userId 当前登录用户ID（留言人）
     * @param dto 留言提交参数（物品类型、物品ID、内容、联系方式等）
     */
    @Override
    public void addComment(Integer userId, ItemCommentAddDTO dto) {
        Integer ownerId;

        if (dto.getItemType() == 1) {
            LostItem lostItem = lostItemMapper.selectById(dto.getItemId());
            if (lostItem == null || lostItem.getStatus() == 2) {
                throw new BusinessException("失物信息不存在");
            }
            ownerId = lostItem.getUserId();
        } else if (dto.getItemType() == 2) {
            FoundItem foundItem = foundItemMapper.selectById(dto.getItemId());
            if (foundItem == null || foundItem.getStatus() == 2) {
                throw new BusinessException("拾物信息不存在");
            }
            ownerId = foundItem.getUserId();
        } else {
            throw new BusinessException("留言类型错误");
        }

        ItemComment comment = new ItemComment();
        comment.setItemType(dto.getItemType());
        comment.setItemId(dto.getItemId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setContactInfo(dto.getContactInfo());
        comment.setIsRead(0);

        itemCommentMapper.insert(comment);
        // 如果不是自己给自己留言，则发送通知给物品发布者
        if (!ownerId.equals(userId)) {
            notificationService.createNotification(ownerId, "COMMENT", comment.getId(), "收到新留言", dto.getContent());
        }
    }
    /**
     * 根据物品类型和物品ID查询留言列表，按创建时间升序排列
     * @param itemType 物品类型 1-失物 2-拾物
     * @param itemId 物品ID
     * @return 该物品下的所有留言
     */
    @Override
    public List<ItemComment> listComments(Integer itemType, Integer itemId) {
        return itemCommentMapper.selectList(
                new LambdaQueryWrapper<ItemComment>()
                        .eq(ItemComment::getItemType, itemType)
                        .eq(ItemComment::getItemId, itemId)
                        .orderByAsc(ItemComment::getCreateTime)
        );
    }
}