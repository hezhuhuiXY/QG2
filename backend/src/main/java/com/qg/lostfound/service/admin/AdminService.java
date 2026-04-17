package com.qg.lostfound.service.admin;

import com.qg.lostfound.vo.admin.AdminStatsVO;
import com.qg.lostfound.vo.admin.AdminUserVO;

import java.time.LocalDateTime;
import java.util.List;
public interface AdminService {

    List<AdminUserVO> listUsers();

    void banUser(Integer adminUserId,Integer targetUserId);

    void unbanUser(Integer adminUserId,Integer targetUserId);

    void deleteLostItemByAdmin(Integer id);

    void deleteFoundItemByAdmin(Integer id);

    AdminStatsVO getPlatformStats(LocalDateTime startTime,LocalDateTime endTime);
}
