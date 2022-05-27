package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="平衡利库表")
public class BalanceLibrary extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "平衡利库单号")
    private String balanceLibraryNo;

    @ApiModelProperty(value = "采购计划单编号")
    private String procurementNos;

    @ApiModelProperty(value = "目标仓库")
    private String targetWarehouse;

    @ApiModelProperty(value = "计划类别-正常、加急、补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @ApiModelProperty(value = "计划部门")
    private String planningDepartment;

    @ApiModelProperty(value = "计划员")
    private String planner;

    @ApiModelProperty(value = "需求部门")
    private String demandDepartment;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建者")
    private String creater;

    @Version
    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;


}
