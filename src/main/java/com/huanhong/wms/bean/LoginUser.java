package com.huanhong.wms.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ldy81
 * @date 2019/12/12 16:17
 */
@Data
public class LoginUser {

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String loginName;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "权限等级")
    private String permissionLevel;

    @ApiModelProperty(value = "父公司ID")
    private Integer parentCompanyId;

    @ApiModelProperty(value = "所属公司ID")
    private Integer companyId;

    @ApiModelProperty(value = "是否为管理员 1.是 0.否")
    private Integer isAdmin;

    @ApiModelProperty(value = "用户角色 ids")
    private List<String> loginUserRoleIds;


}
