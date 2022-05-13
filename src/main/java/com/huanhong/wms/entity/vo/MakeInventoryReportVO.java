package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "盘点报告分页查询")
public class MakeInventoryReportVO {

    @ApiModelProperty(value = "盘点报告编号")
    private String reportNumber;

    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;

    @Min(1)
    @Max(5)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer planStatus;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否全盘: 0-非全盘 1-全盘")
    private Integer allMake;

    @Min(0)
    @Max(1)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "状态: 0-待盘点，1-已盘点")
    private Integer checkStatus;

    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "盘点人")
    private String userId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

}
