package com.huanhong.wms.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.util.List;

@ApiModel(description = "系统角色参数")
@Data
public class SysRoleParam {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("数据范围类型（字典 1全部数据 2本部门及以下数据 3本部门数据 4仅本人数据 5自定义数据）")
    private Integer dataScopeType;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("授权菜单")
    private List<Integer> grantMenuIdList;

    @ApiModelProperty("授权数据")
    private List<Integer> grantOrgIdList;
}
