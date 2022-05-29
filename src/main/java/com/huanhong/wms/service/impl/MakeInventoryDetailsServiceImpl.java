package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.MakeInventoryDetails;
import com.huanhong.wms.entity.dto.AddMakeInventoryDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDetailsDTO;
import com.huanhong.wms.mapper.MakeInventoryDetailsMapper;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.service.IMakeInventoryDetailsService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-11
 */
@Service
public class MakeInventoryDetailsServiceImpl extends SuperServiceImpl<MakeInventoryDetailsMapper, MakeInventoryDetails> implements IMakeInventoryDetailsService {

    @Resource
    private MakeInventoryDetailsMapper makeInventoryDetailsMapper;

    @Override
    public Result addMakeInventoryDetails(List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList) {
        List<AddMakeInventoryDetailsDTO> listSuccess = new ArrayList<>();
        List<AddMakeInventoryDetailsDTO> listFalse = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList) {
            List<InventoryInformation> inventoryList = addMakeInventoryDetailsDTO.getInventoryList();
            inventoryList.forEach(list -> {
                addMakeInventoryDetailsDTO.setUnitPrice(list.getUnitPrice());
            });
            MakeInventoryDetails makeInventoryDetails = new MakeInventoryDetails();
            BeanUtil.copyProperties(addMakeInventoryDetailsDTO, makeInventoryDetails);
            int add = makeInventoryDetailsMapper.insert(makeInventoryDetails);
            if (add > 0) {
                listSuccess.add(addMakeInventoryDetailsDTO);
            } else {
                listFalse.add(addMakeInventoryDetailsDTO);
            }
        }
        jsonObject.put("success", listSuccess);

        jsonObject.put("false", listFalse);
        return Result.success(jsonObject);
    }

    @Override
    public Result updateMakeInventoryDetails(List<UpdateMakeInventoryDetailsDTO> updateMakeInventoryDetailsDTOList) {
        List<UpdateMakeInventoryDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateMakeInventoryDetailsDTO> listFalse = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        for (UpdateMakeInventoryDetailsDTO updateMakeInventoryDetailsDTO : updateMakeInventoryDetailsDTOList) {
            MakeInventoryDetails makeInventoryDetailsOld = makeInventoryDetailsMapper.selectById(updateMakeInventoryDetailsDTO.getId());
            BeanUtil.copyProperties(updateMakeInventoryDetailsDTO, makeInventoryDetailsOld);
            int add = makeInventoryDetailsMapper.updateById(makeInventoryDetailsOld);
            if (add > 0) {
                listSuccess.add(updateMakeInventoryDetailsDTO);
            } else {
                listFalse.add(updateMakeInventoryDetailsDTO);
            }
        }
        jsonObject.put("success", listSuccess);
        jsonObject.put("false", listFalse);



        return Result.success(jsonObject);
    }

    @Override
    public MakeInventoryDetails getMakeInventoryDetailsById(Integer id) {
        return makeInventoryDetailsMapper.selectById(id);
    }

    @Override
    public List<MakeInventoryDetails> getMakeInventoryDetailsByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper<MakeInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        return makeInventoryDetailsMapper.selectList(queryWrapper);
    }

    @Override
    public Integer getMakeInventoryDetailsByDocNumAndWarehouseIdNotComplete(String docNum, String warehouseId) {
        QueryWrapper<MakeInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        //状态: 0-待盘点，1-已盘点
        queryWrapper.eq("check_status",0);
        return makeInventoryDetailsMapper.selectCount(queryWrapper);

    }

    @Override
    public List<MakeInventoryDetails> findByDocumentNumber(String documentNumber) {
        QueryWrapper<MakeInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number",documentNumber);
        return makeInventoryDetailsMapper.selectList(queryWrapper);
    }
}
