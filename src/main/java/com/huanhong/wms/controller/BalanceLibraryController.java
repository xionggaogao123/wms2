package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.BalanceLibrary;
import com.huanhong.wms.service.IBalanceLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@ApiSort()
@Api(tags = "平衡利库表")
@RestController
@RequestMapping("/v1/balance-library")
public class BalanceLibraryController extends BaseController {

    @Resource
    private IBalanceLibraryService balanceLibraryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询平衡利库表")
    @GetMapping("/page")
    public Result<Page<BalanceLibrary>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                             BalanceLibrary balanceLibrary) {
        LambdaQueryWrapper<BalanceLibrary> query = Wrappers.<BalanceLibrary>lambdaQuery()
                .like(StrUtil.isNotBlank(balanceLibrary.getBalanceLibraryNo()),BalanceLibrary::getBalanceLibraryNo,balanceLibrary.getBalanceLibraryNo())
                .like(StrUtil.isNotBlank(balanceLibrary.getPlanner()),BalanceLibrary::getPlanner,balanceLibrary.getPlanner())
                .like(StrUtil.isNotBlank(balanceLibrary.getPlanningDepartment()),BalanceLibrary::getPlanningDepartment,balanceLibrary.getPlanningDepartment())
                .like(StrUtil.isNotBlank(balanceLibrary.getDemandDepartment()),BalanceLibrary::getDemandDepartment,balanceLibrary.getDemandDepartment())
                .eq(null != balanceLibrary.getPlanClassification(),BalanceLibrary::getPlanClassification,balanceLibrary.getPlanClassification())
                .eq(null != balanceLibrary.getTargetWarehouse(),BalanceLibrary::getTargetWarehouse,balanceLibrary.getTargetWarehouse())
                .orderByDesc(BalanceLibrary::getId);

        return Result.success(balanceLibraryService.page(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加平衡利库表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody BalanceLibrary balanceLibrary) {
        return render(balanceLibraryService.save(balanceLibrary));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新平衡利库表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody BalanceLibrary balanceLibrary) {
        return render(balanceLibraryService.updateById(balanceLibrary));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除平衡利库表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return balanceLibraryService.delete(id);
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "导入采购计划")
    @PostMapping("/importProcurements")
    public Result importProcurements(@RequestBody List<Integer> procurementPlanIds) {
        return balanceLibraryService.importProcurementPlans(procurementPlanIds);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "平衡利库详情")
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        return balanceLibraryService.detail(id);
    }

}

