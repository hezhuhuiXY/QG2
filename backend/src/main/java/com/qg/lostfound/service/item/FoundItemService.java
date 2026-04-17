package com.qg.lostfound.service.item;

import com.qg.lostfound.dto.item.FoundItemSaveDTO;
import com.qg.lostfound.dto.item.ItemQueryDTO;
import com.qg.lostfound.entity.item.FoundItem;

import java.util.List;
public interface FoundItemService {

    void addFoundItem(Integer userId, FoundItemSaveDTO foundItemSaveDTO);

    void updateFoundItem(Integer userId, Integer id, FoundItemSaveDTO foundItemSaveDTO);

    void deleteFoundItem(Integer userId, Integer id);

     List<FoundItem> listFoundItems(ItemQueryDTO queryDTO);

    void claimFoundItem(Integer userId, Integer id);
}