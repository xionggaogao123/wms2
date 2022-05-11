package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "临库入库更新")
public class UpdateTemporaryEnterWarehouseDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态:1.草拟 2.审批中 3.审批生效 4.作废 5.驳回")
    private Integer state;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @Min(0)
    @ApiModelProperty(value = "实收数量")
    private Double actualQuantity;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime enterTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}
