package com.huanhong.wms.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="")
public class TemporaryEnterWarehouse extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "临时库入库单据编号")
    private String documentNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "状态:1.草拟 2.审批中 3.审批生效 4.作废")
    private Integer state;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "实收数量")
    private Double actualQuantity;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "批次")
    private String batch;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "入库时间")
    private LocalDateTime enterTime;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createTime;

    @TableField(value = "last_update", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;


}
