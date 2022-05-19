package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "更新盘点报告明细")
public class UpdateMakeInventoryReportDetailsDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "盘点报告Id", required = true)
    private Integer id;

    @Min(1)
    @Max(5)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer planStatus;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否全盘: 0-非全盘 1-全盘")
    private Integer allMake;

    @Min(0)
    @Max(3)
    @ApiModelProperty(value = "盘点状态: 0-待盘点，1-一致，2-盘盈，3-盘亏")
    private Integer checkStatusDetails;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "盈亏金额")
    private BigDecimal finalAmounts;

    @ApiModelProperty(value = "差异原因")
    private String reason;

    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "盈亏数量")
    private Double finalCredit;

    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "盘点人Id")
    private Integer checkerId;

    @ApiModelProperty(value = "盘点人名")
    private String checkerName;

    @ApiModelProperty(value = "备注")
    private String remark;

}
