package com.huanhong.common.units;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * TODO Msg工具类
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @date 2018/1/26 11:04
 */
public class MsgUtil {

    /**
     * 全局消息配置文件
     */
    public static Props msg = new Props("msg.properties");

    /**
     * TODO 获取消息
     *
     * @param key    键
     * @return String
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2017/4/6 19:20
     */
    public static String getStr(String key) {
        return msg.getStr(key);
    }

    /**
     * TODO 获取需要拼接的消息
     *
     * @param key    键
     * @param params 参数
     * @return String
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2017/4/6 19:20
     */
    public static String getStr(String key, Object... params) {
        return StrUtil.format(msg.getStr(key), params);
    }

}