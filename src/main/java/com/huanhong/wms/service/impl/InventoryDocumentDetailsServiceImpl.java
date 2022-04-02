package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.ProcurementPlanDetails;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.AddProcurementPlanDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateProcurementPlanDetailsDTO;
import com.huanhong.wms.mapper.InventoryDocumentDetailsMapper;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.IInventoryDocumentDetailsService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 清点单 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-02
 */
@Service
public class InventoryDocumentDetailsServiceImpl extends SuperServiceImpl<InventoryDocumentDetailsMapper, InventoryDocumentDetails> implements IInventoryDocumentDetailsService {

    @Resource
    private InventoryDocumentDetailsMapper inventoryDocumentDetailsMapper;

    @Override
    public Result addInventoryDocumentDetailsLis(List<AddInventoryDocumentDetailsDTO> addInventoryDocumentDetailsDTOList) {
        List<AddInventoryDocumentDetailsDTO> listSuccess = new ArrayList<>();
        List<AddInventoryDocumentDetailsDTO> listFalse = new ArrayList<>();
        InventoryDocumentDetails inventoryDocumentDetails = new InventoryDocumentDetails();
        HashMap map = new HashMap();
        for (AddInventoryDocumentDetailsDTO addInventoryDocumentDetailsDTO : addInventoryDocumentDetailsDTOList
        ) {
            BeanUtil.copyProperties(addInventoryDocumentDetailsDTO, inventoryDocumentDetails);
            int add = inventoryDocumentDetailsMapper.insert(inventoryDocumentDetails);
            if (add > 0) {
                listSuccess.add(addInventoryDocumentDetailsDTO);
            } else {
                listFalse.add(addInventoryDocumentDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateInventoryDocumentDetailsList(List<UpdateInventoryDocumentDetailsDTO> updateInventoryDocumentDetailsDTOList) {
        List<UpdateInventoryDocumentDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateInventoryDocumentDetailsDTO> listFalse = new ArrayList<>();
        InventoryDocumentDetails inventoryDocumentDetailsOld = new InventoryDocumentDetails();
        HashMap map = new HashMap();
        for (UpdateInventoryDocumentDetailsDTO updateInventoryDocumentDetailsDTO : updateInventoryDocumentDetailsDTOList
        ) {
            inventoryDocumentDetailsOld = getInventoryDocumentDetailsById(updateInventoryDocumentDetailsDTO.getId());
            BeanUtil.copyProperties(updateInventoryDocumentDetailsDTO, inventoryDocumentDetailsOld);
            int update = inventoryDocumentDetailsMapper.updateById(inventoryDocumentDetailsOld);
            if (update > 0) {
                listSuccess.add(updateInventoryDocumentDetailsDTO);
            } else {
                listFalse.add(updateInventoryDocumentDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public List<InventoryDocumentDetails> getInventoryDocumentDetailsListByDocNumberAndWarehosue(String documentNumber, String warehouse) {
        QueryWrapper<InventoryDocumentDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse", warehouse);
        queryWrapper.eq("document_number", documentNumber);
        return inventoryDocumentDetailsMapper.selectList(queryWrapper);
    }

    @Override
    public InventoryDocumentDetails getInventoryDocumentDetailsById(int id) {
        return inventoryDocumentDetailsMapper.selectById(id);
    }

    /**
     * 根据清点单编号和仓库号获取明细数量
     * @param documentNumber
     * @param warehouseId
     * @param status 0-未完成 1-已完成
     * @return
     */
    @Override
    public Integer getCompleteNum(String documentNumber, String warehouseId,Integer status) {
        QueryWrapper<InventoryDocumentDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", documentNumber);
        queryWrapper.eq("warehouse", warehouseId);
        queryWrapper.eq("complete", status);
        return inventoryDocumentDetailsMapper.selectCount(queryWrapper);
    }
}
