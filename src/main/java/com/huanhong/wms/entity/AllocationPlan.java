package com.huanhong.wms.entity;

import java.time.LocalDateTime;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="调拨计划主表")
public class AllocationPlan extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "调拨申请单据编号")
    private String allocationNumber;

    @ApiModelProperty(value = "业务类型：1 计划调拨 2 预备调拨")
    private String businessType;

    @ApiModelProperty(value = "计划状态-草拟 待审核  驳回  通过")
    private String planStatus;

    @ApiModelProperty(value = "调拨日期")
    private LocalDateTime assignmentDate;

    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @ApiModelProperty(value = "调入仓库")
    private String receiveWarehouse;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;


}
