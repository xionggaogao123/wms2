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
@Api(tags = "็จๆท็ฎก็ ๐จโ๐ฉโ๐งโ๐ฆ")
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
            @ApiImplicitParam(name = "current", value = "ๅฝๅ้กต็?"),
            @ApiImplicitParam(name = "size", value = "ๆฏ้กต่กๆฐ"),
            @ApiImplicitParam(name = "search", value = "่ๅๆ็ดข๏ผ็จๆทๅใๆๆบๅทใๅทฅๅทใ้จ้จๅ๏ผ"),
            @ApiImplicitParam(name = "gender", value = "ๆงๅซ  0.ๆชๅฎๅ 1.็ท 2.ๅฅณ 3.ไฟๅฏ"),
            @ApiImplicitParam(name = "deptId", value = "ๆๅฑ้จ้จID"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "ๅ้กตๆฅ่ฏข็จๆท")
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
    @ApiOperation("่ทๅ็จๆท่ฏฆ็ปไฟกๆฏ")
    @GetMapping("/{id}")
    public Result<User> get(@PathVariable Integer id) {
        User user = userService.getUserInfo(id);
        if (user == null) {
            return Result.failure(ErrorCode.DATA_IS_NULL, "็จๆทไธๅญๅจๆๅทฒ่ขซๅ?้ค");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("ๆทปๅ?็จๆท")
    @PostMapping
    public Result add(@Valid @RequestBody AddUserDTO dto) {
        if (StrUtil.isNotEmpty(dto.getIdNumber())) {
            Validator.validateCitizenIdNumber(dto.getIdNumber(), "่ฏท่พๅฅๆญฃ็กฎ็่บซไปฝ่ฏ");
        }
        if (StrUtil.isNotEmpty(dto.getPhoneNumber())) {
            Validator.validateMobile(dto.getPhoneNumber(), "่ฏท่พๅฅๆญฃ็กฎ็ๆๆบๅท");
        }
        if (StrUtil.isNotEmpty(dto.getMail())) {
            Validator.validateEmail(dto.getMail(), "่ฏท่พๅฅๆญฃ็กฎ็Email");
        }

        //true ไธบๅฏ็จ  false ไธบๅ็จ
        if (deptService.isStopUsing(dto.getDeptId())) {
            return Result.failure("้จ้จๅ็จไธญ,ๆ?ๆณๆทปๅ?็จๆท");
        }

        // ่ดฆๅท-ๅคงๅฐๅๅญๆฏๅๆฐๅญ
        Boolean flagLoginName = ReUtil.isMatch("^[A-Za-z0-9]+$", dto.getLoginName());
        if (!flagLoginName) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "่ฏท่พๅฅๆญฃ็กฎ็่ดฆๅท");
        }
        // ๅฏ็?-ๅคงๅฐๅๅญๆฏๅๆฐๅญ
        if (dto.getPassword() != null) {
            Boolean flagPasswd = ReUtil.isMatch("^[A-Za-z0-9]+$", dto.getPassword());
            if (!flagPasswd) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "่ฏท่พๅฅๆญฃ็กฎ็ๅฏ็?");
            }
        }


        LoginUser loginUser = this.getLoginUser();
        Result result =  userService.addUser(loginUser, dto);

        if (!result.isOk()){
            return Result.failure("ๆฐๅข็จๆทๅคฑ่ดฅ๏ผ");
        }

        /**
         * ็ปๅฎไปๅบ
         */
        if (ObjectUtil.isNotEmpty(dto.getWarehouseIdList())){
            //ๅๆฐไผ?ๅฅ็list
            List<String> addWarehouseIdList = dto.getWarehouseIdList();
            //ๆฐๅขไปๅบ็ฎก็ๅDTO
            AddWarehouseManagerDTO addWarehouseManagerDTO = new AddWarehouseManagerDTO();
            for (String warehouseId :addWarehouseIdList
            ) {
                addWarehouseManagerDTO.setLoginName(dto.getLoginName());
                addWarehouseManagerDTO.setWarehouseId(warehouseId);
                warehouseManagerService.addWarehouseManager(addWarehouseManagerDTO);
            }
        }
        return result;
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("ๆดๆฐ็จๆท")
    @PutMapping
    public Result update(@Valid @RequestBody UpUserDTO dto) {

        if (StrUtil.isNotEmpty(dto.getIdNumber())) {
            Validator.validateCitizenIdNumber(dto.getIdNumber(), "่ฏท่พๅฅๆญฃ็กฎ็่บซไปฝ่ฏ");
        }

        if (StrUtil.isNotEmpty(dto.getPhoneNumber())) {
            Validator.validateMobile(dto.getPhoneNumber(), "่ฏท่พๅฅๆญฃ็กฎ็ๆๆบๅท");
        }

        if (StrUtil.isNotEmpty(dto.getMail())) {
            Validator.validateEmail(dto.getMail(), "่ฏท่พๅฅๆญฃ็กฎ็Email");
        }

        //true ไธบๅฏ็จ  false ไธบๅ็จ
        if (ObjectUtil.isNotNull(dto.getDeptId())) {
            if (deptService.isStopUsing(dto.getDeptId())) {
                return Result.failure("้จ้จๅ็จไธญ,ๆ?ๆณ่ฝฌๅฅ็จๆท");
            }
        }

        LoginUser loginUser = this.getLoginUser();
        if (dto.getId() == null) {
            dto.setId(loginUser.getId());
        }

        /**
         * ็ปๅฎไปๅบ
         */
        if (ObjectUtil.isNotEmpty(dto.getWarehouseIdList())){
            String loginName = loginUser.getLoginName();
            List<WarehouseManager> warehouseManagerList = warehouseManagerService.getWarehouseManagerListByLoginName(loginName);
            List<String> oldWarehouseManagerList = new ArrayList<>();
            for (WarehouseManager warehouseManager:warehouseManagerList
                 ) {
                oldWarehouseManagerList.add(warehouseManager.getWarehouseId());
            }
            //ๅๆฐไผ?ๅฅ็list
            List<String> newWarehouseIdList = dto.getWarehouseIdList();
            //ๅๆฐๅฏนๆง็ๅทฎ้็จไบๆฐๅขไปๅบ็ฎก็ๅ
            List<String> addWarehouseIdList =  newWarehouseIdList.stream().filter(item -> !oldWarehouseManagerList.contains(item)).collect(Collectors.toList());
            //ๅๆงๅฏนๆฐ็ๅทฎ้็จไบๅ?้คไปๅบ็ฎก็ๅ
            List<String> deleteWarehouseIdList = oldWarehouseManagerList.stream().filter(item -> !newWarehouseIdList.contains(item)).collect(Collectors.toList());
            //ๆฐๅขไปๅบ็ฎก็ๅDTO
            AddWarehouseManagerDTO addWarehouseManagerDTO = new AddWarehouseManagerDTO();
            for (String warehouseId :addWarehouseIdList
                 ) {
                addWarehouseManagerDTO.setLoginName(loginName);
                addWarehouseManagerDTO.setWarehouseId(warehouseId);
                warehouseManagerService.addWarehouseManager(addWarehouseManagerDTO);
            }
            //ๅ?้คไปๅบ็ฎก็ๅ
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
    @ApiOperation("่ฎพ็ฝฎใไฟฎๆน็ญพๅๅฏ็?")
    @PostMapping("/signPass")
    public Result<Integer> setSignPassword(@Valid @RequestBody SignPasswordDTO dto) {

        LoginUser loginUser = this.getLoginUser();
        if (StrUtil.isBlank(dto.getSignPassword())) {
            return Result.failure("็ญพๅๅฏ็?ไธบ็ฉบ");
        }
        if (StrUtil.isNotBlank(dto.getOldPassword()) && StrUtil.isNotBlank(dto.getCommitPassword())) {
            return Result.failure("ๅๆฐๆ่ฏฏ");
        }
        dto.setId(loginUser.getId());
        return userService.setSignPassword(dto);
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation("่ฎพ็ฝฎใไฟฎๆน็ญพๅๅพ็")
    @PostMapping("/signPic")
    public Result<Integer> setSignPic(@Valid @RequestBody SignPicDTO dto) {

        LoginUser loginUser = this.getLoginUser();
        if (StrUtil.isBlank(dto.getSignPassword())) {
            return Result.failure("็ญพๅๅฏ็?ไธบ็ฉบ");
        }
        if (StrUtil.isBlank(dto.getSignURL())) {
            return Result.failure("็ญพๅๅพ็ๅฐๅไธบ็ฉบ");
        }
        dto.setId(loginUser.getId());
        return userService.setSignPic(dto);

    }

    @ApiOperationSupport(order = 9)
    @ApiOperation("ๅ?้ค็จๆท")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LoginUser loginUser = this.getLoginUser();
        // ๆฃๆฅ็จๆทๆฏๅฆๅฏไปฅๅ?้ค
//        int reservationCount = reservationMapper.checkUndoneReservation(id);
//        if (reservationCount > 0) {
//            return Result.failure("ๅฝๅ็จๆทๅญๅจๆช็ปๆ้ข็บฆๆ?ๆณๅ?้ค!");
//        }
        int i = userMapper.deleteById(id);
        if (i > 0) {
            // ๅ?้ค็จๆทๅถๅฎๆฐๆฎ
        }
        return render(i > 0);
    }


    @ApiOperationSupport(order = 9)
    @ApiOperation("ๆ?นๆฎ็ปๅฝ็จๆท่ทๅไปๅบId")
    @GetMapping("/getWarehouseIdByUser")
    public Result getWarehouseIdByUserId(LoginUser loginUser) {
        Integer companyId = loginUser.getCompanyId();
        List<WarehouseManagement> warehouseManagementList = warehouseManagementService.getWarehouseByCompanyId(companyId);
        if (ObjectUtil.isEmpty(warehouseManagementList)) {
            return Result.success("ๆชๆพๅฐไปๅบไฟกๆฏ");
        }
        List<String> listWarehouseId = new ArrayList<>();
        for (WarehouseManagement warehouse : warehouseManagementList
        ) {
            listWarehouseId.add(warehouse.getWarehouseId());
        }
        return Result.success(listWarehouseId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "่ง่ฒid"),
            @ApiImplicitParam(name = "deptId", value = "้จ้จid"),
            @ApiImplicitParam(name = "name", value = "ๅงๅ"),
    })
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "ๆฅ่ฏข็จๆทๅ่กจ-่ง่ฒใ้จ้จใๅงๅ")
    @GetMapping("/list")
    public Result<List<User>> list(Integer roleId, Integer deptId, String name) {
        return userService.list(roleId, deptId, name);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleIds", value = "่ง่ฒids ไพ๏ผ 1,2,3"),
            @ApiImplicitParam(name = "deptId", value = "้จ้จid"),
    })
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "ๆต็จๅผๆ ่ฟ็จไธญ ๆ?นๆฎ้ๆฉ็้จ้จไฟกๆฏ๏ผๆฅ่ฏข็จๆทๅ่กจ-่ง่ฒๆฐ็ปใ้จ้จ")
    @GetMapping("/listByRoleIdsAndDeptId")
    public Result<List<User>> listByRoleIdsAndDeptId(String roleIds, Integer deptId) {
        return userService.listByRoleIdsAndDeptId(roleIds, deptId);
    }
}
