package com.huanhong.common.exception;

import com.huanhong.common.exception.enums.IResultMsg;
import com.huanhong.common.exception.enums.APIEnum;
import io.swagger.annotations.ApiModel;

/**
 * 业务异常，service层或者proxy层抛出的异常
 */

@ApiModel("业务的异常模型")
public class BizException extends AbstractException {
    private static final long serialVersionUID = -644174310560740232L;

    public BizException(IResultMsg error) {
        super(error);
    }

    public BizException(String error) {

        super(new IResultMsg() {

            private static final long serialVersionUID = -7026523857294062402L;

            @Override
            public Integer getCode() {
                return APIEnum.BIZ_ERROR.getCode();
            }

            @Override
            public String getMessage() {
                return error;
            }
        });
    }

    public BizException(IResultMsg error, Throwable cause) {
        super(error, cause);
    }

    public BizException(Throwable cause) {
        super(APIEnum.BIZ_ERROR, cause);
    }
}