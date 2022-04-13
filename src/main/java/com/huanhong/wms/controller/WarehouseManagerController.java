package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddWarehouseManagerDTO;
import com.huanhong.wms.entity.dto.UpdateWarehouseManagerDTO;
import com.huanhong.wms.entity.vo.WarehouseManagerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.WarehouseManager;
import com.huanhong.wms.mapper.WarehouseManagerMapper;
import com.huanhong.wms.service.IWarehouseManagerService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiSort()
@Slf4j
@Api(tags = "库管员管理")
@RestController
@RequestMapping("/v1//warehouse-manager")
public class WarehouseManagerController extends BaseController {

    @Resource
    private IWarehouseManagerService warehouseManagerService;



    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<WarehouseManager>> page(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               WarehouseManagerVO warehouseManagerVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<WarehouseManager> pageResult = warehouseManagerService.pageFuzzyQuery(new Page<>(current, size), warehouseManagerVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到库管员信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody AddWarehouseManagerDTO addWarehouseManagerDTO) {

            List<WarehouseManager> warehouseManagerList = warehouseManagerService.getWarehouseManagerListByUserId(addWarehouseManagerDTO.getUserId());
            String warehouseId = addWarehouseManagerDTO.getWarehouseId();
            for (WarehouseManager warehouseManager:warehouseManagerList
                 ) {
                if (warehouseId.equals(warehouseManager.getWarehouseId())){
                    return Result.success("此用户已可管理此仓库！无法再次新增管理员！");
                }
            }
            return warehouseManagerService.addWarehouseManager(addWarehouseManagerDTO);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody UpdateWarehouseManagerDTO updateWarehouseManagerDTO) {
            WarehouseManager warehouseManager = warehouseManagerService.getWarehouseManagerById(updateWarehouseManagerDTO.getId());
            if (ObjectUtil.isEmpty(warehouseManager)){
                return Result.failure("此库管员信息不存在！");
            }
            List<WarehouseManager> warehouseManagerList = warehouseManagerService.getWarehouseManagerListByUserId(warehouseManager.getUserId());
            String warehouseId = updateWarehouseManagerDTO.getWarehouseId();
            for (WarehouseManager warehouseManagerAnother:warehouseManagerList
            ) {
                if (warehouseId.equals(warehouseManagerAnother.getWarehouseId())){
                    return Result.success("此用户已可管理此仓库！无法再次新增管理员！");
                }
            }
              return warehouseManagerService.updateWarehouseManager(updateWarehouseManagerDTO);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(warehouseManagerService.removeById(id));
        }


        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据ID获取库管员")
        @GetMapping("getWarehouseManagerById/{id}")
        public Result getWarehouseManagerById(@PathVariable Integer id) {
            try {
                    WarehouseManager warehouseManager = warehouseManagerService.getWarehouseManagerById(id);
                if (ObjectUtil.isNotEmpty(warehouseManager)){
                    return Result.success(warehouseManager);
                }else {
                    return Result.failure("未查询到相关信息！");
                }
                } catch (Exception e) {
                    log.error("查询失败,异常：", e);
                    return Result.failure("查询失败，系统异常！");
                }
        }

        @ApiOperationSupport(order = 6)
        @ApiOperation(value = "根据用户ID获取管理的仓库")
        @GetMapping("getWarehouseManagerByUserId/{userId}")
        public Result getWarehouseManagerByUserId(@PathVariable Integer userId) {
            List<WarehouseManager> warehouseManagerList = warehouseManagerService.getWarehouseManagerListByUserId(userId);
            if (ObjectUtil.isEmpty(warehouseManagerList)){
                return Result.failure("未查询到对应信息！");
            }
            List<String> warehouseIdList = new ArrayList<>();
            for (WarehouseManager warehouseManager:warehouseManagerList
                 ) {
                String warehouseId = warehouseManager.getWarehouseId();
                warehouseIdList.add(warehouseId);
            }
            return Result.success(warehouseIdList);
        }

}

