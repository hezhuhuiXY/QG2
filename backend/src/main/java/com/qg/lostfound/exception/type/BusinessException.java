package com.qg.lostfound.exception.type;
/**
 * 自定义业务异常类
 * 作用：专门用于处理项目中的【业务逻辑错误】
 * 例如：用户不存在、密码错误、无权限、操作失败等
 * 区别于系统代码报错，属于我们主动抛出的“预期内异常”
 */
public class BusinessException extends RuntimeException{
    /**
     * 构造方法
     * @param message 业务错误提示信息（如：无管理员权限、用户已被封禁）
     */
    public BusinessException(String message){
        super(message);
    }
}
