package com.huanhong.common.log.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.units.requestno.RequestNoUtil;
import com.huanhong.wms.entity.SysOpLog;
import com.huanhong.wms.entity.SysVisLog;
import com.huanhong.wms.service.ISysOpLogService;
import com.huanhong.wms.service.ISysVisLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import java.util.TimerTask;


/**
 * 日志操作任务创建工厂
 *
 */
@Slf4j
public class LogTaskFactory {

    private static final ISysVisLogService sysVisLogService = SpringUtil.getBean(ISysVisLogService.class);

    private static final ISysOpLogService sysOpLogService = SpringUtil.getBean(ISysOpLogService.class);

    /**
     * 登录日志
     *
     */
    public static TimerTask loginLog(SysVisLog sysVisLog, final String account, Integer success, String failMessage) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    LogFactory.createSysLoginLog(sysVisLog, account, success, failMessage);
                    sysVisLogService.save(sysVisLog);
                } catch (Exception e) {
                    log.error(">>> 创建登录日志异常，请求号为：{}，具体信息为：{}", RequestNoUtil.get(), e.getMessage());
                }
            }
        };
    }

    /**
     * 登出日志
     *
     * @author xuyuxiang
     * @date 2020/3/12 15:21
     */
    public static TimerTask exitLog(SysVisLog sysVisLog, String account) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    LogFactory.createSysExitLog(sysVisLog, account);
                    sysVisLogService.save(sysVisLog);
                } catch (Exception e) {
                    log.error(">>> 创建退出日志异常，请求号为：{}，具体信息为：{}", RequestNoUtil.get(), e.getMessage());
                }
            }
        };
    }

    /**
     * 操作日志
     *
     */
    public static TimerTask operationLog(SysOpLog sysOpLog, String account, OperateLog operateLog, JoinPoint joinPoint, String result) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    LogFactory.createSysOperationLog(sysOpLog, account, operateLog, joinPoint, result);
                    sysOpLogService.save(sysOpLog);
                } catch (Exception e) {
                    log.error(">>> 创建操作日志异常，请求号为：{}，具体信息为：{}", RequestNoUtil.get(), e.getMessage());
                }
            }
        };
    }

    /**
     * 异常日志
     *
     */
    public static TimerTask exceptionLog(SysOpLog sysOpLog, String account, OperateLog operateLog, JoinPoint joinPoint, Exception exception) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    LogFactory.createSysExceptionLog(sysOpLog, account, operateLog, joinPoint, exception);
                    sysOpLogService.save(sysOpLog);
                } catch (Exception e) {
                    log.error(">>> 创建异常日志异常，请求号为：{}，具体信息为：{}", RequestNoUtil.get(), e.getMessage());
                }
            }
        };
    }
}
