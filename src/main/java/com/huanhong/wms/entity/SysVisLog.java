package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="系统访问日志表")
public class SysVisLog extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "名称")
    private String name;

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

    @ApiModelProperty(value = "操作类型（字典 1登入 2登出）")
    private Integer visType;

    @ApiModelProperty(value = "访问时间")
    private LocalDateTime visTime;

    @ApiModelProperty(value = "访问账号")
    private String account;


}
