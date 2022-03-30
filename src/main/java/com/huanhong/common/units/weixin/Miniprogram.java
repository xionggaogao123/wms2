package com.huanhong.common.units.weixin;

import lombok.Data;

@Data
public class Miniprogram {
    private String appid = WeixinConstant.MINI_APP_ID;
    private String path;

    public Miniprogram(String pagepath) {
        this.path = pagepath;
    }
}
