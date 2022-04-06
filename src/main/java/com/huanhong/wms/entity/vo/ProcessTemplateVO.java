package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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

    @ApiModelProperty(value = "模版类型 1.审批人 2.抄送人")
    private Integer templateType;


}
