package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.TemporaryLibraryInventoryDetails;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryInventoryDetailsDTO;
import com.huanhong.wms.mapper.TemporaryLibraryInventoryDetailsMapper;
import com.huanhong.wms.service.ITemporaryLibraryInventoryDetailsService;
import com.huanhong.wms.SuperServiceImpl;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-06
 */
@Service
public class TemporaryLibraryInventoryDetailsServiceImpl extends SuperServiceImpl<TemporaryLibraryInventoryDetailsMapper, TemporaryLibraryInventoryDetails> implements ITemporaryLibraryInventoryDetailsService {

    @Resource
    private TemporaryLibraryInventoryDetailsMapper temporaryLibraryInventoryDetailsMapper;

    @Override
    public Result addInventoryDocumentDetailsList(List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList) {
        List<AddTemporaryLibraryInventoryDetailsDTO> listSuccess = new ArrayList<>();
        List<AddTemporaryLibraryInventoryDetailsDTO> listFalse = new ArrayList<>();
        TemporaryLibraryInventoryDetails temporaryLibraryInventoryDetails = new TemporaryLibraryInventoryDetails();
        HashMap map = new HashMap();
        for (AddTemporaryLibraryInventoryDetailsDTO addTemporaryLibraryInventoryDetailsDTO : addTemporaryLibraryInventoryDetailsDTOList
        ) {
            BeanUtil.copyProperties(addTemporaryLibraryInventoryDetailsDTO, temporaryLibraryInventoryDetails);
            int add = temporaryLibraryInventoryDetailsMapper.insert(temporaryLibraryInventoryDetails);
            if (add > 0) {
                listSuccess.add(addTemporaryLibraryInventoryDetailsDTO);
            } else {
                listFalse.add(addTemporaryLibraryInventoryDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateTemporaryLibraryInventoryDetailsList(List<UpdateTemporaryLibraryInventoryDetailsDTO> updateTemporaryLibraryInventoryDetailsDTOList){
        List<UpdateTemporaryLibraryInventoryDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateTemporaryLibraryInventoryDetailsDTO> listFalse = new ArrayList<>();
        TemporaryLibraryInventoryDetails temporaryLibraryInventoryDetailsOld = new TemporaryLibraryInventoryDetails();
        HashMap map = new HashMap();
        for (UpdateTemporaryLibraryInventoryDetailsDTO updateTemporaryLibraryInventoryDetailsDTO : updateTemporaryLibraryInventoryDetailsDTOList
        ) {
            temporaryLibraryInventoryDetailsOld = getTemporaryLibraryInventoryDetailsById(updateTemporaryLibraryInventoryDetailsDTO.getId());
            BeanUtil.copyProperties(updateTemporaryLibraryInventoryDetailsDTO, temporaryLibraryInventoryDetailsOld);
            int update = temporaryLibraryInventoryDetailsMapper.updateById(temporaryLibraryInventoryDetailsOld);
            if (update > 0) {
                listSuccess.add(updateTemporaryLibraryInventoryDetailsDTO);
            } else {
                listFalse.add(updateTemporaryLibraryInventoryDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public List<TemporaryLibraryInventoryDetails> getTemporaryLibraryInventoryDetailsListByDocNumberAndWarehosueId(String documentNumber, String warehouseId) {
        QueryWrapper<TemporaryLibraryInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.eq("document_number", documentNumber);
        return temporaryLibraryInventoryDetailsMapper.selectList(queryWrapper);
    }

    @Override
    public TemporaryLibraryInventoryDetails getTemporaryLibraryInventoryDetailsById(int id) {
        return temporaryLibraryInventoryDetailsMapper.selectById(id);
    }

    @Override
    public Integer getCompleteNum(String documentNumber, String warehouseId, Integer status) {
        QueryWrapper<TemporaryLibraryInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", documentNumber);
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.eq("complete", status);
        return temporaryLibraryInventoryDetailsMapper.selectCount(queryWrapper);
    }

    @Override
    public List<TemporaryLibraryInventoryDetails> getTemporaryLibraryInventoryDetailsListByMaterialCodeAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<TemporaryLibraryInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("document_number");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.eq("complete", 0);
        return temporaryLibraryInventoryDetailsMapper.selectList(queryWrapper);
    }
}
