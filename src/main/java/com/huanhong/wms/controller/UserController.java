package com.huanhong.wms.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.AddUserDTO;
import com.huanhong.wms.entity.dto.UpUserDTO;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/v1/user")
@ApiSort(20)
@Api(tags = "用户管理 👨‍👩‍👧‍👦")
public class UserController extends BaseController {

    @Resource
    private IUserService userService;
    @Resource
    private UserMapper userMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（用户名、手机号、工号、部门名）"),
            @ApiImplicitParam(name = "gender", value = "性别  0.未完善 1.男 2.女 3.保密"),
            @ApiImplicitParam(name = "deptId", value = "所属部门ID"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询用户")
    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam Map<String, Object> search) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.orderByAsc("user_name");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("user_name", text).or()
                        .like("login_name", text).or()
                        .like("phone_number", text).or()
                        .like("dept_name", text).or()
                        .like("company_name", text)
                );
            }
        }
        this.eq("deptId", search, query);
        this.eq("gender", search, query);
        Page<User> page = userMapper.selectPage(new Page<>(current, size), query);

        for (int i = 0; i < page.getRecords().size(); i++) {
            page.getRecords().get(i).setPassword(null);
        }

        return Result.success(page);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("获取用户详细信息")
    @GetMapping("/{id}")
    public Result<User> get(@PathVariable Integer id) {
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
    public Result add(@Valid @RequestBody AddUserDTO dto) {
        if (StrUtil.isNotEmpty(dto.getIdNumber())) {
            Validator.validateCitizenIdNumber(dto.getIdNumber(), "请输入正确的身份证");
        }
        if (StrUtil.isNotEmpty(dto.getPhoneNumber())) {
            Validator.validateMobile(dto.getPhoneNumber(), "请输入正确的手机号");
        }
        if (StrUtil.isNotEmpty(dto.getMail())) {
            Validator.validateEmail(dto.getMail(), "请输入正确的Email");
        }


        /**
         * 账号-大小写字母及数字
         */
        Boolean flagLoginName = ReUtil.isMatch("^[A-Za-z0-9]+$", dto.getLoginName());
        if (!flagLoginName) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "请输入正确的账号");
        }

        /**
         * 密码-大小写字母及数字
         */
        if (dto.getPassword() != null) {
            Boolean flagPasswd = ReUtil.isMatch("^[A-Za-z0-9]+$", dto.getPassword());
            if (!flagPasswd) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "请输入正确的密码");
            }
        }

        LoginUser loginUser = this.getLoginUser();
        return userService.addUser(loginUser, dto);
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

        /**
         * 密码-大小写字母及数字
         */
        //密码不为空 判断是和否符合规则
        if (ObjectUtil.isNotEmpty(dto.getPassword())) {
            Boolean flagPasswd = ReUtil.isMatch("^[A-Za-z0-9]+$", dto.getPassword());
            //判断是否有不合法字符
            if (flagPasswd) {
                //判断是否是8~16位字符串
                if (dto.getPassword().length() >= 8 && dto.getPassword().length() <= 16) {
                } else {
                    return Result.failure(ErrorCode.SYSTEM_ERROR, "密码是8~16位的数字和英文字母");
                }
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "密码是8~16位的数字和英文字母");
            }
        }

        LoginUser loginUser = this.getLoginUser();
        if (dto.getId() == null) {
            dto.setId(loginUser.getId());
        }
        return userService.updateUser(loginUser, dto);
    }


    @ApiOperationSupport(order = 9)
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LoginUser loginUser = this.getLoginUser();
        // 检查用户是否可以删除
//        int reservationCount = reservationMapper.checkUndoneReservation(id);
//        if (reservationCount > 0) {
//            return Result.failure("当前用户存在未结束预约无法删除!");
//        }
        int i = userMapper.deleteById(id);
        if (i > 0) {
            // 删除用户其它数据
        }
        return render(i > 0);
    }

}
