package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationOutDetails;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.dto.AddAllocationOutDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationPlanDetailDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationPlanDetailDTO;
import com.huanhong.wms.mapper.AllocationOutDetailsMapper;
import com.huanhong.wms.mapper.AllocationOutMapper;
import com.huanhong.wms.service.IAllocationOutDetailsService;
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
 * @since 2022-03-30
 */
@Service
public class AllocationOutDetailsServiceImpl extends SuperServiceImpl<AllocationOutDetailsMapper, AllocationOutDetails> implements IAllocationOutDetailsService {


    @Resource
    private  AllocationOutDetailsMapper allocationOutDetailsMapper;

    @Override
    public Result addAllocationOutDetails(List<AddAllocationOutDetailsDTO> addAllocationOutDetailsDTOList) {
        List<AddAllocationOutDetailsDTO> listSuccess = new ArrayList<>();
        List<AddAllocationOutDetailsDTO> listFalse = new ArrayList<>();
        AllocationOutDetails allocationOutDetails = new AllocationOutDetails();
        HashMap map = new HashMap();
        for (AddAllocationOutDetailsDTO addAllocationOutDetailsDTO : addAllocationOutDetailsDTOList
        ) {
            BeanUtil.copyProperties(addAllocationOutDetailsDTO, allocationOutDetails);
            int add = allocationOutDetailsMapper.insert(allocationOutDetails);
            if (add > 0) {
                listSuccess.add(addAllocationOutDetailsDTO);
            } else {
                listFalse.add(addAllocationOutDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateAllocationOutDetails(List<UpdateAllocationOutDetailsDTO> updateAllocationOutDetailsDTOList) {
        List<UpdateAllocationOutDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateAllocationOutDetailsDTO> listFalse = new ArrayList<>();
        AllocationOutDetails allocationOutDetailsOld = new AllocationOutDetails();
        HashMap map = new HashMap();
        for (UpdateAllocationOutDetailsDTO updateAllocationOutDetailsDTO : updateAllocationOutDetailsDTOList
        ) {
            allocationOutDetailsOld = getAllocationOutDetailsById(updateAllocationOutDetailsDTO.getId());
            BeanUtil.copyProperties(updateAllocationOutDetailsDTO, allocationOutDetailsOld);
            int update = allocationOutDetailsMapper.updateById(allocationOutDetailsOld);
            if (update > 0) {
                listSuccess.add(updateAllocationOutDetailsDTO);
            } else {
                listFalse.add(updateAllocationOutDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public AllocationOutDetails getAllocationOutDetailsById(Integer id) {
        return allocationOutDetailsMapper.selectById(id);
    }

    @Override
    public List<AllocationOutDetails> getAllocationOutDetailsListByDocNum(String docNum) {
        QueryWrapper<AllocationOutDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("allocation_out_number", docNum);
        return allocationOutDetailsMapper.selectList(queryWrapper);
    }
}
