package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.RequiremetsPlanningDetails;
import com.huanhong.wms.entity.dto.AddRequiremetsPlanningDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateRequiremetsPlanningDetailsDTO;
import com.huanhong.wms.mapper.MaterialMapper;
import com.huanhong.wms.mapper.RequiremetsPlanningDetailsMapper;
import com.huanhong.wms.service.IRequiremetsPlanningDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 需求计划明细表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-16
 */
@Slf4j
@Service
public class RequiremetsPlanningDetailsServiceImpl extends SuperServiceImpl<RequiremetsPlanningDetailsMapper, RequiremetsPlanningDetails> implements IRequiremetsPlanningDetailsService {


    @Resource
    private RequiremetsPlanningDetailsMapper requiremetsPlanningDetailsMapper;
    @Resource
    private MaterialMapper materialMapper;

//    @Override
//    public Page<RequiremetsPlanningDetails> pageFuzzyQuery(Page<RequiremetsPlanningDetails> requiremetsPlanningDetailsPage, RequiremetsPlanningDetailsVO requiremetsPlanningDetailsVO) {
//
//        //新建QueryWrapper对象
//        QueryWrapper<RequiremetsPlanningDetails> query = new QueryWrapper<>();
//
//        //根据id排序
//        query.orderByDesc("id");
//
//        //判断此时的条件对象Vo是否等于空，若等于空，
//        //直接进行selectPage查询
//        if (ObjectUtil.isEmpty(r)) {
//            return requirementsPlanningMapper.selectPage(requirementsPlanningPage, query);
//        }
//
//
//    }

    @Override
    public Result addRequiremetsPlanningDetails(List<AddRequiremetsPlanningDetailsDTO> addRequiremetsPlanningDetailsDTOList) {
        List<AddRequiremetsPlanningDetailsDTO> listSuccess = new ArrayList<>();
        List<AddRequiremetsPlanningDetailsDTO> listFalse = new ArrayList<>();
        RequiremetsPlanningDetails requiremetsPlanningDetails = new RequiremetsPlanningDetails();
        HashMap map = new HashMap();
        for (AddRequiremetsPlanningDetailsDTO addRequiremetsPlanningDetailsDTO : addRequiremetsPlanningDetailsDTOList) {
            BeanUtil.copyProperties(addRequiremetsPlanningDetailsDTO, requiremetsPlanningDetails);
            Material material = materialMapper.selectById(addRequiremetsPlanningDetailsDTO.getMaterialId());
            if(null == material){
                listFalse.add(addRequiremetsPlanningDetailsDTO);
                continue;
            }
            requiremetsPlanningDetails.setMaterialId(addRequiremetsPlanningDetailsDTO.getMaterialId());
            requiremetsPlanningDetails.setMaterialName(material.getMaterialName());
            requiremetsPlanningDetails.setMaterialCoding(material.getMaterialCoding());
            requiremetsPlanningDetails.setCreateTime(LocalDateTime.now());
            log.info("新增管理子表数据为:{}", JsonUtil.obj2String(requiremetsPlanningDetails));
            int add = requiremetsPlanningDetailsMapper.insert(requiremetsPlanningDetails);
            if (add > 0) {
                listSuccess.add(addRequiremetsPlanningDetailsDTO);
            } else {
                listFalse.add(addRequiremetsPlanningDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result updateRequiremetsPlanningDetails(List<UpdateRequiremetsPlanningDetailsDTO> updateRequiremetsPlanningDetailsDTOList) {

        List<UpdateRequiremetsPlanningDetailsDTO> listSuccess = new ArrayList<>();
        List<UpdateRequiremetsPlanningDetailsDTO> listFalse = new ArrayList<>();
        RequiremetsPlanningDetails requiremetsPlanningDetailsOld = new RequiremetsPlanningDetails();
        HashMap map = new HashMap();
        for (UpdateRequiremetsPlanningDetailsDTO updateRequiremetsPlanningDetailsDTO : updateRequiremetsPlanningDetailsDTOList
        ) {
            requiremetsPlanningDetailsOld = getRequiremetsPlanningDetailsById(updateRequiremetsPlanningDetailsDTO.getId());
            BeanUtil.copyProperties(updateRequiremetsPlanningDetailsDTO, requiremetsPlanningDetailsOld);
            int update = requiremetsPlanningDetailsMapper.updateById(requiremetsPlanningDetailsOld);
            if (update > 0) {
                listSuccess.add(updateRequiremetsPlanningDetailsDTO);
            } else {
                listFalse.add(updateRequiremetsPlanningDetailsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public RequiremetsPlanningDetails getRequiremetsPlanningDetailsById(Integer id) {
        return requiremetsPlanningDetailsMapper.selectById(id);
    }

    @Override
    public List<RequiremetsPlanningDetails> getRequiremetsPlanningDetailsByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper<RequiremetsPlanningDetails> queryWrapper = new QueryWrapper();
        queryWrapper.eq("plan_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        return requiremetsPlanningDetailsMapper.selectList(queryWrapper);
    }
}
