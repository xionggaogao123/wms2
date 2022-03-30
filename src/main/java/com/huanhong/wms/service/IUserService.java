package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.*;

import java.util.List;

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

    Result<Integer> addUser(LoginUser loginUser, AddUserDTO dto);

    Result<Integer> updateUser(LoginUser loginUser, UpUserDTO dto);

    boolean getUserByDept(int deptId);

    //查询用户是否停用
    boolean isStopUsing(Integer userId);

    Result<Integer> setSignPassword(SignPasswordDTO dto);

    Result<Integer> setSignPic(SignPicDTO dto);

    Result<Object> delOpenIdById(Integer id);

    Result<User> selectByOpenid(String openid);

    Result<List<User>> list(Integer roleId, Integer deptId, String name);

}
