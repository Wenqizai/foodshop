package com.imooc.config.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 头像上传配置
 * @author liangwq
 * @date 2021/1/10
 */
@PropertySource("classpath:file-upload-dev.properties")
@ConfigurationProperties(prefix = "file")
@Component
public class FileUpload {

    @Value("imageUserFaceLocation")
    private String imageUserFaceLocation;

    public String getImageUserFaceLocation() {
        return imageUserFaceLocation;
    }

    public void setImageUserFaceLocation(String imageUserFaceLocation) {
        this.imageUserFaceLocation = imageUserFaceLocation;
    }
}
