package com.qg.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qg.lostfound.entity.notify.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}