package com.qg.lostfound.service.impl.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.dto.chat.PrivateMessageAddDTO;
import com.qg.lostfound.entity.chat.PrivateMessage;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.PrivateMessageMapper;
import com.qg.lostfound.service.chat.PrivateMessageService;
import com.qg.lostfound.service.notify.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 私聊消息业务实现类
 * 功能：实现用户私信发送、聊天记录查询、消息标记已读
 */
@Service
@RequiredArgsConstructor
public class PrivateMessageServiceImpl implements PrivateMessageService {

    private final PrivateMessageMapper privateMessageMapper;
    private final NotificationService notificationService;
    /**
     * 发送私聊消息
     * @param senderId 发送者ID（当前登录用户）
     * @param dto 消息传输对象（接收者ID、消息内容）
     */
    @Override
    public void sendMessage(Integer senderId, PrivateMessageAddDTO dto) {
        if (senderId.equals(dto.getReceiverId())) {
            throw new BusinessException("不能给自己发私信");
        }

        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setIsRead(0);

        privateMessageMapper.insert(message);

        notificationService.createNotification(
                dto.getReceiverId(),
                "MESSAGE",
                message.getId(),
                "收到新私信",
                dto.getContent()
        );
    }
    /**
     * 查询两个用户之间的聊天记录
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     * @return 按时间升序排列的聊天记录列表
     */
    @Override
    public List<PrivateMessage> listConversation(Integer userId, Integer otherUserId) {
        return privateMessageMapper.selectList(
                new LambdaQueryWrapper<PrivateMessage>()
                        .and(wrapper -> wrapper
                                .eq(PrivateMessage::getSenderId, userId)
                                .eq(PrivateMessage::getReceiverId, otherUserId)
                                .or()
                                .eq(PrivateMessage::getSenderId, otherUserId)
                                .eq(PrivateMessage::getReceiverId, userId))
                        .orderByAsc(PrivateMessage::getCreateTime)
        );
    }
    /**
     * 将当前用户收到的、来自对方的消息标记为已读
     * @param userId 当前用户ID（消息接收者）
     * @param otherUserId 对方用户ID（消息发送者）
     */
    @Override
    public void readMyMessages(Integer userId, Integer otherUserId) {
        List<PrivateMessage> messages = privateMessageMapper.selectList(
                new LambdaQueryWrapper<PrivateMessage>()
                        .eq(PrivateMessage::getSenderId, otherUserId)
                        .eq(PrivateMessage::getReceiverId, userId)
                        .eq(PrivateMessage::getIsRead, 0)
        );

        for (PrivateMessage message : messages) {
            message.setIsRead(1);
            privateMessageMapper.updateById(message);
        }
    }
}