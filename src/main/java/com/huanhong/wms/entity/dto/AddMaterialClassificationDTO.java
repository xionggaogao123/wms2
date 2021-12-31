package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@ApiModel(description = "新增物料分类")
public class AddMaterialClassificationDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "类型编码")
    private String typeCode;

    @NotNull
    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "父类编码-大类无父类编码，中、小类须填写")
    private String parentCode;

    @NotNull
    @ApiModelProperty(value = "0-大类、1-中类、2-小类")
    private Integer levelType;

    @NotNull
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}
