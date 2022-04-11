package com.huanhong.common.exception.enums;


import com.huanhong.common.annotion.ExceptionType;
import com.huanhong.common.exception.ExceptionCodeUtil;
import com.huanhong.common.exception.ExceptionConstant;
import com.huanhong.common.exception.enums.abs.AbstractBaseExceptionEnum;

/**
 * 授权和鉴权异常的枚举
 * <p>
 * 认证和鉴权的区别：
 * <p>
 * 认证可以证明你能登录系统，认证的过程是校验token的过程
 * 鉴权可以证明你有系统的哪些权限，鉴权的过程是校验角色是否包含某些接口的权限
 *
 */
@ExceptionType(module = ExceptionConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExceptionConstant.PERMISSION_EXCEPTION_ENUM)
public enum PermissionExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 资源路径不存在
     */
    URL_NOT_EXIST(1, "资源路径不存在，请检查请求地址"),

    /**
     * 没有权限访问资源
     */
    NO_PERMISSION(2, "没有权限访问资源，请联系管理员"),

    /**
     * 没有权限操作该数据
     */
    NO_PERMISSION_OPERATE(3, "没有权限操作该数据，请联系管理员");

    private final Integer code;

    private final String message;

    PermissionExceptionEnum(Integer code, String message) {
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
