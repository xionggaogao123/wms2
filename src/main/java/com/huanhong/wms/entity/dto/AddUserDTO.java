package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "新增用户DTO")
public class AddUserDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Length(max = 14, min = 5, message = "账号长度在5～14位之间")
    @ApiModelProperty(value = "登录账号", required = true)
    private String loginName;

    @Length(max = 16, min = 8, message = "密码长度在8～16位之间")
    @ApiModelProperty(value = "登录密码")
    private String password;

    @NotNull
    @Length(max = 24, min = 2, message = "姓名长度在2～24位之间")
    @ApiModelProperty(value = "姓名", required = true)
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @NotNull
    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "性别 0.未知 1.男 2.女")
    private Integer gender;

    @ApiModelProperty(value = "邮箱号")
    private String mail;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @NotNull
    @ApiModelProperty(value = "所属部门ID", required = true)
    private Integer deptId;

    @ApiModelProperty(value = "生产队/组")
    private String productionTeam;

    @ApiModelProperty(value = "权限等级")
    private String permissionLevel;

    @Length(max = 256)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "角色 ids")
    private List<Integer> roleIds;

}
