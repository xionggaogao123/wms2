package com.huanhong.wms.entity;

import java.time.LocalDateTime;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="系统操作日志表")
public class SysOpLog extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "操作类型")
    private String opType;

    @ApiModelProperty(value = "是否执行成功（1-是，0-否）")
    private Integer isSuccess;

    @ApiModelProperty(value = "具体消息")
    private String message;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "地址")
    private String location;

    @ApiModelProperty(value = "浏览器")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "请求地址")
    private String url;

    @ApiModelProperty(value = "类名称")
    private String className;

    @ApiModelProperty(value = "方法名称")
    private String methodName;

    @ApiModelProperty(value = "请求方式（GET POST PUT DELETE)")
    private String reqMethod;

    @ApiModelProperty(value = "请求参数")
    private String param;

    @ApiModelProperty(value = "返回结果")
    private String result;

    @ApiModelProperty(value = "操作时间")
    private LocalDateTime opTime;

    @ApiModelProperty(value = "操作账号")
    private String account;


}
