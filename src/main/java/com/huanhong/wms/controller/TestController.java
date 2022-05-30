package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
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
    public Result get(@RequestParam("materialCoding") String materialCoding, @RequestParam("materialName") String materialName, @RequestParam("warehouseId") String warehouseId) {
        materialPriceService.addMaterialPrice(materialCoding, materialName, warehouseId);
        return Result.success();
    }


}
