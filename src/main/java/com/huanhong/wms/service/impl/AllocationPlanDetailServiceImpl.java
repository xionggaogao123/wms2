package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.mapper.AllocationPlanDetailMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IAllocationPlanDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 调拨计划明细表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-29
 */
@Service
public class AllocationPlanDetailServiceImpl extends SuperServiceImpl<AllocationPlanDetailMapper, AllocationPlanDetail> implements IAllocationPlanDetailService {

    @Resource
    private AllocationPlanDetailMapper allocationPlanDetailMapper;

    @Override
    public Result addAllocationPlanDetails(List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList) {
        List<AddAllocationPlanDetailDTO> listSuccess = new ArrayList<>();
        List<AddAllocationPlanDetailDTO> listFalse = new ArrayList<>();
        AllocationPlanDetail allocationPlanDetail = new AllocationPlanDetail();
        HashMap map = new HashMap();
        for (AddAllocationPlanDetailDTO addAllocationPlanDetailDTO : addAllocationPlanDetailDTOList
        ) {
            BeanUtil.copyProperties(addAllocationPlanDetailDTO, allocationPlanDetail);
            int add = allocationPlanDetailMapper.insert(allocationPlanDetail);
            if (add > 0) {
                listSuccess.add(addAllocationPlanDetailDTO);
            } else {
                listFalse.add(addAllocationPlanDetailDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateAllocationPlanDetails(List<UpdateAllocationPlanDetailDTO> updateAllocationPlanDetailDTOList) {
        List<UpdateAllocationPlanDetailDTO> listSuccess = new ArrayList<>();
        List<UpdateAllocationPlanDetailDTO> listFalse = new ArrayList<>();
        AllocationPlanDetail allocationPlanDetailOld = new AllocationPlanDetail();
        HashMap map = new HashMap();
        for (UpdateAllocationPlanDetailDTO updateAllocationPlanDetailDTO : updateAllocationPlanDetailDTOList
        ) {
            allocationPlanDetailOld = getAllocationPlanDetailById(updateAllocationPlanDetailDTO.getId());
            BeanUtil.copyProperties(updateAllocationPlanDetailDTO, allocationPlanDetailOld);
            int update = allocationPlanDetailMapper.updateById(allocationPlanDetailOld);
            if (update > 0) {
                listSuccess.add(updateAllocationPlanDetailDTO);
            } else {
                listFalse.add(updateAllocationPlanDetailDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public AllocationPlanDetail getAllocationPlanDetailById(Integer id) {
        return allocationPlanDetailMapper.selectById(id);
    }

    @Override
    public List<AllocationPlanDetail> getAllocationPlanDetailsListByDocNum(String docNum) {
        QueryWrapper<AllocationPlanDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("allocation_number", docNum);
        return allocationPlanDetailMapper.selectList(queryWrapper);
    }
}
