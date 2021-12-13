package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@ApiModel(description = "对象存储")
public class OssVo {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "资源名称")
    private String name;

    @ApiModelProperty(value = "资源大小")
    private Long size;

    @ApiModelProperty(value = "MD5校验值")
    private String md5;

    @ApiModelProperty(value = "资源文件类型")
    private String type;

    @ApiModelProperty(value = "资源路径")
    private String url;

    @ApiModelProperty(value = "图片状态 0.审核失败 1.待审核 2.审核成功 3.已失效")
    private Integer state;

    @ApiModelProperty(value = "上传时间")
    private LocalDateTime gmtCreate;
}
