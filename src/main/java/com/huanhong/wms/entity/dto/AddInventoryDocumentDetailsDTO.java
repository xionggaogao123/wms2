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
@ApiModel(description = "新增点验单明细DTO")
public class AddInventoryDocumentDetailsDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "清点单编号")
    private String documentNumber;
    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotNull
    @ApiModelProperty(value = "应到数量")
    private Double receivableQuantity;

    @ApiModelProperty(value = "到货数量")
    private Double arrivalQuantity;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotBlank
    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @ApiModelProperty(value = "备注")
    private String remark;
}
