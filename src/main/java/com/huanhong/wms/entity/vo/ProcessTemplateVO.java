package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "流程预设对象", description = "流程预设")
public class ProcessTemplateVO {


    @ApiModelProperty(value = "流程代码")
    private String processCode;


    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

}
