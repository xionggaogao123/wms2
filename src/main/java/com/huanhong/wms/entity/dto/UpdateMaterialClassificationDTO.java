package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "更新物料分类")
public class UpdateMaterialClassificationDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "类型编码")
    private String typeCode;

    @Length(max = 20, min = 1, message = "类型名称长度在1～20位之间")
    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
