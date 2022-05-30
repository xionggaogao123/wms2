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
@ApiModel(description = "新增盘点报告")
public class AddMakeInventoryReportDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "盘点报告编号")
    private String reportNumber;

    @NotBlank
    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;

    @Min(1)
    @Max(5)
    @NotNull
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer planStatus;

    @Min(0)
    @Max(1)
    @NotNull
    @ApiModelProperty(value = "是否全盘: 0-非全盘 1-全盘")
    private Integer allMake;

    @Min(0)
    @Max(1)
    @NotNull
    @ApiModelProperty(value = "状态: 0-待盘点，1-已盘点")
    private Integer checkStatus;

    @NotBlank
    @ApiModelProperty(value = "盘点人")
    private String checkerIds;

    @ApiModelProperty(value = "稽核人Id")
    private Integer auditId;

    @NotBlank
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @NotNull
    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @NotNull
    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "计划类别-1-正常、2-加急、3-补计划、请选择（默认）")
    private Integer planClassification;
}
