package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "临库出庫更新")
public class UpdateTemporaryOutWarehouseDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "审批状态:1.草拟 2.审批中 3.审批生效 4.作废")
    private Integer status;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @ApiModelProperty(value = "领用数量")
    private Double requisitionQuantity;

    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;

    @ApiModelProperty(value = "领用人")
    private String recipient;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "库管员")
    private String librarian;

    @ApiModelProperty(value = "备注")
    private String remark;


}
