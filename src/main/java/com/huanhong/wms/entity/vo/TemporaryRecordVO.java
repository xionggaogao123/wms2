package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "分页查询临库出入库记录")
public class TemporaryRecordVO {

    @ApiModelProperty(value = "单据编号")
    private String number;

    @ApiModelProperty(value = "需求计划单据编号")
    private String requirementsPlanningNumber;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "库管员")
    private String warehouseManager;

    @ApiModelProperty(value = "类型")
    private String recordType;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;


}
