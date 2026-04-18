package com.qg.lostfound.controller.upload;

import com.qg.lostfound.common.result.Result;
import com.qg.lostfound.config.UploadProperties;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.utils.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    /**
     * 上传配置属性
     */
    private final UploadProperties uploadProperties;
    /**
     * 允许上传的图片类型
     */
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * 通用图片上传接口
     * 前端 form-data 传 file
     */
    @PostMapping("/image")
    public Result<String> uploadImage(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("file") MultipartFile file
    ) {
        // 只要能解析出用户，说明已登录
        getUserIdFromHeader(authorization);

        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        if (file.getSize() > uploadProperties.getMaxImageSize()) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException("只支持 jpg、png、gif、webp 格式图片");
        }
        // 获取上传文件的原始文件名（例如：test.png）
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀名（.png/.jpg等），优先从文件名获取，没有则根据文件类型推断
        String suffix = getFileSuffix(originalFilename, contentType);

        LocalDate now = LocalDate.now();
        // 拼接【相对访问路径】格式：/uploads/年/月/日
        String relativeDir = String.format("/uploads/%d/%02d/%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        Path saveDir = Paths.get(uploadProperties.getDir(),
                String.valueOf(now.getYear()),
                String.format("%02d", now.getMonthValue()),
                String.format("%02d", now.getDayOfMonth()));

        try {
            Files.createDirectories(saveDir);

            String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path targetPath = saveDir.resolve(newFileName);

            file.transferTo(targetPath.toFile());

            String relativeUrl = relativeDir + "/" + newFileName;

            // 返回完整可访问地址
            String fullUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path(relativeUrl)
                    .toUriString();

            return Result.success(fullUrl);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("图片上传失败");
        }
    }

    private Integer getUserIdFromHeader(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException("未登录或登录已失效");
        }
        String token = authorization;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        return JwtUtils.getUserId(token);
    }

    /**
     * 优先取原文件后缀，没有的话根据 contentType 推断
     */
    private String getFileSuffix(String originalFilename, String contentType) {
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new BusinessException("不支持的图片格式");
        };
    }
}