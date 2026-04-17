package com.qg.lostfound.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private String password;

    private String avatarUrl;

    private String contactInfo;

    private Integer status;

    private Integer role;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}