package com.huanhong.common.units;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONPath;
import com.huanhong.wms.bean.enums.SymbolConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 根据ip地址定位工具类，使用百度定位api
 * https://lbsyun.baidu.com/index.php?title=webapi/ip-api
 */
public class IpAddressUtil {

    private static final Log log = Log.get();

    private static final String LOCAL_IP = "127.0.0.1";

    private static final String LOCAL_REMOTE_HOST = "0:0:0:0:0:0:0:1";

    /**
     * 获取客户端ip
     */
    public static String getIp(HttpServletRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            return LOCAL_IP;
        } else {
            String remoteHost = ServletUtil.getClientIP(request);
            return LOCAL_REMOTE_HOST.equals(remoteHost) ? LOCAL_IP : remoteHost;
        }
    }

    /**
     * 根据ip地址定位
     *
     */
    @SuppressWarnings("unchecked")
    public static String getAddress(HttpServletRequest request) {
        String resultJson = SymbolConstant.DASH;

        String ip = getIp(request);

        //如果是本地ip或局域网ip，则直接不查询
        if (ObjectUtil.isEmpty(ip) || NetUtil.isInnerIP(ip)) {
            return resultJson;
        }

        try {
            //定位api接口
            String api = "https://api.map.baidu.com/location/ip?ak={}&ip={}&coor=bd09ll";
            //获取百度定位ak
            String ak = "";
            if (ObjectUtil.isAllNotEmpty(api, ak)) {
                String path = "$['content']['address_detail']['province','city']";
                HttpRequest http = HttpUtil.createGet(String.format(api, ak, ip));
                resultJson = http.timeout(3000).execute().body();
                resultJson = String.join("", (List<String>) JSONPath.read(resultJson, path));
            }
        } catch (Exception e) {
            resultJson = SymbolConstant.DASH;
            //注释掉此log，以免频繁打印，可自行开启
            //log.error(">>> 根据ip定位异常，请求号为：{}，具体信息为：{}", RequestNoUtil.get(), e.getMessage());
        }
        return resultJson;
    }

}
