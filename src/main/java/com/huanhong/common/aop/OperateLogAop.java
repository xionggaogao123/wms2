package com.huanhong.common.aop;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.log.LogManager;
import com.huanhong.common.units.user.CurrentUserUtil;
import com.huanhong.wms.bean.Constant;
import com.huanhong.wms.bean.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 操作日志aop切面
 *
 */
@Aspect
@Order(1000)
public class OperateLogAop {

    /**
     * 日志切入点
     */
    @Pointcut("@annotation(com.huanhong.common.annotion.OperateLog)")
    private void getLogPointCut() {
    }

    /**
     * 操作成功返回结果记录日志
     */
    @AfterReturning(pointcut = "getLogPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        String account = Constant.UNKNOWN;
        if(ObjectUtil.isNotNull(loginUser)) {
            account = loginUser.getLoginName();
        }
        //异步记录日志
        LogManager.singleton().executeOperationLog(
                operateLog, account, joinPoint, JSON.toJSONString(result));
    }

    /**
     * 操作发生异常记录日志
     *
     */
    @AfterThrowing(pointcut = "getLogPointCut()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        String account = Constant.UNKNOWN;
        if(ObjectUtil.isNotNull(loginUser)) {
            account = loginUser.getLoginName();
        }
        //异步记录日志
        LogManager.singleton().executeExceptionLog(
                operateLog, account, joinPoint, exception);
    }
}
