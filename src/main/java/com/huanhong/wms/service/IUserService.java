package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.LoginDTO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-13
 */
public interface IUserService extends SuperService<User> {

    /**
     * 验证登录
     *
     * @param login 登录信息
     * @return Result<Map < String, Object>>
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/7/23 18:17
     */
    Result<User> checkLogin(LoginDTO login);

    User getUserInfo(Integer userId);

}
