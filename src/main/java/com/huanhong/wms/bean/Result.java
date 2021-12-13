package com.huanhong.wms.bean;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Json封装类
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @date 2017/12/18 11:38
 */
@Data
@ApiModel(description = "结果封装类")
public class Result<T> {

    @ApiModelProperty(value = "请求状态")
    private int status = ErrorCode.SYSTEM_ERROR;

    @ApiModelProperty(value = "是否成功")
    private boolean ok = false;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回对象")
    private T data;

    // 返回正确结果
    public static <T> Result<T> success() {
        return success(null, "success");
    }

    public static <T> Result<T> success(T data) {
        return success(data, "success");
    }

    public static <T> Result<T> success(T data, String message) {
        Result<T> item = new Result<>();
        item.setOk(true);
        item.setData(data);
        item.setStatus(200);
        item.setMessage(message);
        return item;
    }

    // 返回错误结果
    public static <T> Result<T> failure(String errorMessage) {
        return failure(500, errorMessage);
    }

    public static <T> Result<T> failure(int errorCode, String errorMessage) {
        Result<T> item = new Result<>();
        item.setOk(false);
        item.setStatus(errorCode);
        item.setMessage(errorMessage);
        return item;
    }

    public static <T> Result<T> failure(int errorCode, String errorMessage, T data) {
        Result<T> item = new Result<>();
        item.setOk(false);
        item.setStatus(errorCode);
        item.setMessage(errorMessage);
        item.setData(data);
        return item;
    }

    public static <T> Result<T> noDataError() {
        return failure(ErrorCode.DATA_IS_NULL, "数据不存在或已删除");
    }

    public static <T> Result<T> noAuthority() {
        return failure(ErrorCode.NO_AUTHORITY, "没有权限执行该操作");
    }

    public void setOk(boolean ok) {
        this.ok = ok;
        if (ok) {
            this.status = Constant.HTTP_SUCCEED;
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
