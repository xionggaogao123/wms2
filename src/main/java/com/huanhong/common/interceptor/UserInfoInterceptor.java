package com.huanhong.common.interceptor;

import com.huanhong.common.units.ThreadLocalUtil;
import com.huanhong.wms.bean.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {


    /**
     * 请求执行前执行的，将用户信息放入ThreadLocal
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginUser user;
        try {
            user = (LoginUser) request.getAttribute("loginUser");
        } catch (Exception e) {
            log.info("***************************用户未登录， ThreadLocal无信息***************************");
            return true;
        }
        if (null != user) {
            log.info("***************************用户已登录，用户信息放入ThreadLocal***************************");
            ThreadLocalUtil.addCurrentUser(user);
            return true;
        }
        log.info("***************************用户未登录， ThreadLocal无信息***************************");
        return true;
    }

    /**
     * 接口访问结束后，从ThreadLocal中删除用户信息
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("***************************接口调用结束， 从ThreadLocal删除用户信息***************************");
        ThreadLocalUtil.remove();
    }
}