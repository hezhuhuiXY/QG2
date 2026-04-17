package com.qg.lostfound.common.result;
import lombok.Data;

/**
 * 全局统一返回结果类
 * 用于封装接口的响应数据，统一前端接收格式
 * @param <T> 泛型，代表返回的具体数据类型
 */
@Data
public class Result<T> {
	/**
	 * 响应状态码
	 * 200 表示成功，500 表示失败
	 */
    private int code;
    /**
     * 响应消息
     * 对本次请求结果的描述信息
     */
    private String message;
    /**
     * 响应数据
     * 接口返回的具体业务数据
     */
    private T data;
    /**
     * 成功响应（无返回数据）
     * @return 状态码200，消息success
     */
    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }
    /**
     * 成功响应（带返回数据）
     * @param data 要返回的业务数据
     * @return 状态码200，消息success，携带数据
     */
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    /**
     * 失败响应
     * @param message 失败原因
     * @return 状态码500，携带失败信息
     */
    public static <T> Result<T> fail(String message){
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

}
