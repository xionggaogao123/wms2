package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description="更新调拨计划出库主表")
public class UpdateAllocationOutDTO {

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;


    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;


    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;


    @ApiModelProperty(value = "库管员")
    private String librarian;


    @ApiModelProperty(value = "检验人")
    private String verification;


    @ApiModelProperty(value = "备注")
    private String remark;

}
