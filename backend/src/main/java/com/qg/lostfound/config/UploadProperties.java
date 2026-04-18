package com.qg.lostfound.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    /**
     * 文件保存根目录
     * 例如：D:/campus-lost-found-uploads
     */
    private String dir;

    /**
     * 单张图片最大大小，默认 5MB
     */
    private long maxImageSize = 5 * 1024 * 1024;
}