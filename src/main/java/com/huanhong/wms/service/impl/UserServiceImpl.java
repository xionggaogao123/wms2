package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Company;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.AddUserDTO;
import com.huanhong.wms.entity.dto.LoginDTO;
import com.huanhong.wms.entity.dto.UpUserDTO;
import com.huanhong.wms.mapper.CompanyMapper;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IDeptService;
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
        addUser.setCompanyId(loginUser.getCompanyId());
        addUser.setParentCompanyId(loginUser.getParentCompanyId());
        Company company = companyMapper.selectById(addUser.getCompanyId());
        if (company == null) {
            return Result.failure("公司信息不存在或已删除");
        }
        Dept dept = deptMapper.selectById(addUser.getDeptId());
        if (dept == null) {
            return Result.failure("部门信息不存在或已删除");
        }
        addUser.setCompanyName(company.getName());
        addUser.setDeptName(dept.getName());
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
