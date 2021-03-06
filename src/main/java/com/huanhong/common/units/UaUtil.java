package com.huanhong.common.units;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.huanhong.wms.bean.Constant;
import com.huanhong.wms.bean.enums.SymbolConstant;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户代理工具类
 *
 */
public class UaUtil {

    /**
     * 获取客户端浏览器
     *
     */
    public static String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        if (ObjectUtil.isEmpty(userAgent)) {
            return SymbolConstant.DASH;
        } else {
            String browser = userAgent.getBrowser().toString();
            return Constant.UNKNOWN.equals(browser) ? SymbolConstant.DASH : browser;
        }
    }

    /**
     * 获取客户端操作系统
     *
     */
    public static String getOs(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        if (ObjectUtil.isEmpty(userAgent)) {
            return SymbolConstant.DASH;
        } else {
            String os = userAgent.getOs().toString();
            return Constant.UNKNOWN.equals(os) ? SymbolConstant.DASH : os;
        }
    }

    /**
     * 获取请求代理头
     */
    private static UserAgent getUserAgent(HttpServletRequest request) {
        String userAgentStr = ServletUtil.getHeaderIgnoreCase(request, Constant.USER_AGENT);
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        //判空
        if (ObjectUtil.isNotEmpty(userAgentStr)) {
            //如果根本没获取到浏览器
            if (Constant.UNKNOWN.equals(userAgent.getBrowser().getName())) {
                //则将ua设置为浏览器
                userAgent.setBrowser(new Browser(userAgentStr, null, ""));
            }
        }
        return userAgent;
    }
}
