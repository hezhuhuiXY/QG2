package com.qg.lostfound.service.impl.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.dto.auth.LoginDTO;
import com.qg.lostfound.dto.auth.RegisterDTO;
import com.qg.lostfound.entity.user.User;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.UserMapper;
import com.qg.lostfound.service.auth.AuthService;
import com.qg.lostfound.utils.security.PasswordUtils;
import com.qg.lostfound.utils.security.JwtUtils;
import com.qg.lostfound.vo.auth.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    @Override
    public void register(RegisterDTO registerDTO) {
        //校验两次密码是否一致
        if(!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
           throw new BusinessException("两次输入密码不一致");
        }
        //校验用户是否存在
        Long usernameCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername,registerDTO.getUsername())
        );
        if(usernameCount > 0){
            throw new BusinessException("用户名已存在");
        }
        //校验邮箱是否存在
        Long emailCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail,registerDTO.getEmail())
        );
        if(emailCount > 0){
            throw new BusinessException("邮箱已被注册");
        }
        //校验手机号是否已存在
        Long phoneCount = userMapper.selectCount(
          new LambdaQueryWrapper<User>().eq(User::getPhone,registerDTO.getPhone())
        );
        if(phoneCount > 0){
            throw new BusinessException("手机号已被注册");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setNickname(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        //密码加密
        user.setPassword(PasswordUtils.encode(registerDTO.getPassword()));

        //设置默认值(0为用户,1为管理员)
        user.setStatus(0);
        user.setRole(0);
        user.setAvatarUrl(null);
        user.setContactInfo(null);

        //保存
        int insert = userMapper.insert(user);
        if(insert<=0){
            throw new BusinessException("注册失败");
        }

    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String account = loginDTO.getAccount();
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername,account)
                        .or()
                        .eq(User::getPhone,account)
                        .or()
                        .eq(User::getEmail,account)
        );
        if(user == null){
            throw new BusinessException("用户不存在");
        }
        if(user.getStatus() != null&&user.getStatus() == 1){
            throw new BusinessException("账号已被封禁");
        }
        if(!PasswordUtils.matches(loginDTO.getPassword(),user.getPassword())){
            throw new BusinessException("账号或密码错误");
        }
        //校验通过,生成JWT令牌(传入用户ID,用户名,角色)
        String token=JwtUtils.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
        //封装返回结果给前端
        return new LoginVO(
                token,
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole()
        );
    }
}
