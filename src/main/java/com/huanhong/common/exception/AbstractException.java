package com.huanhong.common.exception;

import com.huanhong.wms.bean.Result;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

public abstract class AbstractException extends RuntimeException {

    private static final long serialVersionUID = -1813767369790744025L;

    @Getter
    @ApiModelProperty(name = "错误消息对象", reference = "Result", required = true)
    private final Result<Object> msg;

    public AbstractException(Result<Object> error) {
        super(error.getMessage());
        msg = error;
    }

    public AbstractException(Result<Object> error, Throwable cause) {
        super(error.getMessage(), cause);
        msg = error;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public AbstractException(Throwable cause) {
        super(cause);
        msg = Result.failure("系统异常");
    }
}
