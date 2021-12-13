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
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {

    /**
     * 主域名
     */
    private String domain;

}
