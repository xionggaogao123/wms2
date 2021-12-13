package com.huanhong.wms;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 基础Controller
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @date 2017/12/18  11:39
 */
public class BaseController {

    @Resource
    protected HttpServletRequest request;

    public LoginUser getLoginUser() {
        return (LoginUser) request.getAttribute("loginUser");
    }

    public Integer getLoginUserId() {
        return getLoginUser().getId();
    }

    public String getLoginPermissionLevel() {
        return getLoginUser().getPermissionLevel();
    }

    protected Result render(boolean b) {
        return b ? Result.success() : Result.failure("操作失败");
    }

    /**
     * 返回重新登录
     *
     * @return
     */
    protected Result renderAgainLogin() {
        return Result.failure(ErrorCode.TOKEN_OVERDUE, "用户授权失效，请重新登陆。");
    }


    public void eq(String key, Object value, QueryWrapper<?> query) {
        if (ObjectUtil.isNotEmpty(value)) {
            query.eq(key, value);
        }
    }

    public void eq(String key, Map<String, Object> search, QueryWrapper<?> query) {
        if (search.containsKey(key)) {
            final Object value = search.get(key);
            if (ObjectUtil.isNotEmpty(value)) {
                query.eq(StrUtil.toUnderlineCase(key), value);
            }
        }
    }

    public void in(String key, Object value, QueryWrapper<?> query) {
        if (ObjectUtil.isNotEmpty(value)) {
            query.in(key, StrUtil.split(value.toString(), StrUtil.C_COMMA));
        }
    }

    public void in(String key, Map<String, Object> search, QueryWrapper<?> query) {
        if (search.containsKey(key)) {
            final Object value = search.get(key);
            if (ObjectUtil.isNotEmpty(value)) {
                query.in(StrUtil.toUnderlineCase(key), StrUtil.split(value.toString(), StrUtil.C_COMMA));
            }
        }
    }

    public void like(String key, Object value, QueryWrapper<?> query) {
        if (ObjectUtil.isNotEmpty(value)) {
            query.like(key, value);
        }
    }

    public void like(String key, Map<String, Object> search, QueryWrapper<?> query) {
        if (search.containsKey(key)) {
            final Object value = search.get(key);
            if (ObjectUtil.isNotEmpty(value)) {
                query.like(StrUtil.toUnderlineCase(key), value);
            }
        }
    }

    public void between(String value, Map<String, Object> condition, QueryWrapper<?> query) {
        if (condition.containsKey("gmtStart") && condition.containsKey("gmtEnd")) {
            query.between(value, condition.get("gmtStart"), condition.get("gmtEnd"));
        }
    }

}
