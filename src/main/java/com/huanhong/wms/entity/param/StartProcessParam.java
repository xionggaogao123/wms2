package com.huanhong.wms.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel(description = "流程启动参数")
@Data
public class StartProcessParam {

    @ApiModelProperty(value = "流程定义 key")
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程定义参数")
    private Map<String, Object> variables;

    @ApiModelProperty(value = "主表 id")
    private Integer id;


}
