package com.qg.lostfound.service.notify;

import com.qg.lostfound.entity.notify.Notification;

import java.util.List;

public interface NotificationService {

    void createNotification(Integer userId, String bizType, Integer bizId, String title, String content);

    List<Notification> listMyNotifications(Integer userId);

    void readNotification(Integer userId, Integer id);
}