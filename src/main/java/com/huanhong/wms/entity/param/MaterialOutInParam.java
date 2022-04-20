package com.huanhong.wms.entity.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(description="物料出入库参数")
public class MaterialOutInParam {

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "统计类型 1、天 2、月")
    private Integer type;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date endTime;
}
