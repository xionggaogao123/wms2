package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import com.huanhong.common.exception.ServiceException;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.MaterialPrice;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.MaterialMapper;
import com.huanhong.wms.mapper.MaterialPriceMapper;
import com.huanhong.wms.service.MaterialPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/30 12:18
 */
@Slf4j
@Service
public class MaterialPriceServiceImpl implements MaterialPriceService {

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private MaterialPriceMapper materialPriceMapper;

    @Override
    public void addMaterialPrice(String materialCoding, String materialName,String warehouseId) {

        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("material_name", materialName);
        queryWrapper.eq("warehouse_id", warehouseId);
        List<InventoryInformation> inventoryInformationList = inventoryInformationMapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(inventoryInformationList)){
            throw new ServiceException(500,"暂无数据");
        }
        log.info("查询的物料信息:{}", JsonUtil.obj2String(inventoryInformationList));
        inventoryInformationList.forEach(list -> {
            QueryWrapper<MaterialPrice> priceQueryWrapper = new QueryWrapper<>();
            priceQueryWrapper.eq("material_coding",list.getMaterialCoding());
            priceQueryWrapper.eq("material_name", list.getMaterialName());
            priceQueryWrapper.eq("warehouse_id", list.getWarehouseId());
            MaterialPrice price = materialPriceMapper.selectOne(priceQueryWrapper);
            MaterialPrice materialPrice = new MaterialPrice();
            AtomicDouble atomicDouble = new AtomicDouble();
            AtomicDouble amount = new AtomicDouble();
            AtomicDouble managementFeeRate = new AtomicDouble();
            if(price == null){
                BigDecimal unitPrice = list.getUnitPrice();
                Double inventoryCredit = list.getInventoryCredit();
                materialPrice.setWarehouseName(list.getWarehouseId());
                materialPrice.setWarehouseId(list.getWarehouseId());
                //求 库存表中的某种物料每个批次的单价*对应批次的数量
                BigDecimal multiply = unitPrice.multiply(new BigDecimal(inventoryCredit));
                log.info("数据为:{}",inventoryCredit+"====="+multiply);
                managementFeeRate.getAndSet(list.getManagementFeeRate());
                amount.addAndGet(multiply.doubleValue());
                atomicDouble.addAndGet(inventoryCredit);
                materialPrice.setMaterialName(materialName);
                materialPrice.setMaterialCoding(materialCoding);
                //总数量
                BigDecimal bigDecimal = new BigDecimal(atomicDouble.get());
                //(物料数量*单价)
                BigDecimal bigDecimal1 = new BigDecimal(amount.get());
                // (物料数量*单价)/总数量
                BigDecimal avgBuyPrice = bigDecimal1.divide(bigDecimal,15,BigDecimal.ROUND_HALF_UP);
                //(物料数量*单价)/总数量 * 费率
                BigDecimal avgSellPrice = avgBuyPrice.multiply(new BigDecimal(managementFeeRate.get()),new MathContext(15));
                materialPrice.setSellRate(new BigDecimal(managementFeeRate.get()));
                materialPrice.setAvgBuyPrice(avgBuyPrice);
                materialPrice.setAvgSellPrice(avgSellPrice);
                materialPrice.setCreateTime(LocalDateTime.now());
                materialPriceMapper.insert(materialPrice);
            }else{
                BigDecimal bigDecimal = new BigDecimal(atomicDouble.get());
                BigDecimal bigDecimal1 = new BigDecimal(amount.get());
                BigDecimal avgBuyPrice = bigDecimal1.divide(bigDecimal,15,BigDecimal.ROUND_HALF_UP);
                BigDecimal avgSellPrice = avgBuyPrice.multiply(new BigDecimal(managementFeeRate.get()),new MathContext(15));
                price.setSellRate(new BigDecimal(managementFeeRate.get()));
                price.setAvgBuyPrice(avgBuyPrice);
                price.setAvgSellPrice(avgSellPrice);
                materialPrice.setLastUpdate(LocalDateTime.now());
                materialPriceMapper.updateById(price);
            }
        });
    }
}
