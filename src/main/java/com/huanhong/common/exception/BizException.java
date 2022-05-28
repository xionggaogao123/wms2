package com.huanhong.common.exception;

import com.huanhong.common.exception.enums.APIEnum;
import com.huanhong.wms.bean.Result;
import io.swagger.annotations.ApiModel;

/**
 * 业务异常，service层或者proxy层抛出的异常
 */

@ApiModel("业务的异常模型")
public class BizException extends AbstractException {
    private static final long serialVersionUID = -644174310560740232L;

    public BizException(Result<Object> error) {
        super(error);
    }

    public BizException(String error) {
        super(Result.failure(APIEnum.BIZ_ERROR.getCode(), error));
    }

    public BizException(Result<Object> error, Throwable cause) {
        super(error, cause);
    }

    public BizException(Throwable cause) {
        super(Result.failure("未知异常"), cause);
    }
}