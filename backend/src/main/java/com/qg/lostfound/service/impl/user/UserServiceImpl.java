package com.qg.lostfound.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.dto.user.ChangePasswordDTO;
import com.qg.lostfound.dto.user.UpdateProfileDTO;
import com.qg.lostfound.entity.user.User;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.UserMapper;
import com.qg.lostfound.service.user.UserService;
import com.qg.lostfound.utils.security.PasswordUtils;
import com.qg.lostfound.vo.user.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
   private final UserMapper userMapper;

   @Override
    public UserInfoVO getCurrentUserInfo(Integer userId){
       User user=userMapper.selectById(userId);
       if(user==null){
           throw new BusinessException("用户不存在");
       }

       UserInfoVO userInfoVO=new UserInfoVO();
       BeanUtils.copyProperties(user,userInfoVO);
       return userInfoVO;
   }

   @Override
    public void updateProfile(Integer userId,UpdateProfileDTO updateProfileDTO){
       User user=userMapper.selectById(userId);
       if(user==null){
           throw new BusinessException("用户不存在");
       }
       //修改手机号前检查手机号是否被使用
       if (StringUtils.hasText(updateProfileDTO.getPhone())
               && !updateProfileDTO.getPhone().equals(user.getPhone())) {
           Long count = userMapper.selectCount(
                   new LambdaQueryWrapper<User>()
                           .eq(User::getPhone, updateProfileDTO.getPhone())
                           .ne(User::getId, userId)
           );

           if(count>0){
               throw new BusinessException("手机号已被使用");
           }

           user.setPhone(updateProfileDTO.getPhone());
       }
       //检查各个属性是否为null,不为null才更新
       if(updateProfileDTO.getNickname()!=null){
           user.setNickname(updateProfileDTO.getNickname());
       }
       if(updateProfileDTO.getAvatarUrl()!=null){
           user.setAvatarUrl(updateProfileDTO.getAvatarUrl());
       }
       if(updateProfileDTO.getContactInfo()!=null){
           user.setContactInfo(updateProfileDTO.getContactInfo());
       }
       int rows=userMapper.updateById(user);
       if(rows<=0){
           throw new BusinessException("修改个人信息失败");
       }

   }

   @Override
    public void changePassword(Integer userId,ChangePasswordDTO changePasswordDTO){
       User user=userMapper.selectById(userId);
       if(user==null){
           throw new BusinessException("用户不存在");
       }
       if(!PasswordUtils.matches(changePasswordDTO.getOldPassword(),user.getPassword())){
           throw new BusinessException("原密码错误");
       }
       if(!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())){
          throw new BusinessException("两次输入密码不一致");
       }
       if(changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())){
           throw new BusinessException("新密码不能和原密码相同");
       }
       user.setPassword(PasswordUtils.encode(changePasswordDTO.getNewPassword()));
       int rows=userMapper.updateById(user);
       if(rows<=0){
           throw new BusinessException("修改密码失败");
       }
   }
}
