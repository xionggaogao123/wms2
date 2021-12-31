package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="子库管理")
public class SublibraryManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "子库名称")
    private String sublibraryName;

    @ApiModelProperty(value = "子库面积")
    private String sublibraryAcreage;

    @ApiModelProperty(value = "子库所在层")
    private String sublibraryFloor;

    @ApiModelProperty(value = "子库负责人")
    private String sublibraryPrincipal;

    @ApiModelProperty(value = "长(m：米)")
    private Double sublibraryLength;

    @ApiModelProperty(value = "宽(m：米)")
    private Double sublibraryWidth;


    @ApiModelProperty(value = "高(m：米)")
    private Double sublibraryHeight;

    @ApiModelProperty(value = "子库联系电话")
    private String sublibraryContactNumber;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "备注")
    private String remark;

}
