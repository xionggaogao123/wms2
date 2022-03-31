package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationEnterDetails;
import com.huanhong.wms.entity.AllocationOutDetails;
import com.huanhong.wms.entity.dto.AddAllocationEnterDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationEnterDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDetailsDTO;
import com.huanhong.wms.mapper.AllocationEnterDetailsMapper;
import com.huanhong.wms.service.IAllocationEnterDetailsService;
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
 * @since 2022-03-31
 */
@Service
public class AllocationEnterDetailsServiceImpl extends SuperServiceImpl<AllocationEnterDetailsMapper, AllocationEnterDetails> implements IAllocationEnterDetailsService {

    @Resource
    private AllocationEnterDetailsMapper allocationEnterDetailsMapper;


    @Override
    public Result addAllocationEnterDetails(List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList) {
        List<AddAllocationEnterDetailsDTO> listSuccess = new ArrayList<>();
        List<AddAllocationEnterDetailsDTO> listFalse = new ArrayList<>();
        AllocationEnterDetails allocationEnterDetails = new AllocationEnterDetails();
        HashMap map = new HashMap();
        for (AddAllocationEnterDetailsDTO addAllocationEnterDetailsDTO : addAllocationEnterDetailsDTOList
        ) {
            BeanUtil.copyProperties(addAllocationEnterDetailsDTO, allocationEnterDetails);
            int add = allocationEnterDetailsMapper.insert(allocationEnterDetails);
            if (add > 0) {
                listSuccess.add(addAllocationEnterDetailsDTO);
            } else {
                listFalse.add(addAllocationEnterDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateAllocationEnterDetails(List<UpdateAllocationEnterDetailsDTO> updateAllocationEnterDetailsDTOList) {
        List<UpdateAllocationEnterDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateAllocationEnterDetailsDTO> listFalse = new ArrayList<>();
        AllocationEnterDetails allocationEnterDetailsOld = new AllocationEnterDetails();
        HashMap map = new HashMap();
        for (UpdateAllocationEnterDetailsDTO updateAllocationEnterDetailsDTO : updateAllocationEnterDetailsDTOList
        ) {
            allocationEnterDetailsOld = getAllocationEnterDetailsById(updateAllocationEnterDetailsDTO.getId());
            BeanUtil.copyProperties(updateAllocationEnterDetailsDTO, allocationEnterDetailsOld);
            int update = allocationEnterDetailsMapper.updateById(allocationEnterDetailsOld);
            if (update > 0) {
                listSuccess.add(updateAllocationEnterDetailsDTO);
            } else {
                listFalse.add(updateAllocationEnterDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public AllocationEnterDetails getAllocationEnterDetailsById(Integer id) {
        return allocationEnterDetailsMapper.selectById(id);
    }

    @Override
    public List<AllocationEnterDetails> getAllocationEnterDetailsListByDocNum(String docNum) {
        QueryWrapper<AllocationEnterDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("allocation_enter_number", docNum);
        return allocationEnterDetailsMapper.selectList(queryWrapper);
    }
}
