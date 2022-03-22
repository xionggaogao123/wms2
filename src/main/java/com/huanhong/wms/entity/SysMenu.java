package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="系统菜单表")
public class SysMenu extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "父id")
    private Integer pid;

    @ApiModelProperty(value = "父ids")
    private String pids;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "菜单类型（字典 0目录 1菜单 2按钮）")
    private Integer type;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "路由地址")
    private String router;

    @ApiModelProperty(value = "组件地址")
    private String component;

    @ApiModelProperty(value = "权限标识")
    private String permission;

    @ApiModelProperty(value = "应用分类（应用编码）")
    private String application;

    @ApiModelProperty(value = "打开方式（字典 0无 1组件 2内链 3外链）")
    private Integer openType;

    @ApiModelProperty(value = "是否可见（Y-是，N-否）")
    private String visible;

    @ApiModelProperty(value = "链接地址")
    private String link;

    @ApiModelProperty(value = "重定向地址")
    private String redirect;

    @ApiModelProperty(value = "权重（字典 1系统权重 2业务权重）")
    private Integer weight;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态（字典 0正常 1停用 2删除）")
    private Integer status;


}
