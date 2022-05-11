package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(description="调拨计划主表分页查询VO")
public class AllocationPlanVO {

    @ApiModelProperty(value = "调拨申请单据编号")
    private String allocationNumber;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "业务类型：1 计划调拨 2 预备调拨")
    private Integer businessType;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer planStatus;

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

    @ApiModelProperty(value = "调拨日期-起始")
    private LocalDateTime assignmentDateStart;

    @ApiModelProperty(value = "调拨日期-终结")
    private LocalDateTime assignmentDateEnd;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

}
