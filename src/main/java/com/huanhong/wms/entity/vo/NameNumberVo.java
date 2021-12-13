package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "名称数值Vo")
public class NameNumberVo {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "数值")
    private Integer value;

}
