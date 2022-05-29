package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.entity.vo.TemporaryLibraryInventoryVO;
import com.huanhong.wms.service.ITemporaryLibraryInventoryService;
import com.huanhong.wms.service.TemporaryLibraryInventoryV1Service;
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
 * @date 2022/5/25 9:39
 */
@ApiSort()
@Api(tags = "临库清点单V1")
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/temporary-library-inventory-v1")
public class TemporaryLibraryInventoryV1Controller extends BaseController {

    @Resource
    private TemporaryLibraryInventoryV1Service temporaryLibraryInventoryV1Service;

    @Resource
    private ITemporaryLibraryInventoryService temporaryLibraryInventoryService;


    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddTemporaryLibraryInventoryAndDetailsDTO addTemporaryLibraryInventoryAndDetailsDTO) {
        log.info("新增临时清点的数据为:{}", JsonUtil.obj2String(addTemporaryLibraryInventoryAndDetailsDTO));
        try {
            return temporaryLibraryInventoryV1Service.addTemporaryMainAndSublistAndWarehouse(addTemporaryLibraryInventoryAndDetailsDTO);
        } catch (Exception e) {
            return Result.failure("添加失败");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateTemporaryLibraryInventoryAndDetailsDTO update) {
        log.info("新增临时清点的数据为:{}", JsonUtil.obj2String(update));
        try {
            return temporaryLibraryInventoryV1Service.updateTemporaryMainAndSublistAndWarehouse(update);
        } catch (Exception e) {
            return Result.failure("更新失败");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "查询", notes = "生成代码")
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable("id") Long id) {
        try {
            return temporaryLibraryInventoryV1Service.selectById(id);
        } catch (Exception e) {
            return Result.failure("更新失败");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryLibraryInventory>> page(@RequestParam(defaultValue = "1") Integer current,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        TemporaryLibraryInventoryVO temporaryLibraryInventoryVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryLibraryInventory> pageResult = temporaryLibraryInventoryService.pageFuzzyQuery(new Page<>(current, size), temporaryLibraryInventoryVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到清点单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }
}
