package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.dto.AddPlanUseOutDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;
import com.huanhong.wms.mapper.PlanUseOutMapper;
import com.huanhong.wms.service.IPlanUseOutService;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 计划领用主表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-15
 */
@Service
public class PlanUseOutServiceImpl extends SuperServiceImpl<PlanUseOutMapper, PlanUseOut> implements IPlanUseOutService {


    @Resource
    private PlanUseOutMapper planUseOutMapper;

    /**
     * 分页查询
     *
     * @param planUseOutPage
     * @param planUseOutVO
     * @return
     */
    @Override
    public Page<PlanUseOut> pageFuzzyQuery(Page<PlanUseOut> planUseOutPage, PlanUseOutVO planUseOutVO) {

        //新建QueryWrapper对象
        QueryWrapper<PlanUseOut> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(planUseOutVO)) {
            return planUseOutMapper.selectPage(planUseOutPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(planUseOutVO.getDocumentNumber()), "document_number", planUseOutVO.getDocumentNumber());

        query.like(ObjectUtil.isNotNull(planUseOutVO.getStatus()), "status", planUseOutVO.getStatus());

        query.like(ObjectUtil.isNotNull(planUseOutVO.getPlanClassification()), "plan_classification", planUseOutVO.getPlanClassification());

        query.like(StringUtils.isNotBlank(planUseOutVO.getRequisitioningUnit()), "requisitioning_unit", planUseOutVO.getRequisitioningUnit());

        query.like(StringUtils.isNotBlank(planUseOutVO.getWarehouseId()), "warehouse_id", planUseOutVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(planUseOutVO.getLibrarian()), "librarian", planUseOutVO.getLibrarian());

        query.eq(ObjectUtil.isNotNull(planUseOutVO.getOutStatus()),"out_status",planUseOutVO.getOutStatus());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 申请时间区间
         */
        if (ObjectUtil.isNotEmpty(planUseOutVO.getApplicationDateStart()) && ObjectUtil.isNotEmpty(planUseOutVO.getApplicationDateEnd())) {
            String applicationDateStart = dtf1.format(planUseOutVO.getApplicationDateStart());
            String applicationDateEnd = dtf1.format(planUseOutVO.getApplicationDateEnd());
            /**
             * 申请时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + applicationDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + applicationDateEnd + "')");

        }

        return planUseOutMapper.selectPage(planUseOutPage, query);
    }

    @Override
    public Result addPlanUseOut(AddPlanUseOutDTO addPlanUseOutDTO) {
        try {
            /**
             * 生成领料出库单据编码（LLCK+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后四位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<PlanUseOut> queryPlanUseOut = new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryPlanUseOut.eq("warehouse_id", addPlanUseOutDTO.getWarehouseId());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryPlanUseOut.likeRight("document_number", "LLCK" + today);
            /**
             * likeRigh: LLCK+XXXXXXXX(当前年月日)
             */
            PlanUseOut maxPlanUseOut = planUseOutMapper.selectOne(queryPlanUseOut.orderByDesc("id").last("limit 1"));
            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxPlanUseOut)) {
                maxDocNum = maxPlanUseOut.getDocumentNumber();
            }
            String orderNo = null;
            //单据编码前缀-LLCK+年月日
            String code_pfix = "LLCK" + today;
            if (maxDocNum != null && maxPlanUseOut.getDocumentNumber().contains(code_pfix)) {
                String code_end = maxPlanUseOut.getDocumentNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            PlanUseOut planUseOut = new PlanUseOut();
            BeanUtil.copyProperties(addPlanUseOutDTO, planUseOut);
            planUseOut.setDocumentNumber(orderNo);
            int i = planUseOutMapper.insert(planUseOut);
            if (i > 0) {
                return Result.success(getPlanUseOutByDocNumAndWarhouseId(orderNo, addPlanUseOutDTO.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增领料出库单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    @Override
    public Result updatePlanUseOut(UpdatePlanUseOutDTO updatePlanUseOutDTO) {

        PlanUseOut planUseOutOld = getPlanUseOutById(updatePlanUseOutDTO.getId());
        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */

        //流程Id
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getProcessInstanceId())){
            planUseOutOld.setProcessInstanceId(updatePlanUseOutDTO.getProcessInstanceId());
        }

        //单据状态
        if (ObjectUtil.isNotNull(updatePlanUseOutDTO.getStatus())){
            planUseOutOld.setStatus(updatePlanUseOutDTO.getStatus());
        }
        //计划类别
        if (ObjectUtil.isNotNull(updatePlanUseOutDTO.getPlanClassification())){
            planUseOutOld.setPlanClassification(updatePlanUseOutDTO.getPlanClassification());
        }
        //领用单位
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRequisitioningUnit())){
            planUseOutOld.setRequisitioningUnit(updatePlanUseOutDTO.getRequisitioningUnit());
        }
        //库房员
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getLibrarian())){
            planUseOutOld.setLibrarian(updatePlanUseOutDTO.getLibrarian());
        }
        //费用承担单位
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getCostBearingUnit())){
            planUseOutOld.setCostBearingUnit(updatePlanUseOutDTO.getCostBearingUnit());
        }
        //费用项目
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getExpenseItem())){
            planUseOutOld.setExpenseItem(updatePlanUseOutDTO.getExpenseItem());
        }
        //物资用途
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getMaterialUse())){
            planUseOutOld.setMaterialUse(updatePlanUseOutDTO.getMaterialUse());
        }
        //领用用途
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRequisitionUse())){
            planUseOutOld.setRequisitionUse(updatePlanUseOutDTO.getRequisitionUse());
        }
        //已完成的明细id,格式：以逗号隔开的字符串
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getDetailIds())){
            //获取已经存储的已完成明细Id
            List<String> listOld = new ArrayList<>();
            //更新的Id
            List<String> listPSL = new ArrayList<>();
            listPSL= Collections.singletonList(updatePlanUseOutDTO.getDetailIds());
            if (ObjectUtil.isNotNull(planUseOutOld.getDetailIds())){
                String s = planUseOutOld.getDetailIds();
                listOld = Arrays.stream(StringUtils.split(s, ",")).map(s1 -> s1.trim()).collect(Collectors.toList());
            }
            listPSL = Stream.of(listPSL, listOld)
                    .flatMap(Collection::stream).distinct().collect(Collectors.toList());
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            planUseOutOld.setDetailIds(resultString);
        }else {
            List<String> listPSL = new ArrayList<>();
            listPSL= Collections.singletonList(updatePlanUseOutDTO.getDetailIds());
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            planUseOutOld.setDetailIds(resultString);
        }

        //出库状态
        if (ObjectUtil.isNotNull(updatePlanUseOutDTO.getOutStatus())){
            planUseOutOld.setOutStatus(updatePlanUseOutDTO.getOutStatus());
        }

        //备注
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRemark())){
            planUseOutOld.setRemark(updatePlanUseOutDTO.getRemark());
        }
        int update = planUseOutMapper.updateById(planUseOutOld);
        return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
    }

    @Override
    public PlanUseOut getPlanUseOutById(Integer id) {
        return planUseOutMapper.selectById(id);
    }


    @Override
    public PlanUseOut getPlanUseOutByDocNumAndWarhouseId(String docNumber, String warhouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number",docNumber);
        queryWrapper.eq("warehouse_id",warhouseId);
        PlanUseOut planUseOut =  planUseOutMapper.selectOne(queryWrapper);
        return planUseOut;
    }


    @Override
    public PlanUseOut getPlanUseOutByProcessInstanceId(String processInstanceId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_instance_id",processInstanceId);
        PlanUseOut planUseOut = planUseOutMapper.selectOne(queryWrapper);
        return planUseOut;
    }
}
