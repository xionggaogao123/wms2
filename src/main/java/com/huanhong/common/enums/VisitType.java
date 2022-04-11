package com.huanhong.common.enums;

import lombok.Getter;

/**
 * 访问日志类型枚举
 *
 */
@Getter
public enum VisitType {

    /**
     * 登录日志
     */
    LOGIN(1, "登录"),

    /**
     * 退出日志
     */
    EXIT(2, "登出");

    private final Integer code;

    private final String message;

    VisitType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
