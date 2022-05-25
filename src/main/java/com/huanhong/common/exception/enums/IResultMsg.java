package com.huanhong.common.exception.enums;

import java.io.Serializable;

/**
 * 结果信息
 *
 **/
public interface IResultMsg extends Serializable {

    /**
     * @return 获取错误代码
     */
    Integer getCode();

    /**
     * 获取错误消息
     * @return
     */
    String getMessage();


}
