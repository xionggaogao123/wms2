package com.huanhong.common.enums;

import lombok.Getter;

/**
 * 日志成功状态
 *
 */
@Getter
public enum LogSuccessStatus {

    /**
     * 失败
     */
    FAIL(0, "失败"),

    /**
     * 成功
     */
    SUCCESS(1, "成功");

    private final Integer code;

    private final String message;

    LogSuccessStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
