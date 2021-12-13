package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-25
 */
@Service
public class InventoryInformationServiceImpl extends SuperServiceImpl<InventoryInformationMapper, InventoryInformation> implements IInventoryInformationService {


    private  InventoryInformationMapper inventoryInformationMapper;

    /**
     * 根据物料编码和批次更新库存信息
     */
    @Override
    public int updateInventoryInformation(InventoryInformation inventoryInformation) {
        UpdateWrapper<InventoryInformation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("material_coding", inventoryInformation.getMaterialCoding());
        updateWrapper.eq("Batch",inventoryInformation.getBatch());
        int i = inventoryInformationMapper.update(inventoryInformation,updateWrapper);
        return i;
    }
}
