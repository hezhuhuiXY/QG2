package com.qg.lostfound.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.qg.lostfound.entity.item.FoundItem;
import com.qg.lostfound.entity.item.LostItem;
import com.qg.lostfound.entity.user.User;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.FoundItemMapper;
import com.qg.lostfound.mapper.LostItemMapper;
import com.qg.lostfound.mapper.UserMapper;
import com.qg.lostfound.service.admin.AdminService;
import com.qg.lostfound.vo.admin.AdminUserVO;
import com.qg.lostfound.vo.admin.AdminStatsVO;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final  UserMapper  userMapper;
    private final LostItemMapper lostItemMapper;
    private final FoundItemMapper foundItemMapper;

    @Override
    public List<AdminUserVO> listUsers() {
        //调用MyBatis-Plus查询用户,按时间降序排列
        List<User> users=userMapper.selectList(
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime)
        );
        //将数据库实体类User转换为前端视图类AdminUserVO,返回给前端
        return users.stream().map(user->{
            //创建视图对象
            AdminUserVO vo=new AdminUserVO();
            //拷贝属性,把User所有同名属性复制到AdminUserVO
            BeanUtils.copyProperties(user,vo);
            return vo;
            //把流转list集合返回
        }).collect(Collectors.toList());
    }

    @Override
    public void banUser(Integer adminUserId,Integer targetUserId) {
        if(adminUserId.equals(targetUserId)){
            throw new BusinessException("不能封禁自己");
        }

        User user =userMapper.selectById(targetUserId);
        if(user==null){
            throw new BusinessException("用户不存在");
        }

        if(user.getRole()!=null&&user.getRole()==1){
            throw new BusinessException("不能封禁管理员");
        }

        user.setStatus(1);

        int rows=userMapper.updateById(user);
        if(rows<=0){
            throw new BusinessException("封禁用户失败");
        }
    }

    @Override
    public void unbanUser(Integer adminUserId,Integer targetUserId) {
        User user =userMapper.selectById(targetUserId);
        if(user==null){
            throw new BusinessException("用户不存在");
        }

        user.setStatus(0);

        int rows = userMapper.updateById(user);
        if(rows<=0){
            throw new BusinessException("解封用户失败");
        }
    }
   //删除失物
    @Override
    public void deleteLostItemByAdmin(Integer id) {
        LostItem lostItem=lostItemMapper.selectById(id);
        if(lostItem==null||lostItem.getStatus()==2){
            throw new BusinessException("失物信息不存在");
        }
        lostItem.setStatus(2);
        int rows = lostItemMapper.updateById(lostItem);
        if (rows <= 0) {
            throw new BusinessException("删除失物信息失败");}
    }
    //删除拾物
    @Override
    public void deleteFoundItemByAdmin(Integer id) {
        FoundItem foundItem=foundItemMapper.selectById(id);
        if (foundItem == null || foundItem.getStatus() == 2) {
            throw new BusinessException("拾物信息不存在");
        }
        foundItem.setStatus(2);
        int rows = foundItemMapper.updateById(foundItem);
        if (rows <= 0) {
            throw new BusinessException("删除拾物信息失败");
        }
    }
    /**
     * 获取平台统计数据（管理员后台首页数据）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计视图对象：总发布数、总完成数、活跃用户数
     */
    @Override
    public AdminStatsVO getPlatformStats(LocalDateTime startTime, LocalDateTime endTime) {
        //统计未删除的失物发布数
        long lostPublishCount=lostItemMapper.selectCount(
                new LambdaQueryWrapper<LostItem>().ne(LostItem::getStatus,2)
        );
        //统计未删除的拾物发布数
        long foundPublishCount=foundItemMapper.selectCount(
                new LambdaQueryWrapper<FoundItem>().ne(FoundItem::getStatus,2)
        );
        //统计完成的失物数
        long lostResolveCount=lostItemMapper.selectCount(
                new LambdaQueryWrapper<LostItem>().ne(LostItem::getStatus,1)
        );
        //统计完成的拾物数
        long foundResolveCount=foundItemMapper.selectCount(
                new LambdaQueryWrapper<FoundItem>().ne(FoundItem::getStatus,1)
        );
        //存放去重后活跃用户的ID
        Set<Integer> activeUserIds=new HashSet<>();
        //查询失物表中所有未删除的用户ID(去重)
        QueryWrapper<LostItem> lostQueryWrapper=new QueryWrapper<>();
        lostQueryWrapper.select("distinct user_id").ne("status",2);
        //查询拾物表中所有未删除的用户ID(去重)
        QueryWrapper<FoundItem> foundQueryWrapper=new QueryWrapper<>();
        foundQueryWrapper.select("distinct user_id").ne("status",2);
        //添加时间
        if(startTime!=null){
            lostQueryWrapper.ge("create_time",startTime);
            foundQueryWrapper.le("create_time",startTime);
        }
        if(endTime!=null){
            lostQueryWrapper.le("create_time",endTime);
            foundQueryWrapper.ge("create_time",endTime);
        }
        //获取所有发布过失物的用户的ID
        List<Object> lostUserIds=lostItemMapper.selectObjs(lostQueryWrapper);
        //获取所有发布过拾物的用户的ID
        List<Object> foundUserIds=foundItemMapper.selectObjs(foundQueryWrapper);
        //把用户ID存入Set自动去重
        for(Object obj:lostUserIds){
            activeUserIds.add(((Number) obj).intValue());
        }
        for(Object obj:foundUserIds){
            activeUserIds.add(((Number) obj).intValue());
        }
        //封装并返回统计数据
        return new AdminStatsVO(
                lostPublishCount+foundPublishCount,//发布量
                lostResolveCount+foundResolveCount,//完成量
                activeUserIds.size()//活跃用户数
        );
    }
}
