package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="")
public class TemporaryRecord extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "需求计划单据编号")
    private String requirementsPlanningNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "记录类型：1-临时库入库 2-临时库出库")
    private Integer recordType;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "批次")
    private String batch;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "入库数量")
    private Double enterQuantity;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "出库数量")
    private Double outQuantity;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库管员")
    private String warehouseManager;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "领用人")
    private String recipient;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createTime;

    @TableField(value = "last_update", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;

}
