package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("流程预设更新")
public class UpdateProcessTemplateDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "流程代码")
    private String processCode;


    @ApiModelProperty(value = "库房编号")
    private String warehouseId;


    @Min(1)
    @ApiModelProperty(value = "步骤")
    private Integer step;


    @ApiModelProperty(value = "登录账号")
    private String loginName;

    @ApiModelProperty(value = "显示名")
    private String name;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "模版类型 1.审批人 2.抄送人")
    private Integer templateType;

    @NotNull
    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "用户类型 1.用户 2.角色")
    private Integer userType;
}
