package com.qg.lostfound.service.impl.item;

import com.qg.lostfound.dto.item.TopRequestAddDTO;
import com.qg.lostfound.entity.item.LostItem;
import com.qg.lostfound.entity.item.TopRequest;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.LostItemMapper;
import com.qg.lostfound.mapper.TopRequestMapper;
import com.qg.lostfound.service.item.TopRequestService;
import com.qg.lostfound.entity.user.User;
import com.qg.lostfound.mapper.UserMapper;
import com.qg.lostfound.service.notify.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TopRequestServiceImpl implements TopRequestService {

    private final TopRequestMapper topRequestMapper;
    private final LostItemMapper lostItemMapper;
    private final NotificationService notificationService;
    private final UserMapper userMapper;

    @Override
    public void addTopRequest(Integer userId, TopRequestAddDTO dto) {
        LostItem lostItem = lostItemMapper.selectById(dto.getLostItemId());
        if (lostItem == null || lostItem.getStatus() == 2) {
            throw new BusinessException("失物信息不存在");
        }
        if (!lostItem.getUserId().equals(userId)) {
            throw new BusinessException("只能为自己的失物申请置顶");
        }

        TopRequest request = new TopRequest();
        request.setLostItemId(dto.getLostItemId());
        request.setUserId(userId);
        request.setReason(dto.getReason());
        request.setStatus(0);
        request.setTopHours(dto.getTopHours() == null ? 24 : dto.getTopHours());

        int rows = topRequestMapper.insert(request);
        if (rows <= 0) {
            throw new BusinessException("提交置顶申请失败");
        }

        notifyAdminsForTopRequest(request);
    }

    @Override
    public void approveTopRequest(Integer adminUserId, Integer requestId) {
        TopRequest request = topRequestMapper.selectById(requestId);
        if (request == null || request.getStatus() != 0) {
            throw new BusinessException("置顶申请不存在或已审核");
        }

        LostItem lostItem = lostItemMapper.selectById(request.getLostItemId());
        if (lostItem == null || lostItem.getStatus() == 2) {
            throw new BusinessException("对应失物信息不存在");
        }

        lostItem.setIsTop(1);
        lostItem.setTopExpireTime(LocalDateTime.now().plusHours(request.getTopHours()));
        lostItemMapper.updateById(lostItem);

        request.setStatus(1);
        request.setReviewAdminId(adminUserId);
        request.setReviewTime(LocalDateTime.now());
        topRequestMapper.updateById(request);
    }

    @Override
    public void rejectTopRequest(Integer adminUserId, Integer requestId) {
        TopRequest request = topRequestMapper.selectById(requestId);
        if (request == null || request.getStatus() != 0) {
            throw new BusinessException("置顶申请不存在或已审核");
        }

        request.setStatus(2);
        request.setReviewAdminId(adminUserId);
        request.setReviewTime(LocalDateTime.now());
        topRequestMapper.updateById(request);
    }

    private void notifyAdminsForTopRequest(TopRequest request) {
        List<User> admins = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, 1)
                        .eq(User::getStatus, 0)
        );

        String title = "收到新的置顶申请";
        String content = "用户提交了一条失物置顶申请，请尽快审核。申请ID：" + request.getId();

        for (User admin : admins) {
            notificationService.createNotification(
                    admin.getId(),
                    "TOP_REQUEST",
                    request.getId(),
                    title,
                    content
            );
        }
    }
}