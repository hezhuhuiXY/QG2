package com.qg.lostfound.controller.user;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.user.ChangePasswordDTO;
import com.qg.lostfound.dto.user.UpdateProfileDTO;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.user.UserService;
import com.qg.lostfound.utils.security.JwtUtils;
import com.qg.lostfound.vo.user.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUserInfo(
            @RequestHeader("Authorization") String authorization
    ) {
        Integer useId = getUserIdFromHeader(authorization);
        return Result.success(userService.getCurrentUserInfo(useId));
    }
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UpdateProfileDTO updateProfileDTO){
        Integer userId=getUserIdFromHeader(authorization);
        userService.updateProfile(userId,updateProfileDTO);
        return Result.success();
    }
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO){
        Integer userId=getUserIdFromHeader(authorization);
        userService.changePassword(userId,changePasswordDTO);
        return Result.success();
    }

    private Integer getUserIdFromHeader(String authorization) {
        if(authorization==null||authorization.isBlank()){
          throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        return JwtUtils.getUserId(token);
    }
}
