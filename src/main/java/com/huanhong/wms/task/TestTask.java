package com.huanhong.wms.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class TestTask {



    @Scheduled(cron = "0 0 6,18 * * ?")
    public void reLogin() {
        Instant startTime = Instant.now();
        log.info("***** Test开始 *****");



        Instant endTime = Instant.now();
        log.info("***** Test结束，耗时:{}ms *****", ChronoUnit.MILLIS.between(startTime, endTime));
    }
}
