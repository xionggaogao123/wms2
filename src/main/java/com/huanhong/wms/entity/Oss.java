package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "对象存储表")
public class Oss extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源名称")
    private String name;

    @ApiModelProperty(value = "资源大小")
    private Long size;

    @ApiModelProperty(value = "MD5校验值")
    private String md5;

    @ApiModelProperty(value = "资源文件类型")
    private String type;

    @ApiModelProperty(value = "资源所属对象ID")
    private Integer objectId;

    @ApiModelProperty(value = "资源所属对象类型")
    private String objectType;

    @ApiModelProperty(value = "资源路径")
    private String url;

    @ApiModelProperty(value = "顺序")
    private Integer sort;

    @ApiModelProperty(value = "图片状态 0.审核失败 1.待审核 2.审核成功 3.已失效")
    private Integer state;

    @ApiModelProperty(value = "上传用户ID")
    private Integer userId;


}
