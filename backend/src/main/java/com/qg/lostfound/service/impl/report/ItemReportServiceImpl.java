package com.qg.lostfound.service.impl.report;

import com.qg.lostfound.dto.report.ItemReportAddDTO;
import com.qg.lostfound.dto.report.ItemReportAuditDTO;
import com.qg.lostfound.entity.item.FoundItem;
import com.qg.lostfound.entity.item.LostItem;
import com.qg.lostfound.entity.report.ItemReport;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.FoundItemMapper;
import com.qg.lostfound.mapper.ItemReportMapper;
import com.qg.lostfound.mapper.LostItemMapper;
import com.qg.lostfound.service.report.ItemReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.entity.user.User;
import com.qg.lostfound.mapper.UserMapper;
import com.qg.lostfound.service.notify.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class ItemReportServiceImpl implements ItemReportService {
    private final LostItemMapper lostItemMapper;
    private final FoundItemMapper foundItemMapper;
    private final ItemReportMapper itemReportMapper;
    private final NotificationService notificationService;
    private final UserMapper userMapper;
    @Override
    public void addReport(Integer userId,ItemReportAddDTO dto){
        if (dto.getItemType() == 1) {
            LostItem lostItem = lostItemMapper.selectById(dto.getItemId());
            if (lostItem == null || lostItem.getStatus() == 2) {
                throw new BusinessException("失物信息不存在");
            }
        } else if (dto.getItemType() == 2) {
            FoundItem foundItem = foundItemMapper.selectById(dto.getItemId());
            if (foundItem == null || foundItem.getStatus() == 2) {
                throw new BusinessException("拾物信息不存在");
            }
        } else {
            throw new BusinessException("举报类型错误");
        }

        ItemReport report=new ItemReport();
        report.setItemType(dto.getItemType());
        report.setItemId(dto.getItemId());
        report.setReporterId(userId);
        report.setReason(dto.getReason());
        report.setStatus(0);

        int rows = itemReportMapper.insert(report);
        if (rows <= 0) {
            throw new BusinessException("提交举报失败");
        }

        notifyAdminsForReport(report);
    }

    @Override
    public void approveReport(Integer adminUserId, Integer reportId, ItemReportAuditDTO dto) {
        ItemReport report = itemReportMapper.selectById(reportId);
        if (report == null || report.getStatus() != 0) {
            throw new BusinessException("举报记录不存在或已审核");
        }
        report.setStatus(1);
        report.setReviewAdminId(adminUserId);
        report.setReviewRemark(dto.getReviewRemark());
        report.setReviewTime(LocalDateTime.now());
        //根据物品类型处理对应物品
        if (report.getItemType() == 1) {
            LostItem lostItem = lostItemMapper.selectById(report.getItemId());
            // 物品存在 且 不是已下架/已处理状态，才更新状态
            if (lostItem != null && lostItem.getStatus() != 2) {
                lostItem.setStatus(2);
                lostItemMapper.updateById(lostItem);
            }
        } else {
            FoundItem foundItem = foundItemMapper.selectById(report.getItemId());
            if (foundItem != null && foundItem.getStatus() != 2) {
                foundItem.setStatus(2);
                foundItemMapper.updateById(foundItem);
            }
        }
        //最终更新举报记录到数据库
        itemReportMapper.updateById(report);
    }

    @Override
    public void rejectReport(Integer adminUserId, Integer reportId, ItemReportAuditDTO dto) {
        ItemReport report = itemReportMapper.selectById(reportId);
        if (report == null || report.getStatus() != 1) {
            throw new BusinessException("举报记录不存在或已审核");
        }
        report.setStatus(2);
        report.setReviewAdminId(adminUserId);
        report.setReviewRemark(dto.getReviewRemark());
        report.setReviewTime(LocalDateTime.now());
        itemReportMapper.updateById(report);
    }

    private void notifyAdminsForReport(ItemReport report) {
        List<User> admins = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, 1)
                        .eq(User::getStatus, 0)
        );

        String itemTypeText = report.getItemType() == 1 ? "失物" : "拾物";
        String title = "收到新的举报";
        String content = "用户提交了一条" + itemTypeText + "举报，请尽快审核。举报ID：" + report.getId();

        for (User admin : admins) {
            notificationService.createNotification(
                    admin.getId(),
                    "REPORT",
                    report.getId(),
                    title,
                    content
            );
        }
    }


}
