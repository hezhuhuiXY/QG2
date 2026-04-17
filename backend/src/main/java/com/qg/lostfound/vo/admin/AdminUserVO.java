package com.qg.lostfound.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {

    private Integer id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private String avatarUrl;

    private String contactInfo;

    private Integer role;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}