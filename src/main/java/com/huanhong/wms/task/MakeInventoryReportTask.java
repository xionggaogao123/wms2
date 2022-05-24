package com.huanhong.wms.task;

import com.huanhong.wms.service.MakeInventoryReportTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @Author wang
 * @date 2022/5/23 14:12
 * 盘点报告定时任务
 */
@Slf4j
@Component
public class MakeInventoryReportTask {

    @Resource
    private MakeInventoryReportTaskService makeInventoryReportTaskService;

    /**
     * 每5分钟同步任务
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void syncTask() {
        Instant startTime = Instant.now();
        log.info("***** 盘点报告生成任务开始 *****");
        try {
            makeInventoryReportTaskService.makeInventoryReportCreate();
        } catch (Exception e) {
            log.error("***** 盘点报告生成任务异常 *****", e);
        }
        Instant endTime = Instant.now();
        log.info("***** Test结束，耗时:{}ms *****", ChronoUnit.MILLIS.between(startTime, endTime));
    }
}
