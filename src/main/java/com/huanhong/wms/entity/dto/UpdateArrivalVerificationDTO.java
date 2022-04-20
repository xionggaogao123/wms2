package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description="到货检验明细表")
public class UpdateArrivalVerificationDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "采购合同编号")
    private String contractNumber;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态: 1草拟 2审批中 3审批生效 4作废")
    private Integer planStatus;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "检验状态：0-未检验，1-部分检验，2-全部检验")
    private Integer verificationStatus;


    @ApiModelProperty(value = "到货日期")
    private LocalDateTime deliveryDate;

    @ApiModelProperty(value = "理货人")
    private String inspector;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "车号")
    private String carNumber;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @ApiModelProperty(value = "备注")
    private String remark;

}
