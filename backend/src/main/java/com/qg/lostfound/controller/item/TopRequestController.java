package com.qg.lostfound.controller.item;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.dto.item.TopRequestAddDTO;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.service.item.TopRequestService;
import com.qg.lostfound.utils.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/top-requests")
@RequiredArgsConstructor
public class TopRequestController {

    private final TopRequestService topRequestService;

    @PostMapping
    public Result<Void> addTopRequest(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid TopRequestAddDTO dto) {

        Integer userId = getUserId(authorization);
        topRequestService.addTopRequest(userId, dto);
        return Result.success();
    }

    private Integer getUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization.startsWith("Bearer ")
                ? authorization.substring(7) : authorization;
        return JwtUtils.getUserId(token);
    }
}