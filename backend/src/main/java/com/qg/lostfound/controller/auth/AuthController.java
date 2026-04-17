package com.qg.lostfound.controller.auth;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.auth.LoginDTO;
import com.qg.lostfound.dto.auth.RegisterDTO;
import com.qg.lostfound.service.auth.AuthService;
import com.qg.lostfound.vo.auth.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    /**
     * 认证控制器
     * 功能：处理用户注册、用户登录等身份认证相关接口
     */
    private final AuthService authService;
    /**
     * 用户注册接口
     * 请求方式：POST
     * 请求路径：/auth/register
     *
     * @param registerDTO 前端传入的注册信息（账号、密码、昵称等），@Valid 开启参数校验
     * @return 注册成功/失败结果
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success();
    }
    /**
     * 用户登录接口
     * 请求方式：POST
     * 请求路径：/auth/login
     *
     * @param loginDTO 前端传入的登录信息（账号、密码），@Valid 开启参数校验
     * @return 登录成功后返回 token、用户信息等（封装在 LoginVO 中）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }


}
