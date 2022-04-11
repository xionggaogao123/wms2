package com.huanhong.common.exception.enums;


import com.huanhong.common.annotion.ExceptionType;
import com.huanhong.common.exception.ExceptionCodeUtil;
import com.huanhong.common.exception.ExceptionConstant;
import com.huanhong.common.exception.enums.abs.AbstractBaseExceptionEnum;

/**
 * 请求类型相关异常枚举
 *
 */
@ExceptionType(module = ExceptionConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExceptionConstant.REQUEST_TYPE_EXCEPTION_ENUM)
public enum RequestTypeExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 参数传递格式不支持
     */
    REQUEST_TYPE_IS_JSON(1, "参数传递格式不支持，请使用JSON格式传参"),

    /**
     * 请求JSON参数格式不正确
     */
    REQUEST_JSON_ERROR(2, "请求JSON参数格式不正确，请检查参数格式");

    private final Integer code;

    private final String message;

    RequestTypeExceptionEnum(Integer code, String message) {
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
