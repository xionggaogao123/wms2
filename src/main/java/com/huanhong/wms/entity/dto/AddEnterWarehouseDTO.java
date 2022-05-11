package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "新增入库单")
public class AddEnterWarehouseDTO {

    private static final long serialVersionUID=1L;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "入库类型-1. 暂估入库（默认）2.正式入库")
    private Integer storageType;


    @ApiModelProperty(value = "采购合同编号")
    private String contractNumber;


    @NotBlank
    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;


    @Min(1)
    @Max(4)
    @NotNull
    @ApiModelProperty(value = "状态:1.草拟2.审批中3.审批生效4.作废 5.驳回")
    private Integer state;

    @NotBlank
    @ApiModelProperty(value = "到货检验单编号")
    private String verificationDocumentNumber;

    @Min(1)
    @Max(3)
    @NotNull
    @ApiModelProperty(value = "计划类别-1.正常、2.加急、3.补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "发票号")
    private String receiptNumber;

    @NotNull
    @ApiModelProperty(value = "到货日期")
    private LocalDateTime deliveryDate;

    @NotBlank
    @ApiModelProperty(value = "经办人")
    private String manager;

    @NotBlank
    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "备注")
    private String remark;

}
