package com.qg.lostfound.service.impl.item;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.dto.item.ItemQueryDTO;
import com.qg.lostfound.dto.item.LostItemSaveDTO;
import com.qg.lostfound.entity.item.LostItem;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.LostItemMapper;
import com.qg.lostfound.service.item.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class LostItemServiceImpl implements LostItemService {
    private final LostItemMapper lostItemMapper;

    @Override
    public void addLostItem(Integer userId, LostItemSaveDTO lostItemSaveDTO){
        LostItem lostItem=new LostItem();
        lostItem.setUserId(userId);
        lostItem.setItemName(lostItemSaveDTO.getItemName());
        lostItem.setLostLocation(lostItemSaveDTO.getLostLocation());
        lostItem.setLostTime(lostItemSaveDTO.getLostTime());
        lostItem.setDescription(lostItemSaveDTO.getDescription());
        lostItem.setImageUrl(lostItemSaveDTO.getImageUrl());
        lostItem.setContactInfo(lostItemSaveDTO.getContactInfo());
        lostItem.setStatus(0);
        lostItem.setIsTop(0);

        int rows=lostItemMapper.insert(lostItem);
        if(rows<=0){
            throw new BusinessException("发布事务信息失败");
        }
    }

    @Override
    public void updateLostItem(Integer userId,Integer id,LostItemSaveDTO lostItemSaveDTO){
        LostItem lostItem=lostItemMapper.selectById(id);
        if(lostItem==null||lostItem.getStatus()==2){
            throw new BusinessException("失物信息不存在");
        }

        if(!lostItem.getUserId().equals(userId)){
            throw new BusinessException("无权修改他人发布的失物信息");
        }

        lostItem.setItemName(lostItemSaveDTO.getItemName());
        lostItem.setLostLocation(lostItemSaveDTO.getLostLocation());
        lostItem.setLostTime(lostItemSaveDTO.getLostTime());
        lostItem.setDescription(lostItemSaveDTO.getDescription());
        lostItem.setImageUrl(lostItemSaveDTO.getImageUrl());
        lostItem.setContactInfo(lostItemSaveDTO.getContactInfo());


        int rows = lostItemMapper.updateById(lostItem);
        if (rows <= 0) {
            throw new BusinessException("修改失物信息失败");}
    }

    @Override
    public void deleteLostItem(Integer userId, Integer id) {
        LostItem lostItem = lostItemMapper.selectById(id);
        if (lostItem == null || lostItem.getStatus() == 2) {
            throw new BusinessException("失物信息不存在");
        }
        if (!lostItem.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人发布的失物信息");
        }
        lostItem.setStatus(2);
        int rows = lostItemMapper.updateById(lostItem);
        if (rows <= 0) {
            throw new BusinessException("删除失物信息失败");
        }
    }

    @Override
    public List<LostItem> listLostItems(ItemQueryDTO queryDTO){
        lostItemMapper.update(
                null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<LostItem>()
                        .set(LostItem::getIsTop, 0)
                        .set(LostItem::getTopExpireTime, null)
                        .eq(LostItem::getIsTop, 1)
                        .isNotNull(LostItem::getTopExpireTime)
                        .lt(LostItem::getTopExpireTime, java.time.LocalDateTime.now())
        );

        LambdaQueryWrapper<LostItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(LostItem::getStatus,2);

        if(queryDTO.getItemName()!=null && !queryDTO.getItemName().isBlank()){
            wrapper.like(LostItem::getItemName,queryDTO.getItemName());
        }
        if(queryDTO.getLocation()!=null && !queryDTO.getLocation().isBlank()){
            wrapper.like(LostItem::getLostLocation,queryDTO.getLocation());
        }
        wrapper.orderByDesc(LostItem::getIsTop)
                .orderByDesc(LostItem::getCreateTime);
        List<LostItem> items = lostItemMapper.selectList(wrapper);

// 仅用于返回展示，不写回数据库
        for (LostItem item : items) {
            String rawDesc = item.getDescription();
            String displayDesc = (rawDesc == null || rawDesc.isBlank()) ? "暂无描述" : rawDesc;
            item.setDescription("【失物ID:" + item.getId() + "】" + displayDesc);
        }

        return items;
    }

    @Override
    public void resolveLostItem(Integer userId, Integer id) {
        LostItem lostItem = lostItemMapper.selectById(id);
        if (lostItem == null || lostItem.getStatus() == 2) {
            throw new BusinessException("失物信息不存在");
        }
        if (!lostItem.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人失误信息");
        }

        lostItem.setStatus(1);
        int rows=lostItemMapper.updateById(lostItem);
        if (rows <= 0) {
            throw new BusinessException("标记找回失败");
        }
    }
}
