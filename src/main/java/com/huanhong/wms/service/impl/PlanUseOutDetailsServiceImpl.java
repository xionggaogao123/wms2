package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddPlanUseOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDetailsDTO;
import com.huanhong.wms.mapper.PlanUseOutDetailsMapper;
import com.huanhong.wms.service.IPlanUseOutDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 计划领用明细表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-15
 */
@Service
public class PlanUseOutDetailsServiceImpl extends SuperServiceImpl<PlanUseOutDetailsMapper, PlanUseOutDetails> implements IPlanUseOutDetailsService {

    @Resource
    private PlanUseOutDetailsMapper planUseOutDetailsMapper;

    /**
     * 添加领料出库明细
     * @param listAddDto
     * @return
     */
    @Override
    public Result addPlanUseOutDetails(List<AddPlanUseOutDetailsDTO> listAddDto) {
        PlanUseOutDetails planUseOutDetails = new PlanUseOutDetails();
        try {
            for (int i = 0; i<listAddDto.size(); i++){
                BeanUtil.copyProperties(listAddDto.get(i),planUseOutDetails);
                planUseOutDetailsMapper.insert(planUseOutDetails);
            }
            return Result.success();
        }catch (Exception e){
            log.error("明细插入失败！异常：",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "明细新增失败！");
        }
    }

    /**
     * updatePlanUseOutDetailsDTO
     * @param updatePlanUseOutDetailsDTOList
     * @return
     */
    @Override
    public Result updatePlanUseOutDetails(List<UpdatePlanUseOutDetailsDTO> updatePlanUseOutDetailsDTOList) {
        List<PlanUseOutDetails> listSuccess = new ArrayList<>();
        List<PlanUseOutDetails> listFalse = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        for (UpdatePlanUseOutDetailsDTO updatePlanUseOutDetailsDTO : updatePlanUseOutDetailsDTOList) {
            PlanUseOutDetails planUseOutDetails = new PlanUseOutDetails();
            BeanUtil.copyProperties(updatePlanUseOutDetailsDTO,planUseOutDetails);
            int update = planUseOutDetailsMapper.updateById(planUseOutDetails);
            if (update>0){
                listSuccess.add(planUseOutDetails);
            }else {
                listFalse.add(planUseOutDetails);
            }
        }
        jsonObject.put("success",listSuccess);
        jsonObject.put("false",listFalse);
        return Result.success(jsonObject);
    }

    @Override
    public Result updatePlanUseOutDetails(UpdatePlanUseOutDetailsDTO updatePlanUseOutDetailsDTO) {
        PlanUseOutDetails planUseOutDetails = new PlanUseOutDetails();
        BeanUtil.copyProperties(updatePlanUseOutDetailsDTO,planUseOutDetails);
        int update = planUseOutDetailsMapper.updateById(planUseOutDetails);
        return update>0 ? Result.success() : Result.failure("更新失败");
    }

    /**
     *
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    @Override
    public List<PlanUseOutDetails> getListPlanUseOutDetailsByDocNumberAndWarehosue(String documentNumber, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("use_planning_document_number",documentNumber);
        queryWrapper.eq("warehouse_id",warehouseId);
        List<PlanUseOutDetails> listData = planUseOutDetailsMapper.selectList(queryWrapper);
        return listData;
    }

    /**
     * 区分状态
     * @param documentNumber
     * @param warehouseId
     * @param outStatus
     * @return
     */
    @Override
    public List<PlanUseOutDetails> getListPlanUseOutDetailsByDocNumberAndWarehosueAndOutStatus(String documentNumber, String warehouseId, Integer outStatus) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("use_planning_document_number",documentNumber);
        queryWrapper.eq("warehouse_id",warehouseId);
        queryWrapper.eq("out_status",outStatus);
        List<PlanUseOutDetails> listData = planUseOutDetailsMapper.selectList(queryWrapper);
        return listData;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public PlanUseOutDetails getPlanUseOutDetailsByDetailsId(int id) {
        return planUseOutDetailsMapper.selectById(id);
    }
}
