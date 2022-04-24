package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "库存查询对象", description = "库存查询对象封装")
public class InventoryInformationVO {

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房名称")
    private String warehouseName;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "是否检验 0-未检验 1-已检验")
    private Integer isVerification;

    @ApiModelProperty(value = "是否入库 0-未入库 1-已入库")
    private Integer isEnter;

    @ApiModelProperty(value = "是否上架 0-未上架 1-已上架")
    private Integer isOnshelf;

}
