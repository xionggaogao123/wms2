package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcurementPlanDetails;
import com.huanhong.wms.entity.dto.AddProcurementPlanDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateProcurementPlanDetailsDTO;
import com.huanhong.wms.mapper.ProcurementPlanDetailsMapper;
import com.huanhong.wms.service.IProcurementPlanDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 采购计划明细表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-21
 */
@Service
public class ProcurementPlanDetailsServiceImpl extends SuperServiceImpl<ProcurementPlanDetailsMapper, ProcurementPlanDetails> implements IProcurementPlanDetailsService {

    @Resource
    private ProcurementPlanDetailsMapper procurementPlanDetailsMapper;

    @Override
    public Result addProcurementPlanDetails(List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList) {
        List<AddProcurementPlanDetailsDTO> listSuccess = new ArrayList<>();
        List<AddProcurementPlanDetailsDTO> listFalse = new ArrayList<>();
        ProcurementPlanDetails procurementPlanDetails = new ProcurementPlanDetails();
        HashMap map = new HashMap();
        for (AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTO : addProcurementPlanDetailsDTOList
        ) {
            BeanUtil.copyProperties(addProcurementPlanDetailsDTO, procurementPlanDetails);
            int add = procurementPlanDetailsMapper.insert(procurementPlanDetails);
            if (add > 0) {
                listSuccess.add(addProcurementPlanDetailsDTO);
            } else {
                listFalse.add(addProcurementPlanDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateProcurementPlanDetails(List<UpdateProcurementPlanDetailsDTO> updateProcurementPlanDetailsDTOList) {
        List<UpdateProcurementPlanDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateProcurementPlanDetailsDTO> listFalse = new ArrayList<>();
        ProcurementPlanDetails procurementPlanDetailsOld = new ProcurementPlanDetails();
        HashMap map = new HashMap();
        for (UpdateProcurementPlanDetailsDTO updateProcurementPlanDetailsDTO : updateProcurementPlanDetailsDTOList
        ) {
            procurementPlanDetailsOld = getProcurementPlanDetailsById(updateProcurementPlanDetailsDTO.getId());
            BeanUtil.copyProperties(updateProcurementPlanDetailsDTO, procurementPlanDetailsOld);
            int update = procurementPlanDetailsMapper.updateById(procurementPlanDetailsOld);
            if (update > 0) {
                listSuccess.add(updateProcurementPlanDetailsDTO);
            } else {
                listFalse.add(updateProcurementPlanDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public ProcurementPlanDetails getProcurementPlanDetailsById(Integer id) {
        return procurementPlanDetailsMapper.selectById(id);
    }

    @Override
    public List<ProcurementPlanDetails> getProcurementPlanDetailsByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper<ProcurementPlanDetails> queryWrapper = new QueryWrapper();
        queryWrapper.eq("plan_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        return procurementPlanDetailsMapper.selectList(queryWrapper);
    }
}
