package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.mapper.MakeInventoryReportDetailsMapper;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.IMakeInventoryReportDetailsService;
import com.huanhong.wms.SuperServiceImpl;
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
 * @since 2022-05-12
 */
@Service
public class MakeInventoryReportDetailsServiceImpl extends SuperServiceImpl<MakeInventoryReportDetailsMapper, MakeInventoryReportDetails> implements IMakeInventoryReportDetailsService {

    @Resource
    private MakeInventoryReportDetailsMapper makeInventoryReportDetailsMapper;

    @Override
    public Result addMakeInventoryReportDetailsList(List<AddMakeInventoryReportDetailsDTO> addMakeInventoryReportDetailsDTOList) {
        List<AddMakeInventoryReportDetailsDTO> listSuccess = new ArrayList<>();
        List<AddMakeInventoryReportDetailsDTO> listFalse = new ArrayList<>();
        MakeInventoryReportDetails makeInventoryReportDetails = new MakeInventoryReportDetails();
        HashMap map = new HashMap();
        for (AddMakeInventoryReportDetailsDTO addMakeInventoryReportDetailsDTO : addMakeInventoryReportDetailsDTOList
        ) {
            BeanUtil.copyProperties(addMakeInventoryReportDetailsDTO, makeInventoryReportDetails);
            int add = makeInventoryReportDetailsMapper.insert(makeInventoryReportDetails);
            if (add > 0) {
                listSuccess.add(addMakeInventoryReportDetailsDTO);
            } else {
                listFalse.add(addMakeInventoryReportDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateMakeInventoryReportDetailsList(List<UpdateMakeInventoryReportDetailsDTO> updateMakeInventoryReportDetailsDTOList) {
        List<UpdateMakeInventoryReportDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateMakeInventoryReportDetailsDTO> listFalse = new ArrayList<>();
        MakeInventoryReportDetails makeInventoryReportDetailsOld = new MakeInventoryReportDetails();
        HashMap map = new HashMap();
        for (UpdateMakeInventoryReportDetailsDTO updateMakeInventoryReportDetailsDTO : updateMakeInventoryReportDetailsDTOList
        ) {
            makeInventoryReportDetailsOld = getMakeInventoryReportDetailsById(updateMakeInventoryReportDetailsDTO.getId());
            BeanUtil.copyProperties(updateMakeInventoryReportDetailsDTO, makeInventoryReportDetailsOld);
            int update = makeInventoryReportDetailsMapper.updateById(makeInventoryReportDetailsOld);
            if (update > 0) {
                listSuccess.add(updateMakeInventoryReportDetailsDTO);
            } else {
                listFalse.add(updateMakeInventoryReportDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public List<MakeInventoryReportDetails> getMakeInventoryReportDetailsByDocNumberAndWarehosueId(String documentNumber, String warehouseId) {
        QueryWrapper<MakeInventoryReportDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.eq("report_number", documentNumber);
        return makeInventoryReportDetailsMapper.selectList(queryWrapper);
    }

    @Override
    public MakeInventoryReportDetails getMakeInventoryReportDetailsById(int id) {
        return makeInventoryReportDetailsMapper.selectById(id);
    }

    @Override
    public MakeInventoryReportDetails getMakeInventoryReportDetailsByMaterialCodingAndBatchAndCargoSpaceId(String materialCoding, String batch, String cargoSpaceId) {
        QueryWrapper<MakeInventoryReportDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("batch",batch);
        queryWrapper.eq("cargo_space_id",cargoSpaceId);
        return makeInventoryReportDetailsMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer getMakeInventoryReportDetailsByDocNumAndWarehouseIdNotComplete(String docNum, String warehouseId) {
        QueryWrapper<MakeInventoryReportDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        //状态: 0-待盘点，1-已盘点
        queryWrapper.eq("check_status",0);
        return makeInventoryReportDetailsMapper.selectCount(queryWrapper);
    }
}
