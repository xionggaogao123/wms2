package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="新增调拨计划出库主表")
public class AddAllocationOutDTO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "调拨出库单据编号-系统自动生成")
    private String allocationOutNumber;

    @NotBlank
    @ApiModelProperty(value = "调拨计划单据编号")
    private String allocationNumber;

    @NotBlank
    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @NotBlank
    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;

    @NotBlank
    @ApiModelProperty(value = "库管员")
    private String librarian;

    @NotBlank
    @ApiModelProperty(value = "检验人")
    private String verification;


    @ApiModelProperty(value = "调出单位")
    private String sendCompany;


    @ApiModelProperty(value = "备注")
    private String remark;
}
