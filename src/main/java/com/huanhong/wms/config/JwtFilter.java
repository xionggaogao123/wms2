package com.huanhong.wms.config;

import cn.hutool.core.util.StrUtil;
import com.huanhong.common.units.TokenUtil;
import com.huanhong.wms.bean.Constant;
import com.huanhong.wms.bean.LoginUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求认证过滤器
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @version v1.0
 * @since 2018/1/25 20:32
 */
@WebFilter(urlPatterns = "/v1/*")
public class JwtFilter implements Filter {

    private static final String[] EXCLUDED_PAGES = new String[]{
            "/login",
            "/code",
            "/webhook",
    };

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // 客户端将token封装在请求头中，格式为（Bearer后加空格）：Authorization：Bearer + Token
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = request.getParameter("access_token");
        if ((StrUtil.isBlank(authHeader) && StrUtil.isBlank(accessToken)) || Constant.GUEST.equals(authHeader)) {
            // 判断是否需要过滤
            if (isExcludedPage(request)) {
                chain.doFilter(req, res);
                return;
            }
            renderResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN IS NULL");
            return;
        }

        if (StrUtil.isNotBlank(authHeader)) {
            if (authHeader.startsWith(Constant.BASIC)) {
                //判断是否需要过滤
                if (isExcludedPage(request)) {
                    chain.doFilter(req, res);
                    return;
                }
                // 若要处理BASIC认证请在下面处理
            }
            if (authHeader.startsWith(Constant.BEARER)) {
                accessToken = authHeader.substring(7);
            } else {
                renderResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN INVALID");
                return;
            }
        }
        LoginUser user;
        try {
            //去除Bearer 后部分
            //解密token，拿到里面的对象claims
            user = TokenUtil.parseJWT(accessToken);
        } catch (SignatureException e) {
            renderResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN INVALID");
            return;
        } catch (ExpiredJwtException e) {
            renderResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN OVERDUE");
            return;
        } catch (Exception e) {
            renderResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN ERROR");
            return;
        }

        //将对象传递给下一个请求
        request.setAttribute("loginUser", user);
        chain.doFilter(req, res);
    }

    /**
     * 渲染响应
     *
     * @param response
     * @param status
     * @param errorCode
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-03-19
     */
    public void renderResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.setStatus(status);
        response.getWriter().write(errorMessage);
    }

    /**
     * 判断是否需要过滤
     *
     * @param request
     * @return boolean
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-03-18
     */
    public static boolean isExcludedPage(HttpServletRequest request) {
        return containsPage(request.getServletPath(), EXCLUDED_PAGES);
    }

    public static boolean containsPage(String url, String[] pages) {
        boolean isExcludedPage = false;
        for (String page : pages) {
            if (url.contains(page)) {
                isExcludedPage = true;
                break;
            }
        }
        return isExcludedPage;
    }

    @Override
    public void destroy() {
    }
}
