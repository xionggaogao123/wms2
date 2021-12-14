package com.huanhong.common.units;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * Toke工具类
 *
 * @author 赵雷颂 zhls1992@qq.com
 * @date 2018/4/10 16:09
 */
public class TokenUtil {

    private static final String TOKEN_SECRET = "o1Jgi8ZPCcDNAP7b9hNs1qH5esFbTreW";

    /**
     * 创建token
     *
     * @param user          用户类型
     * @param timeOutMinute 超时时间(分钟)
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/1/25 19:52
     */
    public static String createJWT(User user, int timeOutMinute) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder().setId(user.getId().toString())
                .setSubject(user.getLoginName())
                .setIssuer(user.getUserName())
                .claim("pl", user.getPermissionLevel())
                .claim("c", user.getCompanyId())
                .claim("pc", user.getParentCompanyId())
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                .setIssuedAt(now);
        if (timeOutMinute > 0) {
            builder.setExpiration(DateUtil.offsetMinute(now, timeOutMinute));
        }
        return builder.compact();
    }

    /**
     * 解析token
     *
     * @param jwt
     * @return
     */
    public static LoginUser parseJWT(String jwt) {
        Claims body = Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(jwt).getBody();
        LoginUser user = new LoginUser();
        user.setId(Convert.toInt(body.getId()));
        user.setLoginName(body.getSubject());
        user.setUserName(body.getIssuer());
        user.setPermissionLevel(body.get("pl", String.class));
        user.setCompanyId(body.get("c", Integer.class));
        user.setParentCompanyId(body.get("pc", Integer.class));
        return user;
    }

    /**
     * 刷新token
     *
     * @param jwt           原token
     * @param timeOutMinute 超时时间(分钟)
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/1/25 19:52
     */
    public static String refreshJWT(String jwt, int timeOutMinute) {
        Date now = new Date();
        Claims body = Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(jwt).getBody();
        JwtBuilder builder = Jwts.builder().setId(body.getId())
                .setSubject(body.getSubject())
                .setIssuer(body.getIssuer())
                .claim("pl", body.get("pl"))
                .claim("c", body.get("c"))
                .claim("pc", body.get("pc"))
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                .setIssuedAt(now);
        if (timeOutMinute > 0) {
            builder.setExpiration(DateUtil.offsetMinute(now, timeOutMinute));
        }
        return builder.compact();
    }

    public static String refreshJWT(User user, String jwt) {
        Date now = new Date();
        Claims body = Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(jwt).getBody();
        JwtBuilder builder = Jwts.builder().setId(user.getId().toString())
                .setSubject(user.getLoginName())
                .setIssuer(user.getUserName())
                .claim("pl", user.getPermissionLevel())
                .claim("c", user.getCompanyId())
                .claim("pc", user.getParentCompanyId())
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                .setIssuedAt(now);
        builder.setExpiration(body.getExpiration());
        return builder.compact();
    }

    public static void main(String[] args) {
        System.out.println(parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0Iiwic3ViIjoiYWRtaW4iLCJpc3MiOiLnrqHnkIblkZgiLCJwbCI6IjAiLCJjIjoxLCJwYyI6MCwiaWF0IjoxNjM5NDY1ODY4LCJleHAiOjE2NDAwNzA2Njh9.PZxzt3Dc5DsYEcbYvV1jUnTY9-J0aDIf9JCwfMsM2lE"));
    }

}
