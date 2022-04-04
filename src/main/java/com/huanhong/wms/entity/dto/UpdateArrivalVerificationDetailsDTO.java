package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "更新到货检验明细表")
public class UpdateArrivalVerificationDetailsDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "检验状态：0-未检验，1-部分检验，2-全部检验")
    private Integer verificationStatus;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "有效期")
    private LocalDateTime validPeriod;

    @Min(0)
    @ApiModelProperty(value = "到货数量")
    private Double arrivalQuantity;

    @Min(0)
    @ApiModelProperty(value = "合格数量")
    private Double qualifiedQuantity;

    @Min(0)
    @ApiModelProperty(value = "不合格数量")
    private Double unqualifiedQuantity;

    @ApiModelProperty(value = "备注")
    private String remark;

}
