package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "添加仓库DTO")
public class AddWarehouseDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "所属公司ID")
    private Integer companyId;

    @NotEmpty
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @NotEmpty
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @NotEmpty
    @ApiModelProperty(value = "仓库面积")
    private String warehouseAcreage;

    @NotEmpty
    @ApiModelProperty(value = "仓库层数")
    private String warehouseLayers;

    @NotEmpty
    @ApiModelProperty(value = "仓库地址")
    private String warehouseAdress;

    @NotEmpty
    @ApiModelProperty(value = "仓库负责人")
    private String warehousePrincipal;

    @NotEmpty
    @ApiModelProperty(value = "仓库联系电话")
    private String warehouseContactNumber;

    @NotNull
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


    @ApiModelProperty(value = "备注")
    private String remark;

}
