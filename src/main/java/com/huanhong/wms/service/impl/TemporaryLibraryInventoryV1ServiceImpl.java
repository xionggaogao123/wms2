package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouseDetails;
import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.entity.WarehouseManager;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDetailsDTO;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseDetailsMapper;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseMapper;
import com.huanhong.wms.mapper.WarehouseManagerMapper;
import com.huanhong.wms.service.ITemporaryLibraryInventoryDetailsService;
import com.huanhong.wms.service.ITemporaryLibraryInventoryService;
import com.huanhong.wms.service.TemporaryLibraryInventoryV1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 9:44
 */
@Slf4j
@Service
public class TemporaryLibraryInventoryV1ServiceImpl implements TemporaryLibraryInventoryV1Service {

    @Resource
    private ITemporaryLibraryInventoryService temporaryLibraryInventoryService;

    @Resource
    private WarehouseManagerMapper warehouseManager;

    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Resource
    private ITemporaryLibraryInventoryDetailsService temporaryLibraryInventoryDetailsService;

    @Resource
    private TemporaryEnterWarehouseDetailsMapper temporaryEnterWarehouseDetailsMapper;

    @Override
    public Result addTemporaryMainAndSublistAndWarehouse(AddTemporaryLibraryInventoryAndDetailsDTO addTemporaryLibraryInventoryAndDetailsDTO) {
        //新增临时清点主表
        Result resultInventory = temporaryLibraryInventoryService.addTemporaryLibraryInventory(addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDTO());
        if (!resultInventory.isOk()) {
            return Result.failure("新增点验单失败！");
        }
        //获取新增临时清点主表数据
        TemporaryLibraryInventory temporaryLibraryInventory = (TemporaryLibraryInventory) resultInventory.getData();
        log.info("新增临时清点主表数据为:{}", JsonUtil.obj2String(temporaryLibraryInventory));
        //查询仓库对应的管理员信息
        List<WarehouseManager> warehouseManagers = selectWarehouseManager(temporaryLibraryInventory.getWarehouseId());
        log.info("获取管理对应的库管数据为:{}", JsonUtil.obj2String(warehouseManagers));
        //添加临时清点入库主表信息
        TemporaryEnterWarehouse temporaryEnterWarehouse = addTemporaryEnterWarehouse(temporaryLibraryInventory, warehouseManagers);
        //添加临时入库子表数据
        addTemporaryEnterWarehouseDetail(addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList(),temporaryEnterWarehouse);
        //添加临时清点子表信息
        List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList();
        addTemporaryLibraryInventoryDetailsDTOList.forEach(details->{
            details.setDocumentNumber(temporaryLibraryInventory.getDocumentNumber());
        });
        return temporaryLibraryInventoryDetailsService.addInventoryDocumentDetailsList(addTemporaryLibraryInventoryDetailsDTOList);
    }

    /**
     * 添加临时入库子表数据
     * @param addTemporaryLibraryInventoryDetailsDTOList 子表数据
     * @param temporaryEnterWarehouse 主表数据
     */
    private void addTemporaryEnterWarehouseDetail(List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList,TemporaryEnterWarehouse temporaryEnterWarehouse) {
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = BeanUtil.copyToList(addTemporaryLibraryInventoryDetailsDTOList, TemporaryEnterWarehouseDetails.class);
        log.info("添加临时子表数据为:{}",JsonUtil.obj2String(temporaryEnterWarehouseDetails));
        temporaryEnterWarehouseDetails.forEach(details->{
            details.setEnterNumber(temporaryEnterWarehouse.getEnterNumber());
            temporaryEnterWarehouseDetailsMapper.insert(details);
        });
    }

    /**
     * 添加临时入库主表数据并返回
     * @param temporaryLibraryInventory 临时清点主表数据
     * @param warehouseManagers 仓库管理信息
     * @return 返回数据
     */
    private TemporaryEnterWarehouse addTemporaryEnterWarehouse(TemporaryLibraryInventory temporaryLibraryInventory, List<WarehouseManager> warehouseManagers) {
        TemporaryEnterWarehouse temporaryEnterWarehouse = new TemporaryEnterWarehouse();
        BeanUtil.copyProperties(temporaryLibraryInventory,temporaryEnterWarehouse);
        temporaryEnterWarehouse.setId(null);
        //遍历赋值
        ArrayList<String> arrayList = new ArrayList<>();
        warehouseManagers.forEach(message->{
            arrayList.add(message.getLoginName());
        });
        temporaryEnterWarehouse.setWarehouse(String.valueOf(arrayList));
        temporaryEnterWarehouse.setEnterNumber("LKRK"+String.valueOf(System.currentTimeMillis()));
        temporaryEnterWarehouse.setEnterTime(temporaryLibraryInventory.getLastUpdate());
        log.info("添加临时入库主表数据为:{}",JsonUtil.obj2String(temporaryEnterWarehouse));
        temporaryEnterWarehouseMapper.insert(temporaryEnterWarehouse);
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number",temporaryLibraryInventory.getDocumentNumber());
        return temporaryEnterWarehouseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询仓库对应的管理员信息
     * @param warehouseId 仓库id
     * @return 对应管理员数据
     */
    private List<WarehouseManager> selectWarehouseManager(String warehouseId) {
        QueryWrapper<WarehouseManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id",warehouseId);
        return warehouseManager.selectList(queryWrapper);
    }
}
