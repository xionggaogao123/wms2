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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private MaterialMapper materialMapper;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @ApiOperationSupport(order = 1)
    @ApiOperation("价格补填")
    @PostMapping("")
    public Result get(TestRequest request) {
        AtomicDouble atomicDouble = new AtomicDouble();
        AtomicDouble amount = new AtomicDouble();
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding",request.getMaterialCoding());
        queryWrapper.eq("material_name",request.getMaterialName());
        List<Material> materialslist = materialMapper.selectList(queryWrapper);
        QueryWrapper<InventoryInformation> informationQueryWrapper = new QueryWrapper<>();
        informationQueryWrapper.eq("material_coding",request.getMaterialCoding());
        informationQueryWrapper.eq("material_name",request.getMaterialName());
        List<InventoryInformation> inventoryInformation = inventoryInformationMapper.selectList(informationQueryWrapper);
        for (InventoryInformation inventory : inventoryInformation) {
            BigDecimal unitPrice = inventory.getUnitPrice();
            Double inventoryCredit = inventory.getInventoryCredit();
            //求 库存表中的某种物料每个批次的单价*对应批次的数量
            BigDecimal multiply = unitPrice.multiply(new BigDecimal(inventoryCredit));
            amount.getAndSet(multiply.doubleValue());
            atomicDouble.getAndAdd(inventoryCredit);
        }
        double avgBuyPrice =  amount.get() / atomicDouble.get();
        materialslist.forEach(material -> {
            material.setAvgBuyPrice(avgBuyPrice);
            material.setAvgSellPrice(avgBuyPrice*1.1);
            material.setIntRate(1.1);
            materialMapper.updateById(material);
        });
        return Result.success("ok");
    }
}
