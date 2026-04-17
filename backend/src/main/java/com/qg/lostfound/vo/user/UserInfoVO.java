package com.qg.lostfound.vo.user;
import lombok.Data;

@Data
public class UserInfoVO {
        private Integer id;
        private String username;
        private String nickname;
        private String email;
        private String phone;
        private String avatarUrl;
        private String contactInfo;
        private Integer role;
        private Integer status;

}
