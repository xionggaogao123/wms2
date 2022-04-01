package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.wms.SuperEntity;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Company;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.mapper.CompanyMapper;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IDeptService;
import com.huanhong.wms.service.ISysRoleService;
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
    private IDeptService deptService;

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ISysRoleService sysRoleService;

    @Override
    public Result<User> checkLogin(LoginDTO login) {
        // 域账号登录失败，进行普通账号验证
        User user = this.baseMapper.getUserByAccount(login.getAccount());
        if (user == null) {
            return Result.failure(1024, "登录失败，请确认账号是否正确");
        }
        if (!user.getState().equals(1)) {
            return Result.failure(2003, "账号已禁用");
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
        Company company = companyMapper.selectById(user.getCompanyId());
        if (!company.getState().equals(1)) {
            return Result.failure("公司账号已被禁用");
        }

        // 角色信息
        List<Dict> roles = sysRoleService.getLoginRoles(user.getId());
        user.setRoles(roles);

        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public User getUserInfo(Integer userId) {
        User user = this.baseMapper.selectById(userId);
        if (user != null) {
            List<Map<String, Object>> depts = new ArrayList<>();
            this.getDeptUp(depts, user.getDeptId());
            Collections.reverse(depts);
            user.setDepts(depts);
        }
        return user;
    }

    @Override
    public Result<Integer> addUser(LoginUser loginUser, AddUserDTO dto) {
        User check = this.baseMapper.getUserByAccount(dto.getLoginName());
        if (check != null) {
            return Result.failure("该账号已存在");
        }
        User addUser = new User();
        BeanUtil.copyProperties(dto, addUser);

        Dept dept = deptMapper.selectById(addUser.getDeptId());
        if (dept == null) {
            return Result.failure("部门信息不存在或已删除");
        }
        addUser.setDeptName(dept.getName());
        addUser.setCompanyId(dept.getCompanyId());
        addUser.setParentCompanyId(dept.getParentCompanyId());

        Company company = companyMapper.selectById(addUser.getCompanyId());
        if (company == null) {
            return Result.failure("公司信息不存在或已删除");
        }
        addUser.setCompanyName(company.getName());

        if (StrUtil.isNotEmpty(addUser.getPassword())) {
            addUser.setPassword(SecureUtil.md5(addUser.getPassword()));

        }
        int insert = 0;
        try {
            insert = this.baseMapper.insert(addUser);
        } catch (Exception e) {
            log.error("用户信息添加异常: {}", addUser);
        }
        if (insert > 0) {
            // 更新部门人数
            deptService.updateDeptUserCount();
        }
        return insert > 0 ? Result.success(addUser.getId()) : Result.failure("新增失败");
    }

    @Override
    public Result<Integer> updateUser(LoginUser loginUser, UpUserDTO dto) {
        if (dto.getId() == null) {
            dto.setId(loginUser.getId());
        }
        // 查看用户是否停用
        if (dto.getState() == null && isStopUsing(dto.getId())) {
            return Result.failure("用户禁用中,不能更新数据");
        }
        User updateUser = new User();
        BeanUtil.copyProperties(dto, updateUser);
        if (StrUtil.isNotEmpty(dto.getPassword())) {
            updateUser.setPassword(SecureUtil.md5(dto.getPassword()));
        }

        if (updateUser.getDeptId() != null) {
            Dept dept = deptMapper.selectById(updateUser.getDeptId());
            if (dept == null) {
                return Result.failure("部门信息不存在或已删除");
            }
            updateUser.setDeptName(dept.getName());
        }
        int update = 0;
        try {
            update = this.baseMapper.updateById(updateUser);
        } catch (Exception e) {
            log.error("用户信息更新异常: {}", updateUser);
        }
        return update > 0 ? Result.success(updateUser.getId()) : Result.failure("更新失败");
    }

    @Override
    public boolean getUserByDept(int deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id", deptId);
        int count = userMapper.selectCount(queryWrapper);
        return count > 0;
    }

    /**
     * 查询用户 是否停用
     *
     * @param userId
     * @return boolean
     */
    @Override
    public boolean isStopUsing(Integer userId) {
        int count = this.baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(SuperEntity::getId, userId).eq(User::getState, 0));
        return count > 0;
    }

    @Override
    public Result<Integer> setSignPassword(SignPasswordDTO dto) {
        User user = userMapper.selectById(dto.getId());
        User upUser = new User();
        upUser.setId(dto.getId());
        upUser.setSignPassword(dto.getSignPassword());
        int update = 0;
        //添加签名密码
        if (StrUtil.isNotBlank(dto.getCommitPassword())) {
            if (StrUtil.isBlank(user.getSignPassword())) {
                if (!dto.getSignPassword().equals(dto.getCommitPassword())) {
                    return Result.failure(ErrorCode.PARAM_ERROR, "两次输入密码不一致");
                }
                update = userMapper.updateById(upUser);
                if (update > 0) {
                    return Result.success();
                }
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试");
            }
            return Result.failure(ErrorCode.SYSTEM_ERROR, "已设置密码");
        }
        //修改密码
        if (StrUtil.isNotBlank(dto.getOldPassword())) {
            if (!user.getSignPassword().equals(dto.getOldPassword())) {
                return Result.failure(ErrorCode.PARAM_ERROR, "旧密码输入有误，请确认");
            }
            if (dto.getSignPassword().equals(dto.getOldPassword())) {
                return Result.failure(ErrorCode.PARAM_ERROR, "新密码不得与原密一致，请重新设置");
            }
            update = userMapper.updateById(upUser);
        }
        if (update > 0) {
            return Result.success();
        }
        return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试");

    }

    @Override
    public Result<Integer> setSignPic(SignPicDTO dto) {
        User user0 = userMapper.selectById(dto.getId());
        if (!dto.getSignPassword().equals(user0.getSignPassword())) {
            return Result.failure(ErrorCode.PARAM_ERROR, "签名密码有误");
        }
        User user = new User();
        user.setId(dto.getId());
        user.setSignUrl(dto.getSignURL());
        int update = userMapper.updateById(user);
        if (update > 0) {
            return Result.success();
        }
        return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试");
    }

    @Override
    public Result<Object> delOpenIdById(Integer id) {
        int count = userMapper.delOpenIdById(id);
        if (count < 1) {
            return Result.failure("无效操作");
        }
        return Result.success();
    }

    @Override
    public Result<User> selectByOpenid(String openid) {
        User user = this.baseMapper.getByWxOpenId(openid);
        if (user == null) {
            return Result.failure(1024, "登录失败，请确认账号是否正确");
        } else if (user.getDeptId().equals("-1")) {
            return Result.failure(402, "非员工账号不可登陆");
        }
        Result<User> result = new Result<>();
        // 角色信息
        List<Dict> roles = sysRoleService.getLoginRoles(user.getId());
        user.setRoles(roles);
        user.setPassword(null);
        result.setData(user);
        result.setOk(true);
        return result;
    }

    @Override
    public Result<List<User>> list(Integer roleId, Integer deptId, String name) {
        List<User> users = userMapper.list(roleId, deptId, name);
        return Result.success(users);
    }
    private void getDeptUp(List<Map<String, Object>> depts, Integer deptId) {
        Map<String, Object> dept = deptMapper.getDeptById(deptId);
        if (ObjectUtil.isNotEmpty(dept)) {
            depts.add(dept);
            if (Convert.toInt(dept.get("parentId")) != 0) {
                this.getDeptUp(depts, Convert.toInt(dept.get("parentId")));
            }
        }
    }


}
