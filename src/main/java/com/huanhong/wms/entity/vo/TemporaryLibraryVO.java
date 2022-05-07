package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "临库查询对象", description = "临库查询对象封装")
public class TemporaryLibraryVO {

    @ApiModelProperty(value = "临库出入库单编号")
    private String documentNumber;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;
    
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

}
