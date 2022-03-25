package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "领料出库查询对象", description = "领料出库查询对象封装")
public class PlanUseOutVO {

    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @ApiModelProperty(value = "状态状态状态:1.草拟,2.审批中,3.审批生效,4.作废")
    private Integer status;

    @ApiModelProperty(value = "计划类别-1.正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "库管员")
    private String librarian;

    @ApiModelProperty(value = "领用人")
    private String recipient;

    @ApiModelProperty(value = "出库状态：0-未出库，1-部分出库，2-全部出库 // PDA端 0-未出库&出库 1-全部出库")
    private Integer outStatus;

    @ApiModelProperty(value = "申请日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applicationDateStart;

    @ApiModelProperty(value = "申请日期-结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applicationDateEnd;
}