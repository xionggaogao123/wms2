package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@ApiModel(value = "仓库更新对象", description = "仓库更新对象封装")
@Data
public class UpdateWarehouseDTO {

    @NotEmpty
    @ApiModelProperty(value = "仓库编号")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehouseId;


    @ApiModelProperty(value = "仓库名称")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehouseName;


    @ApiModelProperty(value = "仓库面积")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehouseAcreage;


    @ApiModelProperty(value = "仓库层数")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehouseLayers;


    @ApiModelProperty(value = "仓库地址")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehouseAdress;


    @ApiModelProperty(value = "仓库负责人")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehousePrincipal;


    @ApiModelProperty(value = "仓库联系电话")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String warehouseContactNumber;



    @ApiModelProperty(value = "备注")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String remark;
}
