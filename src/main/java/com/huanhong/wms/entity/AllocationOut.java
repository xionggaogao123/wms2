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
@ApiModel(description="调拨出库主表")
public class AllocationOut extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "调拨出库单据编号")
    private String allocationOutNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "调拨计划单据编号")
    private String allocationNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "出库状态：0-未出库，1-部分出库，2-全部出库")
    private Integer outStatus;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库管员")
    private String librarian;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "检验人")
    private String verification;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "last_update",fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "调出单位")
    private String sendCompany;

}
