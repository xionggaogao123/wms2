package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.dto.AddCargoSpacedDTO;
import com.huanhong.wms.entity.dto.UpdateCargoSpaceDTO;
import com.huanhong.wms.entity.vo.CargoSpaceVO;
import com.huanhong.wms.mapper.CargoSpaceManagementMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IShelfManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/cargo-space-management")
@ApiSort()
@Api(tags = "货位管理")
public class CargoSpaceManagementController extends BaseController {

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Resource
    private IShelfManagementService shelfManagementService;

    @Resource
    private IInventoryInformationService iventoryInformationService;

    @Resource
    private CargoSpaceManagementMapper cargoSpaceManagementMapper;

    public static final Logger LOGGER = LoggerFactory.getLogger(CargoSpaceManagementController.class);

    @Autowired
    private JudgeConfig judgeConfig;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询货位")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<CargoSpaceManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   CargoSpaceVO cargoSpaceVO) {
        QueryWrapper<CargoSpaceManagement> query = new QueryWrapper<>();
        query.orderByDesc("id");
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<CargoSpaceManagement> pageResult = cargoSpaceManagementService.pageFuzzyQuery(new Page<>(current, size), cargoSpaceVO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加货位管理")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddCargoSpacedDTO addCargoSpacedDTO) {
        try {
            /**
             * 检查货位编码是否合法
             */
            if (addCargoSpacedDTO.getCargoSpaceId().length() != 2 || !StrUtils.isNumeric(addCargoSpacedDTO.getCargoSpaceId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "货位编号为两位数字");
            }

            addCargoSpacedDTO.setCargoSpaceId(addCargoSpacedDTO.getShelfId() + addCargoSpacedDTO.getCargoSpaceId());
            CargoSpaceManagement cargoSpaceManagementIsExist = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(addCargoSpacedDTO.getCargoSpaceId());
            ShelfManagement shelfManagementIsExist = shelfManagementService.getShelfByShelfId(addCargoSpacedDTO.getShelfId());

            //判断货架是否存在
            if (ObjectUtil.isEmpty(shelfManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "货架不存在,无法添加货位");
            }

            //货位编号重复判定
            if (ObjectUtil.isNotEmpty(cargoSpaceManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "货位编码重复，货位已存在");
            }
            try {
                CargoSpaceManagement addCargoSpace = new CargoSpaceManagement();
                BeanUtil.copyProperties(addCargoSpacedDTO, addCargoSpace);
                int insert = cargoSpaceManagementMapper.insert(addCargoSpace);
                if (insert > 0) {
                    LOGGER.info("添加货位成功");
                } else {
                    LOGGER.error("添加货位失败");
                }
                return render(insert>0);
            } catch (Exception e) {
                LOGGER.error("添加货位错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加货位失败--处理（判断库区编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新货位管理")
    @PutMapping("/update")
    public Result updateByCargoSpaceId(@Valid @RequestBody UpdateCargoSpaceDTO updateCargoSpaceDTO) {
        UpdateWrapper<CargoSpaceManagement> updateWrapper = new UpdateWrapper<>();
        try {
            //空值判断
            if (StringUtils.isBlank(updateCargoSpaceDTO.getCargoSpaceId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "货位编码不能为空");
            }
            //判断更新的货架是否存在
            CargoSpaceManagement cargoSpaceDTOIsExist = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(updateCargoSpaceDTO.getCargoSpaceId());
            if (ObjectUtil.isEmpty(cargoSpaceDTOIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此货位编码，货架不存在");
            }
            updateWrapper.eq("cargo_space_id", updateCargoSpaceDTO.getCargoSpaceId());
            CargoSpaceManagement updateCargoSpace = new CargoSpaceManagement();
            BeanUtil.copyProperties(updateCargoSpaceDTO, updateCargoSpace);
            int update = cargoSpaceManagementMapper.update(updateCargoSpace, updateWrapper);
            return render(update > 0);
        } catch (Exception e) {
            LOGGER.error("更新货位信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：货位更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除货位管理", notes = "生成代码")
    @DeleteMapping("delete/{cargoId}")
    public Result delete(@PathVariable String cargoId) {

        QueryWrapper<CargoSpaceManagement> queryWrapper = new QueryWrapper<>();
        try {
            //检查货位是否存在
            queryWrapper.eq("cargo_space_id", cargoId);
            CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "操作失败：货位不存在");
            }

            /**
             * 检查此货位下是否有库存
             */
            List<InventoryInformation> inventoryInformationList = iventoryInformationService.getInventoryInformationByCargoSpaceId(cargoId);
            if (ObjectUtil.isNotEmpty(inventoryInformationList)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "操作失败：此货位使用中，请清空库存后再次操作");
            }

            int i = cargoSpaceManagementMapper.delete(queryWrapper);
            LOGGER.info("货位:  " + cargoId + "删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除货位信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：货位删除失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取货位详细信息")
    @GetMapping("/getCargoSpaceByCargoSpaceId/{cargoSpaceId}")
    public Result getCargoSpace(@PathVariable String cargoSpaceId) {
        try {
            CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(cargoSpaceId);
            return Result.success(cargoSpaceManagement);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取货位信息失败");
        }
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "获取对应货位的所有库存")
    @GetMapping("/getInventoryInformationByCargoSpaceId/{cargoSpaceId}")
    public Result getAllInventory(@PathVariable String cargoSpaceId) {
        try {
            List<InventoryInformation> inventoryInformationList = iventoryInformationService.getInventoryInformationByCargoSpaceId(cargoSpaceId);
            return Result.success(inventoryInformationList);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取库存信息失败");
        }
    }
}

