package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "采购计划单查询")
public class ProcurementPlanVO {

    @ApiModelProperty(value = "需求计划单编号")
    private String originalDocumentNumber;

    @ApiModelProperty(value = "采购计划单据编号")
    private String planNumber;

    @ApiModelProperty(value = "计划类别-1正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "状态:1.草拟 2.审批中 3.审批生效 4.作废 5.驳回")
    private Integer status;

    @ApiModelProperty(value = "计划部门")
    private String planningDepartment;

    @ApiModelProperty(value = "计划员")
    private String planner;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "需求部门")
    private String demandDepartment;

    @ApiModelProperty(value = "入库日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "入库日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

    @ApiModelProperty(value = "是否被导入 0.否 1.是")
    private Integer isImported;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;
}
