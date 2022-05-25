package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.exception.BizException;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.mapper.*;
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
    private TemporaryLibraryInventoryMapper temporaryLibraryInventoryMapper;

    @Resource
    private TemporaryLibraryInventoryDetailsMapper temporaryLibraryInventoryDetailsMapper;

    @Resource
    private WarehouseManagerMapper warehouseManager;

    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Resource
    private ITemporaryLibraryInventoryDetailsService temporaryLibraryInventoryDetailsService;

    @Resource
    private TemporaryEnterWarehouseDetailsMapper temporaryEnterWarehouseDetailsMapper;

    /**
     * 添加临时清点单子表数据和主表数据 添加临时入库数据
     *
     * @param addTemporaryLibraryInventoryAndDetailsDTO 清点单子表数据和主表数据
     * @return 返回值
     */
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
        addTemporaryEnterWarehouseDetail(addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList(), temporaryEnterWarehouse);
        //添加临时清点子表信息
        List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList();
        addTemporaryLibraryInventoryDetailsDTOList.forEach(details -> {
            details.setDocumentNumber(temporaryLibraryInventory.getDocumentNumber());
        });
        return temporaryLibraryInventoryDetailsService.addInventoryDocumentDetailsList(addTemporaryLibraryInventoryDetailsDTOList);
    }

    /**
     * 更新临时清点数据主表和子表数据 修改临时入库数据
     *
     * @param update
     * @return
     */
    @Override
    public Result updateTemporaryMainAndSublistAndWarehouse(UpdateTemporaryLibraryInventoryAndDetailsDTO update) {
        TemporaryLibraryInventory temporaryLibraryInventory = update.getTemporaryLibraryInventory();
        TemporaryLibraryInventory temporaryLibraryInventory1 = temporaryLibraryInventoryMapper.selectById(temporaryLibraryInventory.getId());
        if (temporaryLibraryInventory1 == null) {
            throw new BizException("临时清单数据不存在");
        }
        temporaryLibraryInventoryMapper.updateById(temporaryLibraryInventory);
        //修改临时入库表
        String enterNumber = updateTemporaryEnterWarehouse(temporaryLibraryInventory);
        List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails = update.getTemporaryLibraryInventoryDetails();
        //修改临时入库子表数据
        updateTemporaryEnterWarehouseDetails(temporaryLibraryInventoryDetails, enterNumber);
        //修改临时清单子表数据
        temporaryLibraryInventoryDetails.forEach(details -> {
            temporaryLibraryInventoryDetailsMapper.updateById(details);
        });

        return Result.success("修改成功");
    }

    private void updateTemporaryEnterWarehouseDetails(List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails, String enterNumber) {
        QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enter_number", enterNumber);
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);
        temporaryEnterWarehouseDetails.forEach(details1 -> {
            temporaryLibraryInventoryDetails.forEach(details2 -> {
                details1.setMaterialCoding(details2.getMaterialCoding());
                details1.setReceivableQuantity(details2.getReceivableQuantity());
                details1.setBatch(details2.getBatch());
                details1.setWarehouseId(details2.getWarehouseId());
                details1.setComplete(details2.getComplete());
                details1.setRemark(details2.getRemark());
                details1.setCreateTime(details2.getCreateTime());
                details1.setLastUpdate(details2.getLastUpdate());
                temporaryEnterWarehouseDetailsMapper.updateById(details1);
            });
        });
    }

    /**
     * 修改临时入库主表信息
     *
     * @param temporaryLibraryInventory 临时清点数据
     */
    private String updateTemporaryEnterWarehouse(TemporaryLibraryInventory temporaryLibraryInventory) {
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", temporaryLibraryInventory.getDocumentNumber());
        TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectOne(queryWrapper);
        temporaryEnterWarehouse.setWarehouseId(temporaryLibraryInventory.getWarehouseId());
        temporaryEnterWarehouse.setEnterTime(temporaryLibraryInventory.getCreateTime());
        temporaryEnterWarehouse.setRemark(temporaryLibraryInventory.getRemark());
        temporaryEnterWarehouseMapper.updateById(temporaryEnterWarehouse);
        return temporaryEnterWarehouse.getEnterNumber();
    }

    /**
     * 添加临时入库子表数据
     *
     * @param addTemporaryLibraryInventoryDetailsDTOList 子表数据
     * @param temporaryEnterWarehouse                    主表数据
     */
    private void addTemporaryEnterWarehouseDetail(List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList, TemporaryEnterWarehouse temporaryEnterWarehouse) {
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = BeanUtil.copyToList(addTemporaryLibraryInventoryDetailsDTOList, TemporaryEnterWarehouseDetails.class);
        log.info("添加临时子表数据为:{}", JsonUtil.obj2String(temporaryEnterWarehouseDetails));
        temporaryEnterWarehouseDetails.forEach(details -> {
            details.setEnterNumber(temporaryEnterWarehouse.getEnterNumber());
            temporaryEnterWarehouseDetailsMapper.insert(details);
        });
    }

    /**
     * 添加临时入库主表数据并返回
     *
     * @param temporaryLibraryInventory 临时清点主表数据
     * @param warehouseManagers         仓库管理信息
     * @return 返回数据
     */
    private TemporaryEnterWarehouse addTemporaryEnterWarehouse(TemporaryLibraryInventory temporaryLibraryInventory, List<WarehouseManager> warehouseManagers) {
        TemporaryEnterWarehouse temporaryEnterWarehouse = new TemporaryEnterWarehouse();
        BeanUtil.copyProperties(temporaryLibraryInventory, temporaryEnterWarehouse);
        temporaryEnterWarehouse.setId(null);
        //遍历赋值
        ArrayList<String> arrayList = new ArrayList<>();
        warehouseManagers.forEach(message -> {
            arrayList.add(message.getLoginName());
        });
        temporaryEnterWarehouse.setWarehouse(String.valueOf(arrayList));
        temporaryEnterWarehouse.setEnterNumber("LKRK" + String.valueOf(System.currentTimeMillis()));
        temporaryEnterWarehouse.setEnterTime(temporaryLibraryInventory.getLastUpdate());
        log.info("添加临时入库主表数据为:{}", JsonUtil.obj2String(temporaryEnterWarehouse));
        temporaryEnterWarehouseMapper.insert(temporaryEnterWarehouse);
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", temporaryLibraryInventory.getDocumentNumber());
        return temporaryEnterWarehouseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询仓库对应的管理员信息
     *
     * @param warehouseId 仓库id
     * @return 对应管理员数据
     */
    private List<WarehouseManager> selectWarehouseManager(String warehouseId) {
        QueryWrapper<WarehouseManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id", warehouseId);
        return warehouseManager.selectList(queryWrapper);
    }
}
