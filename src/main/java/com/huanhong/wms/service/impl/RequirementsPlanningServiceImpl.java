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
import com.huanhong.wms.entity.RequirementsPlanning;
import com.huanhong.wms.entity.dto.AddRequirementsPlanningDTO;
import com.huanhong.wms.entity.dto.UpdateRequirementsPlanningDTO;
import com.huanhong.wms.entity.vo.RequirementsPlanningVO;
import com.huanhong.wms.mapper.RequirementsPlanningMapper;
import com.huanhong.wms.service.IRequirementsPlanningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 需求计划表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-16
 */
@Slf4j
@Service
public class RequirementsPlanningServiceImpl extends SuperServiceImpl<RequirementsPlanningMapper, RequirementsPlanning> implements IRequirementsPlanningService {

    @Resource
    private RequirementsPlanningMapper requirementsPlanningMapper;


    @Override
    public Page<RequirementsPlanning> pageFuzzyQuery(Page<RequirementsPlanning> requirementsPlanningPage, RequirementsPlanningVO requirementsPlanningVO) {

        //新建QueryWrapper对象
        QueryWrapper<RequirementsPlanning> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(requirementsPlanningVO)) {
            return requirementsPlanningMapper.selectPage(requirementsPlanningPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        //需求单编号
        query.like(StringUtils.isNotBlank(requirementsPlanningVO.getPlanNumber()), "plan_number",requirementsPlanningVO.getPlanNumber());
        //计划部门
        query.like(StringUtils.isNotBlank(requirementsPlanningVO.getPlanUnit()), "plan_unit", requirementsPlanningVO.getPlanUnit());
        //仓库编号
        query.like(StringUtils.isNotBlank(requirementsPlanningVO.getWarehouseId()),"warehouse_id",requirementsPlanningVO.getWarehouseId());
        //申请人
        query.like(StringUtils.isNotBlank(requirementsPlanningVO.getApplicant()),"applicant",requirementsPlanningVO.getApplicant());
        //计划类别
        query.like(ObjectUtil.isNotNull(requirementsPlanningVO.getPlanClassification()),"plan_classification",requirementsPlanningVO.getPlanClassification());
        //预估总金额
        query.like(ObjectUtil.isNotNull(requirementsPlanningVO.getEstimatedTotalAmount()),"estimated_total_amount",requirementsPlanningVO.getEstimatedTotalAmount());
        //状态
        query.like(ObjectUtil.isNotNull(requirementsPlanningVO.getPlanStatus()),"plan_status",requirementsPlanningVO.getPlanStatus());
        //物料用途
        query.like(StringUtils.isNotBlank(requirementsPlanningVO.getMaterialUse()),"material_use",requirementsPlanningVO.getMaterialUse());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(requirementsPlanningVO.getCreateDateStart())&&ObjectUtil.isNotEmpty(requirementsPlanningVO.getCreateDateEnd())){
            String createDateStart = dtf1.format(requirementsPlanningVO.getCreateDateStart());
            String createDateEnd = dtf1.format(requirementsPlanningVO.getCreateDateEnd());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        return baseMapper.selectPage(requirementsPlanningPage, query);

    }

    @Override
    public Result addRequirementsPlanning(AddRequirementsPlanningDTO addRequirementsPlanningDTO) {

        try {
            /**
             * 生成需求计划单据编码（XQJH+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<RequirementsPlanning> queryRequirementsPlanning = new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryRequirementsPlanning.eq("warehouse_id", addRequirementsPlanningDTO.getWarehouseId());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryRequirementsPlanning.likeRight("plan_number", "XQJH" + today);
            /**
             * likeRigh: XQJH+XXXXXXXX(当前年月日)
             */
            RequirementsPlanning maxRequirementsPlanning = requirementsPlanningMapper.selectOne(queryRequirementsPlanning.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxRequirementsPlanning)) {
                maxDocNum = maxRequirementsPlanning.getPlanNumber();
            }
            String orderNo = null;
            //单据编码前缀-CGRU+年月日
            String code_pfix = "XQJH" + today;
            if (maxDocNum != null && maxRequirementsPlanning.getPlanNumber().contains(code_pfix)) {
                String code_end = maxRequirementsPlanning.getPlanNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            RequirementsPlanning requirementsPlanning = new RequirementsPlanning();
            BeanUtil.copyProperties(addRequirementsPlanningDTO, requirementsPlanning);
            requirementsPlanning.setPlanNumber(orderNo);
            int i = requirementsPlanningMapper.insert(requirementsPlanning);
            if (i > 0) {
                return Result.success(getRequirementsPlanningByDocNumAndWarehouseId(orderNo, requirementsPlanning.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增需求计划单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    @Override
    public Result updateRequirementsPlanning(UpdateRequirementsPlanningDTO updateRequirementsPlanningDTO) {
        RequirementsPlanning requirementsPlanningOld = getRequirementsPlanningById(updateRequirementsPlanningDTO.getId());
        BeanUtil.copyProperties(updateRequirementsPlanningDTO,requirementsPlanningOld);
        int update = requirementsPlanningMapper.updateById(requirementsPlanningOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public RequirementsPlanning getRequirementsPlanningById(Integer id) {
        return requirementsPlanningMapper.selectById(id);
    }

    @Override
    public RequirementsPlanning getRequirementsPlanningByDocNumAndWarehouseId(String DocNum, String warehouseId) {
        QueryWrapper<RequirementsPlanning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_number",DocNum);
        queryWrapper.eq("warehouse_id",warehouseId);
        return requirementsPlanningMapper.selectOne(queryWrapper);
    }

    @Override
    public RequirementsPlanning getRequirementsPlanningByProcessInstanceId(String processInstanceId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_instance_id",processInstanceId);
        RequirementsPlanning requirementsPlanning = requirementsPlanningMapper.selectOne(queryWrapper);
        return requirementsPlanning;
    }
}
