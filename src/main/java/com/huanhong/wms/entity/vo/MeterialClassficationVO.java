package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MeterialClassficationVO查询对象", description = "物料分类对象封装")
public class MeterialClassficationVO {

    @ApiModelProperty(value = "0-大类、1-中类、2-小类")
    private Integer levelType;

    @ApiModelProperty(value = "类型编码")
    private String typeCode;

    @ApiModelProperty(value = "父类编码-大类无父类编码，中、小类须填写")
    private String parentCode;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

}
