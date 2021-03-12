package com.imooc.resource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author liangwq
 * @date 2021/3/10
 */
@Component
@PropertySource("classpath:file.properties")
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileResource {

    private String host;
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
    private String ossHost;

}
