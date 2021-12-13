package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="仓库管理")
public class WarehouseManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "所属公司ID")
    private String companyId;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房名称")
    private String warehouseName;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房面积")
    private String warehouseAcreage;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房层数")
    private String warehouseLayers;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房地址")
    private String warehouseAdress;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房负责人")
    private String warehousePrincipal;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房联系电话")
    private String warehouseContactNumber;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;


}
