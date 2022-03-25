package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "签名图片参数")
public class SignPicDTO {

    @ApiModelProperty(value = "用户id",hidden = true)
    private Integer id;

    @ApiModelProperty(value = "签名密码")
    private String signPassword;

    @ApiModelProperty(value = "签名图片")
    private String signURL;

}
