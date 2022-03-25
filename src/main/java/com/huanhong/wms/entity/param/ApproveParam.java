package com.huanhong.wms.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(description="审批参数")
public class ApproveParam {

    private String taskId;
    private String message;
    private String username;
    private Map<String, Object> variables;
    private Boolean isFirst;

    @ApiModelProperty(value = "抄送人账号")
    private List<String> accounts;

    @ApiModelProperty(value = "签名密码")
    private String signPassWord;

    @ApiModelProperty(value = "审批类型 1.通过 2.驳回")
    private Integer type;

    @ApiModelProperty(value = "用户id")
    private Integer userId;


}
