package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@ApiModel(description = "更新用户")
public class UpUserDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer id;

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "密码是8~16位的数字和英文字母组合")
    @Length(min = 8, max = 16, message = "密码是8~16位的数字和英文字母组合")
    @ApiModelProperty(value = "登录密码(密码是8~16位的数字和英文字母组合)")
    private String password;

    @Length(max = 24, min = 2, message = "姓名长度在2～24位之间")
    @ApiModelProperty(value = "姓名(姓名长度在2～24位之间)")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "性别 0.未知 1.男 2.女")
    private Integer gender;

    @ApiModelProperty(value = "邮箱号")
    private String mail;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "所属部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "生产队/组")
    private String productionTeam;

    @ApiModelProperty(value = "权限等级")
    private String permissionLevel;

    @Length(max = 256)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 0.禁用  1.启用")
    private Integer state;


    @ApiModelProperty(value = "角色 ids")
    private List<Integer> roleIds;
}
