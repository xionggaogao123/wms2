package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "临库点验明细新增")
public class AddTemporaryLibraryInventoryDetailsDTO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "清点单编号")
    private String documentNumber;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotEmpty
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Min(0)
    @ApiModelProperty(value = "应到数量")
    private Double receivableQuantity;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveTime;


    @Min(0)
    @ApiModelProperty(value = "到货数量")
    private Double arrivalQuantity;

    @NotNull
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotEmpty
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "金额")
    private Double money;
}
