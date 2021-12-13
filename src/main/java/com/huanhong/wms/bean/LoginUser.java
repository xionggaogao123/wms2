package com.huanhong.wms.bean;

import lombok.Data;

/**
 * @author ldy81
 * @date 2019/12/12 16:17
 */
@Data
public class LoginUser {

    /**
     * 用户ID
     */
    private Integer id;
    /**
     * 账号
     */
    private String loginName;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 权限等级
     */
    private String permissionLevel;

}
