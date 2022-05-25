package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="平衡利库记录")
public class BalanceLibraryRecord extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "平衡利库明细 id")
    private Integer balanceLibraryDetailId;

    @ApiModelProperty(value = "平衡类别 1.调拨 2.采购")
    private Integer balanceType;

    @ApiModelProperty(value = "调出仓库")
    private String outWarehouse;
    @ApiModelProperty(value = "调出仓库编号")
    private String outWarehouseId;

    @ApiModelProperty(value = "准调数量")
    private Double calibrationQuantity;

    @ApiModelProperty(value = "采购或调拨计划单号")
    private String planNo;

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

    @ApiModelProperty(value = "批准数量")
    private Double approvedQuantity;

    @ApiModelProperty(value = "平衡利库单号")
    private String balanceLibraryNo;


}
