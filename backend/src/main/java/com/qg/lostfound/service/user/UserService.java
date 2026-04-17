package com.qg.lostfound.service.user;

import com.qg.lostfound.dto.user.ChangePasswordDTO;
import com.qg.lostfound.dto.user.UpdateProfileDTO;
import com.qg.lostfound.vo.user.UserInfoVO;
public interface UserService {

    UserInfoVO getCurrentUserInfo(Integer userId);
    void changePassword(Integer userId,ChangePasswordDTO changePasswordDTO);
    void updateProfile(Integer userId,UpdateProfileDTO updateProfileDTO);
}

