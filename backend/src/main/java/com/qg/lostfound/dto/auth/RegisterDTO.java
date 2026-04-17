package com.qg.lostfound.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 20,message="用户名的长度必须在3到20之间")
    private String username;

    @NotBlank(message="邮箱不能为空")
    @Email(message="邮箱格式不正确")
    private String email;

    @NotBlank(message ="手机号不能为空")
    @Pattern(regexp="^1[3-9]\\d{9}$",message="手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max=20,message = "密码长度必须在6到20位之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
