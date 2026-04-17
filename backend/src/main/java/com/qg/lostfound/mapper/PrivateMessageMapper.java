package com.qg.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qg.lostfound.entity.chat.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {
}