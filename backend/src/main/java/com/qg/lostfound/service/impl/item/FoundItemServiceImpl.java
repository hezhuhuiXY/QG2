package com.qg.lostfound.service.impl.item;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.dto.item.FoundItemSaveDTO;
import com.qg.lostfound.dto.item.ItemQueryDTO;
import com.qg.lostfound.entity.item.FoundItem;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.FoundItemMapper;
import com.qg.lostfound.service.item.FoundItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * 拾物信息业务实现类
 * 功能：处理拾物的发布、修改、删除、查询、认领等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class FoundItemServiceImpl implements FoundItemService {

    private final FoundItemMapper foundItemMapper;
    //新增
    @Override
    public void addFoundItem(Integer userId, FoundItemSaveDTO foundItemSaveDTO) {
        FoundItem foundItem = new FoundItem();
        foundItem.setUserId(userId);

        foundItem.setItemName(foundItemSaveDTO.getItemName());
        foundItem.setFoundLocation(foundItemSaveDTO.getFoundLocation());
        foundItem.setFoundTime(foundItemSaveDTO.getFoundTime());
        foundItem.setDescription(foundItemSaveDTO.getDescription());
        foundItem.setImageUrl(foundItemSaveDTO.getImageUrl());
        foundItem.setContactInfo(foundItemSaveDTO.getContactInfo());
        foundItem.setStatus(0);

        int rows = foundItemMapper.insert(foundItem);
        if (rows <= 0) {
            throw new BusinessException("发布拾取信息失败");
        }
    }
    //修改拾取信息
    @Override
    public void updateFoundItem(Integer userId, Integer id, FoundItemSaveDTO foundItemSaveDTO) {
        FoundItem foundItem = foundItemMapper.selectById(id);
        if (foundItem == null || foundItem.getStatus() == 2) {
            throw new BusinessException("拾取信息不存在");
        }

        if (!foundItem.getUserId().equals(userId)) {
            throw new BusinessException("无权修改他人发布的拾取信息");
        }

        foundItem.setItemName(foundItemSaveDTO.getItemName());
        foundItem.setFoundLocation(foundItemSaveDTO.getFoundLocation());
        foundItem.setFoundTime(foundItemSaveDTO.getFoundTime());
        foundItem.setDescription(foundItemSaveDTO.getDescription());
        foundItem.setImageUrl(foundItemSaveDTO.getImageUrl());
        foundItem.setContactInfo(foundItemSaveDTO.getContactInfo());

        int rows = foundItemMapper.updateById(foundItem);
        if (rows <= 0) {
            throw new BusinessException("修改拾取信息失败");
        }
    }
    //删除发布
    @Override
    public void deleteFoundItem(Integer userId, Integer id) {
        FoundItem foundItem = foundItemMapper.selectById(id);
        if (foundItem == null || foundItem.getStatus() == 2) {
            throw new BusinessException("拾取信息不存在");
        }

        if (!foundItem.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人发布的拾取信息");
        }

        foundItem.setStatus(2);

        int rows = foundItemMapper.updateById(foundItem);
        if (rows <= 0) {
            throw new BusinessException("删除拾取信息失败");
        }
    }

    //条件查询拾物列表
    @Override
    public List<FoundItem> listFoundItems(ItemQueryDTO queryDTO) {
        LambdaQueryWrapper<FoundItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(FoundItem::getStatus, 2);

        if (queryDTO.getItemName() != null && !queryDTO.getItemName().isBlank()) {
            wrapper.like(FoundItem::getItemName, queryDTO.getItemName());
        }
        if (queryDTO.getLocation() != null && !queryDTO.getLocation().isBlank()) {
            wrapper.eq(FoundItem::getFoundLocation, queryDTO.getLocation());
        }

        wrapper.orderByDesc(FoundItem::getCreateTime);

        List<FoundItem> items = foundItemMapper.selectList(wrapper);

        // 仅用于返回展示，不写回数据库
        for (FoundItem item : items) {
            String rawDesc = item.getDescription();
            String displayDesc = (rawDesc == null || rawDesc.isBlank()) ? "暂无描述" : rawDesc;
            item.setDescription("【拾物ID:" + item.getId() + "】" + displayDesc);
        }

        return items;
    }
    //认领失物
    @Override
    public void claimFoundItem(Integer userId, Integer id) {
        FoundItem foundItem = foundItemMapper.selectById(id);
        if (foundItem == null || foundItem.getStatus() == 2) {
            throw new BusinessException("拾物信息不存在");
        }
        if (!foundItem.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人拾物信息");
        }

        foundItem.setStatus(1);
        int rows = foundItemMapper.updateById(foundItem);
        if (rows <= 0) {
            throw new BusinessException("标记已认领失败");
        }
    }
}