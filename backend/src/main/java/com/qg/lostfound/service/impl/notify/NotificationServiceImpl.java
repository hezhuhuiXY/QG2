package com.qg.lostfound.service.impl.notify;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.entity.notify.Notification;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.NotificationMapper;
import com.qg.lostfound.service.notify.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 消息通知业务实现类
 * 功能：创建系统通知、查询个人通知列表、标记通知为已读
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    //创建系统通知
    @Override
    public void createNotification(Integer userId, String bizType, Integer bizId, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setBizType(bizType);
        notification.setBizId(bizId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public List<Notification> listMyNotifications(Integer userId) {
        return notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime)
        );
    }

    @Override
    public void readNotification(Integer userId, Integer id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在");
        }

        notification.setIsRead(1);
        notificationMapper.updateById(notification);
    }
}