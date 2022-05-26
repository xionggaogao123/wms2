package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "临库点验新增")
public class AddTemporaryLibraryInventoryDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "清点单编号")
    private String documentNumber;

    @ApiModelProperty(value = "送货单编号")
    private String deliveryNoteNumber;

    @ApiModelProperty(value = "申请单编号")
    private String rfqNumber;

    @Min(0)
    @Max(1)
    @NotNull
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @NotEmpty
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "计划部门")
    private String planUnit;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @ApiModelProperty(value = "单据编号（需求计划）")
    private String planNumber;

}
