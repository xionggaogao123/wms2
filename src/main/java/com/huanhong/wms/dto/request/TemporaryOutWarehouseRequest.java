package com.huanhong.wms.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @Author wang
 * @date 2022/5/25 16:38
 */
@Data
public class TemporaryOutWarehouseRequest {

    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @ApiModelProperty(value = "审批状态:1.草拟 2.审批中 3.审批生效 4.作废 5.驳回")
    private Integer status;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @NotBlank(message = "领用单位不能为空")
    @ApiModelProperty(value = "领用单位",required = true)
    private String requisitioningUnit;

    @NotBlank(message = "领用人不能为空")
    @ApiModelProperty(value = "领用人",required = true)
    private String recipient;

    @NotBlank(message = "库房ID不能为空")
    @ApiModelProperty(value = "库房ID",required = true)
    private String warehouseId;

    @NotBlank(message = "库管员不能为空")
    @ApiModelProperty(value = "库管员",required = true)
    private String librarian;

    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;

    @ApiModelProperty(value = "备注")
    private String remark;


    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "计划部门")
    private String planUnit;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @ApiModelProperty(value = "单据编号（需求计划）")
    private String planNumber;
}
