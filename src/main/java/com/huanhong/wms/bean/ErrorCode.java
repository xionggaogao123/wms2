package com.huanhong.wms.bean;

/**
 * 错误异常码
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @version v1.0
 * @since 2017/3/2 13:47
 */
public interface ErrorCode {

    /** 参数不合法 */
    int PARAM_ILLEGAL = 0;
    /** 参数错误 */
    int PARAM_ERROR = 400;
    /** 没权限 */
    int NO_AUTHORITY= 401;
    /** 没权限 */
    int REQUEST_LIMIT= 402;
    /** 系统错误 */
    int SYSTEM_ERROR = 500;
    /** 令牌格式错误 */
    int TOKEN_ERROR = 1000;
    /** 令牌为空 */
    int TOKEN_NULL = 1001;
    /** 令牌无效 */
    int TOKEN_INVALID = 1002;
    /** 令牌过期 */
    int TOKEN_OVERDUE = 1003;
    /** 数据已存在 */
    int DATA_EXISTS_ERROR = 1004;
    /** 数据已被删除或不存在 */
    int DATA_IS_NULL = 1005;
    /** 参数格式错误 */
    int PARAM_FORMAT_ERROR = 1006;
    /**
     * 参数为空
     */
    int PARAM_IS_NULL = 1007;
    /**
     * 参数输入错误
     */
    int PARAM_IN_ERROR = 1008;
    /**
     * 参数输入不正确
     */
    int PARAM_INCORRECT = 1009;
    /**
     * 用户不存在
     */
    int USER_NOT_EXIST = 1010;
    /**
     * 是否继续
     */
    int CHECK_GMT = 1011;
    /**
     * 验证码信息
     */
    int CHECK_INFO = 1012;

}
