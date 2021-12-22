package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(description = "添加公司DTO")
public class AddCompanyDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司类型")
    private Integer type;

    @NotEmpty
    @ApiModelProperty(value = "公司名", required = true)
    private String name;

    @NotEmpty
    @ApiModelProperty(value = "公司全名", required = true)
    private String fullName;

    @NotEmpty
    @ApiModelProperty(value = "初始账号", required = true)
    private String account;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "备注")
    private String remark;

}
