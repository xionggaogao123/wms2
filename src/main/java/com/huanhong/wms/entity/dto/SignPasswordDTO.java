package com.huanhong.wms.entity.dto;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperBsEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;


@Data
@ApiModel(description = "签名密码参数")
public class SignPasswordDTO {

    @ApiModelProperty(value = "用户id",hidden = true)
    private Integer id;

    @ApiModelProperty(value = "原签名密码，设置密码时不传，修改密码时必传")
    private String oldPassword;

    @ApiModelProperty(value = "签名密码")
    private String signPassword;

    @ApiModelProperty(value = "确认签名密码，设置密码时必传，修改密码时不传")
    private String commitPassword;

}
