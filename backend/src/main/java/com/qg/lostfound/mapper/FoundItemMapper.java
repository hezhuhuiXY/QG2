package com.qg.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qg.lostfound.entity.item.FoundItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoundItemMapper extends BaseMapper<FoundItem> {
}