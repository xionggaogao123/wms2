package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description="调拨计划主表更新DTO")
public class UpdateAllocationPlanDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "业务类型：1 计划调拨 2 预备调拨")
    private Integer businessType;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废")
    private Integer planStatus;

    @ApiModelProperty(value = "调拨日期")
    private LocalDateTime assignmentDate;

    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @ApiModelProperty(value = "调出负责人")
    private String sendUser;

    @ApiModelProperty(value = "调入仓库")
    private String receiveWarehouse;

    @ApiModelProperty(value = "调入负责人")
    private String receiveUser;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "备注")
    private String remark;

}
