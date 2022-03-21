
package com.huanhong.wms.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description="系统菜单参数")
@Data
public class SysMenuParam {
    
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("父id")
    private Long pid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("菜单类型（字典 0目录 1菜单 2按钮）")
    private Integer type;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("路由地址")
    private String router;

    @ApiModelProperty("组件地址")
    private String component;

    @ApiModelProperty("权限标识")
    private String permission;

    @ApiModelProperty("应用分类（应用编码）")
    private String application;

    @ApiModelProperty("打开方式（字典 0无 1组件 2内链 3外链）")
    private Integer openType;

    @ApiModelProperty("是否可见（Y-是，N-否）")
    private String visible;

    @ApiModelProperty("内链地址")
    private String link;

    @ApiModelProperty("重定向地址")
    private String redirect;

    @ApiModelProperty("权重（字典 1系统权重 2业务权重）")
    private Integer weight;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("备注")
    private String remark;
}
