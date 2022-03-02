package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@ApiModel(description = "更新入库单")
public class UpdateEnterWarehouseDTO {


    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "入库单ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @ApiModelProperty(value = "采购合同编号")
    private String contractNumber;

    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "入库类型-1. 暂估入库（默认）2.正式入库")
    private Integer storageType;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态:1.草拟2.审批中3.审批生效4.作废")
    private Integer state;

    @ApiModelProperty(value = "到货检验单编号")
    private String verificationDocumentNumber;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1.正常、2.加急、3.补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "发票号")
    private String receiptNumber;

    @ApiModelProperty(value = "到货日期")
    private LocalDateTime deliveryDate;

    @ApiModelProperty(value = "经办人")
    private String manager;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
