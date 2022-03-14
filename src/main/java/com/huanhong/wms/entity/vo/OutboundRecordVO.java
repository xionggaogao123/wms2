package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;


@Data
@ApiModel(description="出库记录查询VO")
public class OutboundRecordVO {

    @ApiModelProperty(value = "原单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;


    @ApiModelProperty(value = "详细信息（json 货位 批次 数量）")
    private String details;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态：0-审批中（锁库存）1-审批生效（出库）")
    private Integer status;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateStart;

    @ApiModelProperty(value = "创建日期-结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateEnd;
}
