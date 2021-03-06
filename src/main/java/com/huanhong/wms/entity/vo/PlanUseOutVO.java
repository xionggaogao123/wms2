package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "领料出库查询对象", description = "领料出库查询对象封装")
public class PlanUseOutVO {

    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态:1.草拟,2.审批中,3.审批生效,4.作废 5.驳回")
    private Integer status;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1.正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "出库类型：0-暂存库出库 1-正式库出库")
    private Integer outType;

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
