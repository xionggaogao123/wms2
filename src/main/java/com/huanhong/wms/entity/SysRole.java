package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="系统角色表")
public class SysRole extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "序号")
    private Integer sort;

    @ApiModelProperty(value = "数据范围类型（字典 1全部数据 2本部门及以下数据 3本部门数据 4仅本人数据 5自定义数据）")
    private Integer dataScopeType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态（字典 0正常 1停用 2删除）")
    private Integer status;


}
