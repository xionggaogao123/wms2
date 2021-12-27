package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="库房区域管理")
public class WarehouseAreaManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;


    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;


    @ApiModelProperty(value = "库区名称")
    private String warehouseAreaName;


    @ApiModelProperty(value = "长(m：米)")
    private Double warehouseAreaLength;


    @ApiModelProperty(value = "宽(m：米)")
    private Double warehouseAreaWidth;


    @ApiModelProperty(value = "高(m：米)")
    private Double warehouseAreaHeight;


    @ApiModelProperty(value = "库区负责人")
    private String warehouseAreaPrincipal;


    @ApiModelProperty(value = "库区联系电话")
    private String warehouseAreaContactNumber;


    @ApiModelProperty(value = "备注")
    private String remark;


}
