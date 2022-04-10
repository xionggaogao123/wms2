package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ArrivalVerificationDetails;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.dto.AddArrivalVerificationDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateArrivalVerificationDetailsDTO;
import com.huanhong.wms.mapper.ArrivalVerificationDetailsMapper;
import com.huanhong.wms.service.IArrivalVerificationDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 到货检验明细表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-23
 */
@Service
public class ArrivalVerificationDetailsServiceImpl extends SuperServiceImpl<ArrivalVerificationDetailsMapper, ArrivalVerificationDetails> implements IArrivalVerificationDetailsService {

    @Resource
    private ArrivalVerificationDetailsMapper arrivalVerificationDetailsMapper;

    @Override
    public Result addArrivalVerificationDetails(List<AddArrivalVerificationDetailsDTO> addArrivalVerificationDetailsDTOList) {
        List<AddArrivalVerificationDetailsDTO> listSuccess = new ArrayList<>();
        List<AddArrivalVerificationDetailsDTO> listFalse = new ArrayList<>();
        ArrivalVerificationDetails arrivalVerificationDetails = new ArrivalVerificationDetails();
        HashMap map = new HashMap();
        for (AddArrivalVerificationDetailsDTO addArrivalVerificationDetailsDTO : addArrivalVerificationDetailsDTOList
        ) {
            BeanUtil.copyProperties(addArrivalVerificationDetailsDTO, arrivalVerificationDetails);
            int add = arrivalVerificationDetailsMapper.insert(arrivalVerificationDetails);
            if (add > 0) {
                listSuccess.add(addArrivalVerificationDetailsDTO);
            } else {
                listFalse.add(addArrivalVerificationDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateArrivalVerificationDetails(List<UpdateArrivalVerificationDetailsDTO> updateArrivalVerificationDetailsDTOList) {
        List<UpdateArrivalVerificationDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateArrivalVerificationDetailsDTO> listFalse = new ArrayList<>();
        ArrivalVerificationDetails arrivalVerificationDetailsOld = new ArrivalVerificationDetails();
        HashMap map = new HashMap();
        for (UpdateArrivalVerificationDetailsDTO updateArrivalVerificationDetailsDTO : updateArrivalVerificationDetailsDTOList
        ) {
            arrivalVerificationDetailsOld = getArrivalVerificationDetailsById(updateArrivalVerificationDetailsDTO.getId());
            BeanUtil.copyProperties(updateArrivalVerificationDetailsDTO, arrivalVerificationDetailsOld);
            int update = arrivalVerificationDetailsMapper.updateById(arrivalVerificationDetailsOld);
            if (update > 0) {
                listSuccess.add(updateArrivalVerificationDetailsDTO);
            } else {
                listFalse.add(updateArrivalVerificationDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public ArrivalVerificationDetails getArrivalVerificationDetailsById(Integer id) {
        return arrivalVerificationDetailsMapper.selectById(id);
    }

    @Override
    public List<ArrivalVerificationDetails> getArrivalVerificationDetailsByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper<ArrivalVerificationDetails> queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        return arrivalVerificationDetailsMapper.selectList(queryWrapper);
    }

    @Override
    public List<ArrivalVerificationDetails> getArrivalVerificationDetailsListByMaterialCodeAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<ArrivalVerificationDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("document_number");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.eq("verification_status", 0).or().eq("verification_status", 1);
        return arrivalVerificationDetailsMapper.selectList(queryWrapper);
    }
}
