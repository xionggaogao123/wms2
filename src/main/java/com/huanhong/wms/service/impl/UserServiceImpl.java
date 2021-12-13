package com.huanhong.wms.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.LoginDTO;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-13
 */
@Slf4j
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DeptMapper deptMapper;

    @Override
    public Result<User> checkLogin(LoginDTO login) {
        // 域账号登录失败，进行普通账号验证
        User user = this.baseMapper.getUserByAccount(login.getAccount());
        if (user == null) {
            return Result.failure(1024, "登录失败，请确认账号是否正确");
        }
        if (login.getType().equals("account")) {
            if (!user.getPassword().equals(SecureUtil.md5(login.getPassword()))) {
                return Result.failure(1025, "密码错误");
            }
        } else if (login.getType().equals("sms")) {
            String key = StrUtil.format("code_{}_{}", 2, login.getAccount());
            Object code = redisTemplate.opsForValue().get(key);
            if (code == null) {
                return Result.failure(1023, "短信验证码已过期,请重新发送");
            }
            if (!code.toString().equals(login.getPassword())) {
                return Result.failure(1025, "短信验证码错误");
            }
            redisTemplate.delete(key);
        } else {
            return Result.failure("非法登陆");
        }
//        if (!user.getState().equals(1)) {
//            return Result.failure(2003, "账号已禁用");
//        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public User getUserInfo(Integer userId) {
        User user = this.baseMapper.selectById(userId);
        if (user != null) {
            List<Map<String, Object>> depts = new ArrayList<>();
//            this.getDeptUp(depts, user.getDeptId());
            Collections.reverse(depts);
            user.setDepts(depts);
        }
        return user;
    }

    private void getDeptUp(List<Map<String, Object>> depts, String deptId) {
        Map<String, Object> dept = deptMapper.getDeptById(deptId);
        if (ObjectUtil.isNotEmpty(dept)) {
            depts.add(dept);
            if (Convert.toInt(dept.get("parentId")) != 0) {
                this.getDeptUp(depts, dept.get("parentId").toString());
            }
        }
    }

}
