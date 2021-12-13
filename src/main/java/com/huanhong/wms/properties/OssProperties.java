package com.huanhong.wms.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Setter
@Getter
@ToString
@Component
@ConfigurationProperties(prefix = "project.oss")
public class OssProperties {

    /**
     * 网络访问路口
     */
    private String url;

    /**
     * 云路径
     */
    private String yunUrl;

    /**
     * 本地文件路径
     */
    private String path;

}
