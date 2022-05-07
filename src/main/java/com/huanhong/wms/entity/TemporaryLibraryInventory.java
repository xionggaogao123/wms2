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
public class TemporaryLibraryInventory extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "清点单编号")
    private String documentNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "送货单编号")
    private String deliveryNoteNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "申请单编号")
    private String rfqNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

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
