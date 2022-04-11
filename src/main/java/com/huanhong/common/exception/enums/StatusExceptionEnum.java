package com.huanhong.common.exception.enums;


import com.huanhong.common.annotion.ExceptionType;
import com.huanhong.common.exception.ExceptionCodeUtil;
import com.huanhong.common.exception.ExceptionConstant;
import com.huanhong.common.exception.enums.abs.AbstractBaseExceptionEnum;

/**
 * 状态枚举
 *
 */
@ExceptionType(module = ExceptionConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExceptionConstant.STATUS_EXCEPTION_ENUM)
public enum StatusExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 请求状态值为空
     */
    REQUEST_EMPTY(1, "请求状态值为空"),

    /**
     * 请求状值为非正确状态值
     */
    NOT_WRITE_STATUS(2, "请求状态值不合法"),

    /**
     * 更新状态失败，试图更新被删除的记录
     */
    UPDATE_STATUS_ERROR(3, "更新状态失败，您试图更新被删除的记录");

    private final Integer code;

    private final String message;

    StatusExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ExceptionCodeUtil.getExceptionCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
