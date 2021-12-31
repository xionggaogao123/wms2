package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "新增货架")
public class AddShelfDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @NotEmpty
    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @NotEmpty
    @ApiModelProperty(value = "货架类型- 0-货架、1-地堆")
    private String shelfType;

    @NotEmpty
    @ApiModelProperty(value = "货架承重(地堆无限大)")
    private String shelfLoadBearing;

    @NotNull
    @ApiModelProperty(value = "底长(m：米)")
    private Double shelfBottomLength;

    @NotNull
    @ApiModelProperty(value = "底宽(m：米)")
    private Double shelfBottomWidth;

    @NotNull
    @ApiModelProperty(value = "高(m：米 地堆无限高)")
    private Double shelfHeight;

    @NotEmpty
    @ApiModelProperty(value = "货架层数-地堆即为一层")
    private String shelfLayer;

    @NotNull
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}
