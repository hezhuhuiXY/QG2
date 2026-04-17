package com.qg.lostfound.service.chat;

import com.qg.lostfound.dto.chat.PrivateMessageAddDTO;
import com.qg.lostfound.entity.chat.PrivateMessage;

import java.util.List;
/**
 * 私聊消息服务接口
 * 功能：用户之间发送私信、查询聊天记录、标记消息为已读
 */
public interface PrivateMessageService {
    /**
     * 发送私聊消息
     *
     * @param senderId 发送者用户ID
     * @param dto      消息接收者ID、内容等参数
     */
    void sendMessage(Integer senderId, PrivateMessageAddDTO dto);
    /**
     * 查询两个用户之间的聊天记录
     *
     * @param userId       当前用户ID
     * @param otherUserId  对方用户ID
     * @return 两人之间的私聊消息列表
     */
    List<PrivateMessage> listConversation(Integer userId, Integer otherUserId);
    /**
     * 将当前用户收到的、来自对方用户的消息标记为已读
     *
     * @param userId       当前用户ID（消息接收者）
     * @param otherUserId  对方用户ID（消息发送者）
     */
    void readMyMessages(Integer userId, Integer otherUserId);
}