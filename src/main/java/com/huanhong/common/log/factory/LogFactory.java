package com.huanhong.common.log.factory;

import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.LogSuccessStatus;
import com.huanhong.common.enums.VisitType;
import com.huanhong.common.units.JoinPointUtil;
import com.huanhong.wms.bean.enums.SymbolConstant;
import com.huanhong.wms.entity.SysOpLog;
import com.huanhong.wms.entity.SysVisLog;
import org.aspectj.lang.JoinPoint;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 日志对象创建工厂
 *
 */
public class LogFactory {

    /**
     * 创建登录日志
     *
     */
    static void createSysLoginLog(SysVisLog sysVisLog, String account, Integer successCode, String failMessage) {
        sysVisLog.setName(VisitType.LOGIN.getMessage());
        sysVisLog.setIsSuccess(successCode);

        sysVisLog.setVisType(VisitType.LOGIN.getCode());
        sysVisLog.setVisTime(LocalDateTime.now());
        sysVisLog.setAccount(account);

        if (LogSuccessStatus.SUCCESS.getCode().equals(successCode)) {
            sysVisLog.setMessage(VisitType.LOGIN.getMessage() + LogSuccessStatus.SUCCESS.getMessage());
        }
        if (LogSuccessStatus.FAIL.getCode().equals(successCode)) {
            sysVisLog.setMessage(VisitType.LOGIN.getMessage() +
                    LogSuccessStatus.FAIL.getMessage() + SymbolConstant.COLON + failMessage);
        }
    }

    /**
     * 创建登出日志
     *
     */
    static void createSysExitLog(SysVisLog sysVisLog, String account) {
        sysVisLog.setName(VisitType.EXIT.getMessage());
        sysVisLog.setIsSuccess(LogSuccessStatus.SUCCESS.getCode());
        sysVisLog.setMessage(VisitType.EXIT.getMessage() + LogSuccessStatus.SUCCESS.getMessage());
        sysVisLog.setVisType(VisitType.EXIT.getCode());
        sysVisLog.setVisTime(LocalDateTime.now());
        sysVisLog.setAccount(account);
    }

    /**
     * 创建操作日志
     *
     */
    static void createSysOperationLog(SysOpLog sysOpLog, String account, OperateLog operateLog, JoinPoint joinPoint, String result) {
        fillCommonSysOpLog(sysOpLog, account, operateLog, joinPoint);
        sysOpLog.setIsSuccess(LogSuccessStatus.SUCCESS.getCode());
        sysOpLog.setResult(result);
        sysOpLog.setMessage(LogSuccessStatus.SUCCESS.getMessage());
    }

    /**
     * 创建异常日志
     *
     */
    static void createSysExceptionLog(SysOpLog sysOpLog, String account, OperateLog operateLog, JoinPoint joinPoint, Exception exception) {
        fillCommonSysOpLog(sysOpLog, account, operateLog, joinPoint);
        sysOpLog.setIsSuccess(LogSuccessStatus.FAIL.getCode());
        sysOpLog.setMessage(Arrays.toString(exception.getStackTrace()));
    }

    /**
     * 生成通用操作日志字段
     *
     */
    private static void fillCommonSysOpLog(SysOpLog sysOpLog, String account, OperateLog operateLog, JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();

        String methodName = joinPoint.getSignature().getName();

        String param = JoinPointUtil.getArgsJsonString(joinPoint);

        sysOpLog.setName(operateLog.title());
        sysOpLog.setOpType(operateLog.type().name());
        sysOpLog.setClassName(className);
        sysOpLog.setMethodName(methodName);
        sysOpLog.setParam(param);
        sysOpLog.setOpTime(LocalDateTime.now());
        sysOpLog.setAccount(account);
    }

    /**
     * 构建基础访问日志
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:36
     */
    public static SysVisLog genBaseSysVisLog(String ip, String location, String browser, String os) {
        SysVisLog sysVisLog = new SysVisLog();
        sysVisLog.setIp(ip);
        sysVisLog.setLocation(location);
        sysVisLog.setBrowser(browser);
        sysVisLog.setOs(os);
        return sysVisLog;
    }

    /**
     * 构建基础操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:36
     */
    public static SysOpLog genBaseSysOpLog(String ip, String location, String browser, String os, String url, String method) {
        SysOpLog sysOpLog = new SysOpLog();
        sysOpLog.setIp(ip);
        sysOpLog.setLocation(location);
        sysOpLog.setBrowser(browser);
        sysOpLog.setOs(os);
        sysOpLog.setUrl(url);
        sysOpLog.setReqMethod(method);
        return sysOpLog;
    }

}
