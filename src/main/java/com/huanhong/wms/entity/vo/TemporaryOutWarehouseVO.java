package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "临库出库分页查询")
public class TemporaryOutWarehouseVO {

    @ApiModelProperty(value = "出库单据编号")
    private String outNumber;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "审批状态:1.草拟 2.审批中 3.审批生效 4.作废 5.驳回")
    private Integer status;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;

    @ApiModelProperty(value = "领用人")
    private String recipient;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

}
