package com.huanhong.wms.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.huanhong.wms.service.HikCloudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Profile("prod")
@Component
public class HikMqTask {

    @Resource
    private HikCloudService hikCloudService;

    /**
     * 每5秒消费消息
     */
    @Scheduled(fixedDelay = 5*1000)
    public void consumerMessage() {
        TimeInterval timer = DateUtil.timer();
        log.info("***** 检查当前任务开始 *****");
        hikCloudService.consumerMessage();
        log.info("***** 检查当前任务结束，耗时:{}ms *****", timer.interval());
    }

}
