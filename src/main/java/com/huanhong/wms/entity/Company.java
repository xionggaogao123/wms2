package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "门店表")
public class Company extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Logo")
    private String logo;

    @ApiModelProperty(value = "公司类型")
    private Integer type;

    @ApiModelProperty(value = "公司名")
    private String name;

    @ApiModelProperty(value = "公司全名")
    private String fullName;

    @ApiModelProperty(value = "初始账号")
    private String account;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "父ID")
    private Integer parentId;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "员工人数")
    private Integer userCount;

    @ApiModelProperty(value = "0.系统默认 1.自定义")
    private Boolean customize;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 0.禁用 1.启用")
    private Integer state;


}
