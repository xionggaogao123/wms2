package com.huanhong.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.google.common.util.concurrent.AtomicDouble;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.TestRequest;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.MaterialMapper;
import com.huanhong.wms.service.MaterialPriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/29 14:36
 */
@ApiSort()
@Validated
@Slf4j
@Api(tags = "价格")
@RestController
@RequestMapping("/v1/test")
public class TestController extends BaseController {

    @Resource
    private MaterialPriceService materialPriceService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("价格补填")
    @PostMapping("")
    public Result get(@RequestParam("materialCoding")String materialCoding,@RequestParam("materialName")String materialName,@RequestParam("warehouseId")String warehouseId) {
        materialPriceService.addMaterialPrice(materialCoding,materialName,warehouseId);
        return Result.success();
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("价格补填")
    @PostMapping("")
    public Result get(@RequestParam("materialCoding")String materialCoding,@RequestParam("warehouseId")String warehouseId) {
        materialPriceService.selectMaterialPrice(materialCoding,warehouseId);
        return Result.success();
    }
}
