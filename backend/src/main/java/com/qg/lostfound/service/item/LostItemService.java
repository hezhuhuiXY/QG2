package com.qg.lostfound.service.item;

import com.qg.lostfound.dto.item.ItemQueryDTO;
import com.qg.lostfound.dto.item.LostItemSaveDTO;
import com.qg.lostfound.entity.item.LostItem;
import java.util.List;
public interface LostItemService {

    void addLostItem(Integer userId, LostItemSaveDTO lostItemSaveDTO);

    void updateLostItem(Integer userId, Integer id, LostItemSaveDTO lostItemSaveDTO);

    void deleteLostItem(Integer userId, Integer id);

    List<LostItem> listLostItems(ItemQueryDTO queryDTO);

    void resolveLostItem(Integer userId, Integer id);
}