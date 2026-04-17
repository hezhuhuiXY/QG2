package com.qg.lostfound.exception.handler;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.exception.type.BusinessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 全局异常处理器
 * 作用：统一捕获项目中所有接口抛出的异常，封装成统一格式返回给前端
 * 避免前端收到杂乱的报错信息，保证接口返回格式一致
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕获【业务异常】
     * 项目中主动抛出的 BusinessException（如：无权限、操作失败、参数错误等）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e){
    return Result.fail(e.getMessage());
    }
    /**
     * 捕获【参数校验异常】
     * 前端传入参数不符合校验规则时触发（如 @NotBlank、@NotNull 等）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(message);
    }
    /**
     * 捕获【其他所有未知异常】
     * 系统级别错误、代码bug、未知错误等兜底处理
     * 避免前端看到500错误页面
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){

        return Result.fail("系统异常,请稍后重试");
    }

}
