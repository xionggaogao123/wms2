package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "需求计划表")
public class RequirementsPlanningVO {

    @ApiModelProperty(value = "单据编号（需求计划）")
    private String planNumber;

    @ApiModelProperty(value = "计划部门")
    private String planUnit;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1-正常、2-加急、3-补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @Min(0)
    @ApiModelProperty(value = "预估总金额")
    private BigDecimal estimatedTotalAmount;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态: 1-草拟、2-审批中、3-审批生效、4-作废")
    private Integer  planStatus;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateStart;

    @ApiModelProperty(value = "创建日期-结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateEnd;

}
