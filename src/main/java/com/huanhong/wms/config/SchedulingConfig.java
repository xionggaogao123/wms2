package com.huanhong.wms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务开关
 *
 * @author ldy
 */
@Configuration
// 启用定时任务
@EnableScheduling
// 配置文件读取是否启用此配置
@ConditionalOnProperty(prefix = "spring.scheduling", name = "enabled")
public class SchedulingConfig {
}