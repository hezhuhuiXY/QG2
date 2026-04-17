package com.qg.lostfound.service.auth;

import com.qg.lostfound.dto.auth.LoginDTO;
import com.qg.lostfound.dto.auth.RegisterDTO;
import com.qg.lostfound.vo.auth.LoginVO;
public interface AuthService {
    void register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);
}
