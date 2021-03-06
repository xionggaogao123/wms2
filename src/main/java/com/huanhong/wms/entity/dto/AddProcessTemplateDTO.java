package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("新增流程预设")
public class AddProcessTemplateDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "流程代码")
    private String processCode;

    @NotEmpty
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "步骤")
    private Integer step;

    @NotEmpty
    @ApiModelProperty(value = "登录账号或角色 id")
    private String loginName;

    @ApiModelProperty(value = "显示名")
    private String name;

    @NotNull
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
