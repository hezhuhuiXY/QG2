package com.qg.lostfound.utils.security;

import  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
/**
 * 密码加密/校验工具类
 * 功能：使用 BCrypt 算法对用户密码进行加密，以及登录时校验密码是否正确
 * 特点：不可逆加密、自动加盐、无法被反向破解，非常安全
 */
public class PasswordUtils {
    //BCrypt 加密器（单例，全局复用）
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    /**
     * 密码加密（注册时使用）
     * @param rawPassword 用户明文密码（如：123456）
     * @return 加密后的密码（存到数据库里）
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    /**
     * 密码校验（登录时使用）
     * @param rawPassword 用户输入的明文密码
     * @param encodedPassword 数据库中存储的加密密码
     * @return 匹配返回 true，不匹配返回 false
     */
    public static boolean matches(String rawPassword,String encodedPassword){
        return encoder.matches(rawPassword, encodedPassword);
    }
}
