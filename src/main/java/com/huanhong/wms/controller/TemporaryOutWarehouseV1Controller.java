package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseV1AddRequest;
import com.huanhong.wms.entity.TemporaryOutWarehouse;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;
import com.huanhong.wms.service.TemporaryOutWarehouseV1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author wang
 * @date 2022/5/25 16:34
 */
@Slf4j
@Validated
@ApiSort()
@Api(tags = "临库出库单V1")
@RestController
@RequestMapping("/v1/temporary-out-warehouse-v1")
public class TemporaryOutWarehouseV1Controller extends BaseController {

    @Resource
    private TemporaryOutWarehouseV1Service tempOutWarehouseV1Service;


    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody TemporaryOutWarehouseV1AddRequest request) {
        try {
            return tempOutWarehouseV1Service.addMasterAndSublist(request);
        } catch (Exception e) {
            log.error("添加临库出库单出错，异常", e);
            return Result.failure("系统异常：临库出库单添加失败。");
        }
    }


    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "id查询", notes = "生成代码")
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable("id") Long id) {
        try {
            return tempOutWarehouseV1Service.selectById(id);
        } catch (Exception e) {
            log.error("查询临时出库数据异常:{}", e.getMessage());
            return Result.failure("系统异常：临库出库单添加失败。");
        }
    }


    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "id删除", notes = "生成代码")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable("id") Long id) {
        try {
            return tempOutWarehouseV1Service.deleteById(id);
        } catch (Exception e) {
            log.error("查询临时出库数据异常:{}", e.getMessage());
            return Result.failure("系统异常：临库出库单添加失败。");
        }
    }


    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryOutWarehouse>> page(@RequestParam(defaultValue = "1") Integer current,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    TemporaryOutWarehouseVO temporaryOutWarehouseVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryOutWarehouse> pageResult = tempOutWarehouseV1Service.pageFuzzyQuery(new Page<>(current, size), temporaryOutWarehouseVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到临库出库单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }
}
