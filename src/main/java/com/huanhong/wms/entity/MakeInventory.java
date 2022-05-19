package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="清点单Entity")
public class MakeInventory extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "物料类型: 0-全部物料、1-指定物料、2-随机物料")
    private Integer materialType;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存 3-全部 ")
    private Integer inventoryType;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货主：0-泰丰盛和  1-矿上自有 2-全部")
    private Integer consignor;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer planStatus;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "盘点人")
    private String checkerIds;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "稽核人")
    private String auditId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "是否全盘: 0-非全盘 1-全盘")
    private Integer allMake;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "状态: 0-待盘点,1-已盘点")
    private Integer checkStatus;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "申请日期")
    private LocalDateTime createTime;

    @TableField(value = "last_update",fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;

}
