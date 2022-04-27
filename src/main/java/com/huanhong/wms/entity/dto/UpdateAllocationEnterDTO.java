package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description="更新调拨入库主表")
public class UpdateAllocationEnterDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;

    @ApiModelProperty(value = "库管员")
    private String librarian;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "调入单位")
    private String receiveCompany;

}
