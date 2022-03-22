package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="系统用户角色表")
public class SysUserRole extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "角色id")
    private Integer roleId;


}
