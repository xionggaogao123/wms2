package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "平衡利库记录")
public class BalanceLibraryRecord extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "平衡利库明细 id")
    private Integer balanceLibraryDetailId;

    @ApiModelProperty(value = "调出仓库")
    private String outWarehouse;
    @ApiModelProperty(value = "调出仓库编号")
    private String outWarehouseId;

    @ApiModelProperty(value = "第一次预调拨数量")
    private Double preCalibrationQuantity;

    @ApiModelProperty(value = "第一次准调数量")
    private Double calibrationQuantity;

    @ApiModelProperty(value = "第一次调拨计划单号")
    private String planNo;

    @ApiModelProperty(value = "第2次预调拨数量")
    private Double preCalibrationQuantity2;

    @ApiModelProperty(value = "第2次准调数量")
    private Double calibrationQuantity2;

    @ApiModelProperty(value = "第2次调拨计划单号")
    private String planNo2;

    @ApiModelProperty(value = "第3次预调拨数量")
    private Double preCalibrationQuantity3;

    @ApiModelProperty(value = "第3次准调数量")
    private Double calibrationQuantity3;

    @ApiModelProperty(value = "第3次调拨计划单号")
    private String planNo3;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Version
    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "物料id")
    private Integer materialId;

    @ApiModelProperty(value = "平衡利库单号")
    private String balanceLibraryNo;

    @ApiModelProperty(value = "第一次调拨状态 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer calibrationStatus;
    @ApiModelProperty(value = "第2次调拨状态 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer calibrationStatus2;
    @ApiModelProperty(value = "第3次调拨状态 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer calibrationStatus3;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;
}
