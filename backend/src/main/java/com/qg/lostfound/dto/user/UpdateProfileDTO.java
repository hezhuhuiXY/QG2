package com.qg.lostfound.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UpdateProfileDTO {

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max=255,message="头像地址长度不能超过255")
    private String avatarUrl;

    @Size(max=255,message="联系信息长度不能超过255")
    private String contactInfo;

    @Pattern(regexp = "^1[3-9]\\d{9}$",message="手机号格式不正确")
    private String phone;
}
