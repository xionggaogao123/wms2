package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="新增调拨入库主表")
public class AddAllocationEnterDTO {

    private static final long serialVersionUID=1L;

    @NotBlank
    @ApiModelProperty(value = "调拨计划单据编号")
    private String allocationNumber;

    @NotBlank
    @ApiModelProperty(value = "调拨出库单据编号")
    private String allocationOutNumber;


    @ApiModelProperty(value = "调拨入库单据编号-系统自动生成")
    private String allocationEnterNumber;

    @NotBlank
    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @NotBlank
    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;

    @NotBlank
    @ApiModelProperty(value = "库管员")
    private String librarian;

    @ApiModelProperty(value = "调入单位")
    private String receiveCompany;

    @ApiModelProperty(value = "备注")
    private String remark;
}
