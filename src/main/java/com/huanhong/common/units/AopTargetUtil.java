package com.huanhong.common.units;

import com.huanhong.common.units.requestno.RequestNoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * 获取代理原始对象的工具
 */
@Slf4j
public class AopTargetUtil {


    /**
     * 获取被代理的对象本身
     */
    public static Object getTarget(Object proxy) {

        // 判断是不是代理对象，如果不是直接返回
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }

        try {
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                return getJdkDynamicProxyTargetObject(proxy);
            } else {
                return getCglibProxyTargetObject(proxy);
            }
        } catch (Exception e) {
            log.error(">>> 获取代理对象异常，请求号为：{}，具体信息为：{}", RequestNoUtil.get(), e.getMessage());
            return null;
        }
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
    }

}
