package com.qg.lostfound.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * 登录接口返回给前端的 VO 对象
 * 作用：封装登录成功后需要返回给前端的所有数据
 */
@Data
@AllArgsConstructor
public class LoginVO {
    /**
     * 用户身份凭证（JWT 令牌）
     * 前端后续请求接口时，需要放在请求头 Authorization 中使用
     */
    private String token;
    private Integer userId;
    private String username;
    private String nickname;
    private Integer role;
}
