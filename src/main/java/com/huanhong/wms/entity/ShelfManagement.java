package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="货架管理")
public class ShelfManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @ApiModelProperty(value = "货架类型- 0-货架、1-地堆")
    private String shelfType;

    @ApiModelProperty(value = "货架承重(地堆无限大)")
    private String shelfLoadBearing;

    @ApiModelProperty(value = "底长(m：米)")
    private Double shelfBottomLength;

    @ApiModelProperty(value = "底宽(m：米)")
    private Double shelfBottomWidth;

    @ApiModelProperty(value = "高(m：米 地堆无限高)")
    private Double shelfHeight;

    @ApiModelProperty(value = "货架层数-地堆即为一层")
    private String shelfLayer;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "备注")
    private String remark;


}
