package com.huanhong.wms.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.huanhong.wms.service.IProcessAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ProcessTask {

    @Resource
    private IProcessAssignmentService processAssignmentService;

    /**
     * 每5分钟同步任务
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void syncTask() {
        TimeInterval timer = DateUtil.timer();
        log.info("***** 检查当前任务开始 *****");
        try {
            processAssignmentService.syncProcessAssignment();
        } catch (Exception e) {
            log.error("***** 检查当前任务异常 *****", e);
        }

        log.info("***** 检查当前任务结束，耗时:{}ms *****", timer.interval());
    }

}
