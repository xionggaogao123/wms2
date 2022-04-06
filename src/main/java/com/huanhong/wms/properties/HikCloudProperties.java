package com.huanhong.wms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "project.hik-cloud")
public class HikCloudProperties {
    public static final String TOKEN_FORMAT = "hik-cloud:token:{}";
    public static final String EZVIZ_TOKEN_FORMAT = "hik-cloud:token:ezviz:{}";
    public static final String CONSUMER_GROUP_FORMAT = "hik-cloud:consumer:message:{}";

    private String clientId;

    private String clientSecret;

    private String host;

    private String tokenUrl;

    private String ezvizTokenUrl;
    private String mqConsumerGroupUrl;
    private String mqConsumerMessageUrl;
    private String mqConsumerOffsetUrl;
    private String deviceCreateUrl;
    private String deviceGetUrl;
    private String groupGetUrl;
    private String groupCreateUrl;


}
