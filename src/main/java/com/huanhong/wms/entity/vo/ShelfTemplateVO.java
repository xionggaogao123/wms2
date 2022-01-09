package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ShelfTemplateVO查询对象", description = "货架模板查询对象封装")
@Data
public class ShelfTemplateVO {

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "货架模板名称")
    private String shelfTemplateName;

    @ApiModelProperty(value = "货架模板类型- 0-货架、1-地堆")
    private Integer shelfType;

}
