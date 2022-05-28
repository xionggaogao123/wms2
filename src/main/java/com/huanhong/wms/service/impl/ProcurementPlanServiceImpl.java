package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcurementPlan;
import com.huanhong.wms.entity.dto.AddProcurementPlanAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddProcurementPlanDTO;
import com.huanhong.wms.entity.dto.AddProcurementPlanDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateProcurementPlanDTO;
import com.huanhong.wms.entity.param.DeptMaterialParam;
import com.huanhong.wms.entity.param.MaterialYearParam;
import com.huanhong.wms.entity.vo.MaterialYearVO;
import com.huanhong.wms.entity.vo.ProcurementPlanVO;
import com.huanhong.wms.mapper.ProcurementPlanMapper;
import com.huanhong.wms.service.IProcurementPlanDetailsService;
import com.huanhong.wms.service.IProcurementPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购计划主表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-21
 */
@Slf4j
@Service
public class ProcurementPlanServiceImpl extends SuperServiceImpl<ProcurementPlanMapper, ProcurementPlan> implements IProcurementPlanService {

    @Resource
    private ProcurementPlanMapper procurementPlanMapper;
    @Resource
    private IProcurementPlanDetailsService procurementPlanDetailsService;

    @Override
    public Page<ProcurementPlan> pageFuzzyQuery(Page<ProcurementPlan> procurementPlanPage, ProcurementPlanVO procurementPlanVO) {

        //新建QueryWrapper对象
        QueryWrapper<ProcurementPlan> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(procurementPlanVO)) {
            return procurementPlanMapper.selectPage(procurementPlanPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装

        //需求单编号
        query.like(StringUtils.isNotBlank(procurementPlanVO.getOriginalDocumentNumber()), "original_document_number", procurementPlanVO.getOriginalDocumentNumber());
        //采购计划单
        query.like(StringUtils.isNotBlank(procurementPlanVO.getPlanNumber()), "plan_number", procurementPlanVO.getPlanNumber());
        //计划类别
        query.like(ObjectUtil.isNotNull(procurementPlanVO.getPlanClassification()), "plan_classification", procurementPlanVO.getPlanClassification());
        //状态
        query.like(ObjectUtil.isNotNull(procurementPlanVO.getStatus()), "status", procurementPlanVO.getStatus());
        //计划部门
        query.like(StringUtils.isNotBlank(procurementPlanVO.getPlanningDepartment()), "planning_department", procurementPlanVO.getPlanningDepartment());
        //计划员
        query.like(StringUtils.isNotBlank(procurementPlanVO.getPlanner()), "planner", procurementPlanVO.getPlanner());
        //仓库编号
        query.like(StringUtils.isNotBlank(procurementPlanVO.getWarehouseId()), "warehouse_id", procurementPlanVO.getWarehouseId());
        //需求部门
        query.like(StringUtils.isNotBlank(procurementPlanVO.getDemandDepartment()), "demand_department", procurementPlanVO.getDemandDepartment());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(procurementPlanVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(procurementPlanVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(procurementPlanVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(procurementPlanVO.getCreateTimeEnd());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        //状态
        query.eq(ObjectUtil.isNotNull(procurementPlanVO.getIsImported()), "is_imported", procurementPlanVO.getIsImported());
        query.eq(StrUtil.isNotBlank(procurementPlanVO.getMaterialUse()), "material_use", procurementPlanVO.getMaterialUse());
        return baseMapper.selectPage(procurementPlanPage, query);
    }

    @Override
    public Result addProcurementPlan(AddProcurementPlanDTO addProcurementPlanDTO) {
        try {
            /**
             * 生成需求计划单据编码（CGJH+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
//            QueryWrapper<ProcurementPlan> queryProcurementPlan = new QueryWrapper<>();
//            /**
//             * 当前仓库
//             */
//            queryProcurementPlan.eq("warehouse_id", addProcurementPlanDTO.getWarehouseId());
//            /**
//             * 当前日期
//             */
//            String today = StrUtils.HandleData(DateUtil.today());
//            queryProcurementPlan.likeRight("plan_number", "CGJH" + today);
//            /**
//             * likeRigh: XQJH+XXXXXXXX(当前年月日)
//             */
//            ProcurementPlan maxProcurementPlan = procurementPlanMapper.selectOne(queryProcurementPlan.orderByDesc("id").last("limit 1"));
//
//            //目前最大的单据编码
//            String maxDocNum = null;
//            if (ObjectUtil.isNotEmpty(maxProcurementPlan)) {
//                maxDocNum = maxProcurementPlan.getPlanNumber();
//            }
//            String orderNo = null;
//            //单据编码前缀-CGRU+年月日
//            String code_pfix = "CGJH" + today;
//            if (maxDocNum != null && maxProcurementPlan.getPlanNumber().contains(code_pfix)) {
//                String code_end = maxProcurementPlan.getPlanNumber().substring(12, 16);
//                int endNum = Integer.parseInt(code_end);
//                int tmpNum = 10000 + endNum + 1;
//                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
//            } else {
//                orderNo = code_pfix + "0001";
//            }

            /**
             * 新增单据
             */
            ProcurementPlan procurementPlan = new ProcurementPlan();
            BeanUtil.copyProperties(addProcurementPlanDTO, procurementPlan);
            procurementPlan.setPlanNumber("CGJH"+String.valueOf(System.currentTimeMillis()));
            int i = procurementPlanMapper.insert(procurementPlan);
            if (i > 0) {
                return Result.success(getProcurementPlanByDocNumAndWarehouseId(procurementPlan.getPlanNumber(), procurementPlan.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增采购计划单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    @Override
    public Result updateProcurementPlan(UpdateProcurementPlanDTO updateProcurementPlanDTO) {
        ProcurementPlan procurementPlanOld = getProcurementPlanById(updateProcurementPlanDTO.getId());
        BeanUtil.copyProperties(updateProcurementPlanDTO,procurementPlanOld);
        int update = procurementPlanMapper.updateById(procurementPlanOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public ProcurementPlan getProcurementPlanById(Integer id) {
        return procurementPlanMapper.selectById(id);
    }

    @Override
    public ProcurementPlan getProcurementPlanByDocNumAndWarehouseId(String DocNum, String warehouseId) {
        QueryWrapper<ProcurementPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_number",DocNum);
        queryWrapper.eq("warehouse_id",warehouseId);
        return procurementPlanMapper.selectOne(queryWrapper);
    }

    @Override
    public ProcurementPlan getProcurementPlanByProcessInstanceId(String processInstanceId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_instance_id",processInstanceId);
        ProcurementPlan procurementPlan = procurementPlanMapper.selectOne(queryWrapper);
        return procurementPlan;
    }

    @Override
    public Result<Object> getProcurementPlanFrequencyAndQuantity(DeptMaterialParam param) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> material = procurementPlanMapper.getProcurementPlanFrequencyAndQuantityByParam(param);
        map.put("material", material);
        return Result.success(map);
    }

    @Override
    public Result<Object> getMaterialPurchasingAnalysisOnYearBasis(MaterialYearParam param) {
        Map<Integer, List<MaterialYearVO>> map = new HashMap<>();
        List<MaterialYearVO> list = procurementPlanMapper.getMaterialPurchasingAnalysisOnYearBasisByParam(param);
        map = list.stream().collect(Collectors.groupingBy(MaterialYearVO::getYearTime));
        return Result.success(map);
    }

    @Override
    public Result add(AddProcurementPlanAndDetailsDTO addProcurementPlanAndDetailsDTO) {

        AddProcurementPlanDTO addProcurementPlanDTO = addProcurementPlanAndDetailsDTO.getAddProcurementPlanDTO();
        List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList = addProcurementPlanAndDetailsDTO.getAddProcurementPlanDetailsDTOList();
        Result result = addProcurementPlan(addProcurementPlanDTO);
        if (!result.isOk()) {
            return Result.failure("新增采购计划失败！");
        }
        ProcurementPlan procurementPlan = (ProcurementPlan) result.getData();
        String docNum = procurementPlan.getPlanNumber();
        String warehouseId = procurementPlan.getWarehouseId();
        if (ObjectUtil.isNotNull(addProcurementPlanDetailsDTOList)){
            for (AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTO : addProcurementPlanDetailsDTOList) {
                addProcurementPlanDetailsDTO.setPlanNumber(docNum);
                addProcurementPlanDetailsDTO.setWarehouseId(warehouseId);
            }
            procurementPlanDetailsService.addProcurementPlanDetails(addProcurementPlanDetailsDTOList);
        }
        return result;
    }
}
