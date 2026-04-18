package com.qg.lostfound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;
/**
     * 配置静态资源访问映射
     * 作用：把 服务器本地磁盘路径 映射为 浏览器可访问的 URL
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadProperties.getDir()).toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);// 映射到服务器本地磁盘路径
    }
}
