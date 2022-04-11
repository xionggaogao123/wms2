package com.huanhong.common.exception.enums;


import com.huanhong.common.annotion.ExceptionType;
import com.huanhong.common.exception.ExceptionCodeUtil;
import com.huanhong.common.exception.ExceptionConstant;
import com.huanhong.common.exception.enums.abs.AbstractBaseExceptionEnum;

/**
 * 请求方法相关异常枚举
 *
 */
@ExceptionType(module = ExceptionConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExceptionConstant.REQUEST_METHOD_EXCEPTION_ENUM)
public enum RequestMethodExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 不支持该请求方法，请求方法应为POST
     */
    REQUEST_METHOD_IS_POST(1, "不支持该请求方法，请求方法应为POST"),

    /**
     * 不支持该请求方法，请求方法应为GET
     */
    REQUEST_METHOD_IS_GET(2, "不支持该请求方法，请求方法应为GET");

    private final Integer code;

    private final String message;

    RequestMethodExceptionEnum(Integer code, String message) {
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
