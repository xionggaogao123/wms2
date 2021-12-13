package com.huanhong.common.interceptor;

import cn.hutool.core.lang.id.NanoId;
import com.huanhong.wms.bean.Constant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求处理程序拦截器
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @version v1.0
 * @since 2018/1/18 12:38
 */
@Slf4j
@Configuration
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        // 初次请求运行,如果异常这里还会走一次
        if (response.getStatus() == Constant.HTTP_SUCCEED) {
            String nanoId = NanoId.randomNanoId(12);
            MDC.put(Constant.REQUEST_ID, nanoId);
            response.addHeader("request-id", nanoId);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) {
        MDC.clear();
    }

}