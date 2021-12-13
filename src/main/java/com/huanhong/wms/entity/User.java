package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "用户")
public class User extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录账号")
    private String loginName;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private Integer phoneNumber;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "邮箱号")
    private String mail;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "工作部门")
    private String department;

    @ApiModelProperty(value = "生产队/组")
    private String productionTeam;

    @ApiModelProperty(value = "权限等级")
    private String permissionLevel;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "部门列表")
    @TableField(exist = false)
    private List<Map<String, Object>> depts;

}
