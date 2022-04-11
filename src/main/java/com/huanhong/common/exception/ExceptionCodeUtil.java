package com.huanhong.common.exception;


import com.huanhong.common.annotion.ExceptionType;

/**
 * 异常枚举code值快速创建
 */
public class ExceptionCodeUtil {

    public static Integer getExceptionCode(Class<?> clazz, int code) {

        // 默认的异常响应码
        Integer defaultCode = Integer.valueOf("" + 99 + 9999 + 9);

        if (clazz == null) {
            return defaultCode;
        } else {
            ExceptionType exceptionType = clazz.getAnnotation(ExceptionType.class);
            if (exceptionType == null) {
                return defaultCode;
            }
            return Integer.valueOf("" + exceptionType.module() + exceptionType.kind() + code);
        }

    }

}
