package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.UpUserDTO;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IDeptService;
import com.huanhong.wms.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/user")
@ApiSort(20)
@Api(tags = "用户管理 👨‍👩‍👧‍👦")
public class UserController extends BaseController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IUserService userService;
    @Resource
    private IDeptService deptService;

    @Resource
    private UserMapper userMapper;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询用户")
    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size, @RequestParam Map<String, Object> search) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.orderByAsc("user_name");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("user_name", text).or()
                        .like("login_name", text).or()
                        .like("phone_number", text).or()
                        .like("department", text).or()
                        .like("company_name", text)
                );
            }
        }
        this.eq("sex", search, query);
        return Result.success(userMapper.selectPage(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("获取用户详细信息")
    @GetMapping("/{id}")
    public Result<User> getMyInfo(@PathVariable Integer id) {
        User user = userService.getUserInfo(id);
        if (user == null) {
            return Result.failure(ErrorCode.DATA_IS_NULL, "用户不存在或已被删除");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("添加用户")
    @PostMapping
    public Result add(@Valid @RequestBody User user) {
        User check = userMapper.getUserByAccount(user.getLoginName());
        if (check != null) {
            return Result.failure("该账号已存在");
        }
        int insert = userMapper.insert(user);
        if (insert > 0) {
            // 更新部门人数
            deptService.updateDeptUserCount();
        }
        return render(insert > 0);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("更新用户")
    @PutMapping
    public Result update(@Valid @RequestBody UpUserDTO dto) {
        if (StrUtil.isNotEmpty(dto.getIdNumber())) {
            Validator.validateCitizenIdNumber(dto.getIdNumber(), "请输入正确的身份证");
        }
        if (StrUtil.isNotEmpty(dto.getPhoneNumber())) {
            Validator.validateMobile(dto.getPhoneNumber(), "请输入正确的手机号");
        }
        if (StrUtil.isNotEmpty(dto.getMail())) {
            Validator.validateEmail(dto.getMail(), "请输入正确的Email");
        }
        LoginUser loginUser = this.getLoginUser();
        if (dto.getId() == null) {
            dto.setId(loginUser.getId());
        }
        User updateUser = new User();
        BeanUtil.copyProperties(dto, updateUser);
        if (StrUtil.isNotEmpty(dto.getPassword())) {
            updateUser.setPassword(SecureUtil.md5(dto.getPassword()));
        }
        int update = 0;
        try {
            update = userMapper.updateById(updateUser);
        } catch (Exception e) {
            log.error("用户信息更新异常: {}", updateUser);
        }
        // 更新部门人数
//        deptService.updateDeptUserCount();
        return render(update > 0);
    }


    @ApiOperationSupport(order = 9)
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LoginUser loginUser = this.getLoginUser();
//        int reservationCount = reservationMapper.checkUndoneReservation(id);
//        if (reservationCount > 0) {
//            return Result.failure("当前用户存在未结束预约无法删除!");
//        }
        int i = userMapper.deleteById(id);
        if (i > 0) {
            // 删除用户设备数据
//            monitorDeviceUserService.removeUserAuthority(id);
        }
        return render(i > 0);
    }

}
