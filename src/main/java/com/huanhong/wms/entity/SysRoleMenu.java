package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="系统角色菜单表")
public class SysRoleMenu extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "角色id")
    private Integer roleId;

    @ApiModelProperty(value = "菜单id")
    private Integer menuId;


}
