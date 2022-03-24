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
@ApiModel(description="到货检验主表")
public class AddArrivalVerificationDTO {

    @ApiModelProperty(value = "采购合同编号")
    private String contractNumber;

    @NotBlank
    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;


    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态: 1草拟 2审批中 3审批生效 4作废")
    private Integer planStatus;

    @NotNull
    @ApiModelProperty(value = "到货日期")
    private LocalDateTime deliveryDate;

    @NotBlank
    @ApiModelProperty(value = "理货人")
    private String inspector;

    @NotBlank
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @NotBlank
    @ApiModelProperty(value = "车号")
    private String carNumber;

    @NotBlank
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

}
