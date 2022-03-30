package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(description = "登录对象")
public class LoginDTO implements Serializable {

    @NotNull
    @ApiModelProperty(value = "账号", required = true)
    private String account;

    @NotNull
    @ApiModelProperty(value = "密码", required = true, notes = "登录方式为sms时该参数为验证码")
    private String password;

    @NotNull
    @ApiModelProperty(value = "登录方式", required = true, notes = "account 账号密码 sms 短信验证码")
    private String type;

    @NotNull
    @ApiModelProperty(value = "登录平台", required = true, notes = "PC iOS Android miniProgram")
    private String terminal;

    @NotNull
    @ApiModelProperty(value = "App版本", required = true)
    private String version;

    /**
     * IP地址
     */
    private String ip;

    private String openid;
    private String js_code;


    public void setAccount(String account) {
        this.account = account.trim();
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }
}
