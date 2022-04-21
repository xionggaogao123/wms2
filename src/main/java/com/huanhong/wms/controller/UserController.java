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
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.WarehouseManager;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.mapper.WarehouseManagerMapper;
import com.huanhong.wms.service.IDeptService;
import com.huanhong.wms.service.IUserService;
import com.huanhong.wms.service.IWarehouseManagementService;
import com.huanhong.wms.service.IWarehouseManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Resource
    private IDeptService deptService;

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private IWarehouseManagerService warehouseManagerService;

    @Resource
    private WarehouseManagerMapper warehouseManagerMapper;

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

        //true 为启用  false 为停用
        if (deptService.isStopUsing(dto.getDeptId())) {
            return Result.failure("部门停用中,无法添加用户");
        }

        // 账号-大小写字母及数字
        Boolean flagLoginName = ReUtil.isMatch("^[A-Za-z0-9]+$", dto.getLoginName());
        if (!flagLoginName) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "请输入正确的账号");
        }
        // 密码-大小写字母及数字
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

        //true 为启用  false 为停用
        if (ObjectUtil.isNotNull(dto.getDeptId())) {
            if (deptService.isStopUsing(dto.getDeptId())) {
                return Result.failure("部门停用中,无法转入用户");
            }
        }

        LoginUser loginUser = this.getLoginUser();
        if (dto.getId() == null) {
            dto.setId(loginUser.getId());
        }

        /**
         * 绑定仓库
         */
        if (ObjectUtil.isNotEmpty(dto.getWarehouseIdList())){
            String loginName = loginUser.getLoginName();
            List<WarehouseManager> warehouseManagerList = warehouseManagerService.getWarehouseManagerListByLoginName(loginName);
            List<String> oldWarehouseManagerList = new ArrayList<>();
            for (WarehouseManager warehouseManager:warehouseManagerList
                 ) {
                oldWarehouseManagerList.add(warehouseManager.getWarehouseId());
            }
            //取新传入的list
            List<String> newWarehouseIdList = dto.getWarehouseIdList();
            //取新对旧的差集用于新增仓库管理员
            List<String> addWarehouseIdList =  newWarehouseIdList.stream().filter(item -> !oldWarehouseManagerList.contains(item)).collect(Collectors.toList());
            //取旧对新的差集用于删除仓库管理员
            List<String> deleteWarehouseIdList = oldWarehouseManagerList.stream().filter(item -> !newWarehouseIdList.contains(item)).collect(Collectors.toList());
            //新增仓库管理员DTO
            AddWarehouseManagerDTO addWarehouseManagerDTO = new AddWarehouseManagerDTO();
            for (String warehouseId :addWarehouseIdList
                 ) {
                addWarehouseManagerDTO.setLoginName(loginName);
                addWarehouseManagerDTO.setWarehouseId(warehouseId);
                warehouseManagerService.addWarehouseManager(addWarehouseManagerDTO);
            }
            //删除仓库管理员
            for (String warehouseId:deleteWarehouseIdList
                 ) {
                for (WarehouseManager warehouseManage : warehouseManagerList
                ) {
                    if (warehouseId.equals(warehouseManage.getWarehouseId())){
                        Integer id = warehouseManage.getId();
                        warehouseManagerService.removeById(id);
                    }
                }
            }
        }

        return userService.updateUser(loginUser, dto);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("设置、修改签名密码")
    @PostMapping("/signPass")
    public Result<Integer> setSignPassword(@Valid @RequestBody SignPasswordDTO dto) {

        LoginUser loginUser = this.getLoginUser();
        if (StrUtil.isBlank(dto.getSignPassword())) {
            return Result.failure("签名密码为空");
        }
        if (StrUtil.isNotBlank(dto.getOldPassword()) && StrUtil.isNotBlank(dto.getCommitPassword())) {
            return Result.failure("参数有误");
        }
        dto.setId(loginUser.getId());
        return userService.setSignPassword(dto);
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation("设置、修改签名图片")
    @PostMapping("/signPic")
    public Result<Integer> setSignPic(@Valid @RequestBody SignPicDTO dto) {

        LoginUser loginUser = this.getLoginUser();
        if (StrUtil.isBlank(dto.getSignPassword())) {
            return Result.failure("签名密码为空");
        }
        if (StrUtil.isBlank(dto.getSignURL())) {
            return Result.failure("签名图片地址为空");
        }
        dto.setId(loginUser.getId());
        return userService.setSignPic(dto);

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


    @ApiOperationSupport(order = 9)
    @ApiOperation("根据登录用户获取仓库Id")
    @GetMapping("/getWarehouseIdByUser")
    public Result getWarehouseIdByUserId(LoginUser loginUser) {
        Integer companyId = loginUser.getCompanyId();
        List<WarehouseManagement> warehouseManagementList = warehouseManagementService.getWarehouseByCompanyId(companyId);
        if (ObjectUtil.isEmpty(warehouseManagementList)) {
            return Result.success("未找到仓库信息");
        }
        List<String> listWarehouseId = new ArrayList<>();
        for (WarehouseManagement warehouse : warehouseManagementList
        ) {
            listWarehouseId.add(warehouse.getWarehouseId());
        }
        return Result.success(listWarehouseId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id"),
            @ApiImplicitParam(name = "deptId", value = "部门id"),
            @ApiImplicitParam(name = "name", value = "姓名"),
    })
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "查询用户列表-角色、部门、姓名")
    @GetMapping("/list")
    public Result<List<User>> list(Integer roleId, Integer deptId, String name) {
        return userService.list(roleId, deptId, name);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleIds", value = "角色ids 例： 1,2,3"),
            @ApiImplicitParam(name = "deptId", value = "部门id"),
    })
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "流程引擎 过程中 根据选择的部门信息，查询用户列表-角色数组、部门")
    @GetMapping("/listByRoleIdsAndDeptId")
    public Result<List<User>> listByRoleIdsAndDeptId(String roleIds, Integer deptId) {
        return userService.listByRoleIdsAndDeptId(roleIds, deptId);
    }
}
