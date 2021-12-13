package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "上传对象存储结果")
public class UploadOssVo {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "资源名称")
    private String fileName;

    @ApiModelProperty(value = "资源路径")
    private String fileUrl;

    @ApiModelProperty(value = "资源对象ID")
    private Integer objectId;

}
