package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.dto.AddAllocationPlanDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationPlanDTO;
import com.huanhong.wms.entity.vo.AllocationPlanVO;
import com.huanhong.wms.mapper.AllocationPlanMapper;
import com.huanhong.wms.service.IAllocationPlanService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 调拨计划主表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-17
 */
@Service
public class AllocationPlanServiceImpl extends SuperServiceImpl<AllocationPlanMapper, AllocationPlan> implements IAllocationPlanService {

    @Resource
    private AllocationPlanMapper allocationPlanMapper;

    @Override
    public Page<AllocationPlan> pageFuzzyQuery(Page<AllocationPlan> allocationPlanPage, AllocationPlanVO allocationPlanVO) {

        //新建QueryWrapper对象
        QueryWrapper<AllocationPlan> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(allocationPlanVO)) {
            return allocationPlanMapper.selectPage(allocationPlanPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(allocationPlanVO.getAllocationNumber()), "allocation_number", allocationPlanVO.getAllocationNumber());

        query.like(ObjectUtil.isNotNull(allocationPlanVO.getBusinessType()), "business_type", allocationPlanVO.getBusinessType());

        query.like(ObjectUtil.isNotNull(allocationPlanVO.getPlanStatus()), "plan_status",allocationPlanVO.getPlanStatus());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getSendWarehouse()), "send_warehouse",allocationPlanVO.getSendWarehouse());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getReceiveWarehouse()), "receive_warehouse",allocationPlanVO.getReceiveWarehouse());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getApplicant()), "applicant",allocationPlanVO.getApplicant());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 调拨时间区间
         */
        if (ObjectUtil.isNotEmpty(allocationPlanVO.getAssignmentDateStart()) && ObjectUtil.isNotEmpty(allocationPlanVO.getAssignmentDateEnd())) {
            String AssignmentDateStart = dtf1.format(allocationPlanVO.getAssignmentDateStart());
            String AssignmentDateEnd = dtf1.format(allocationPlanVO.getAssignmentDateEnd());
            /**
             * 调拨时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(assignment_date) >= UNIX_TIMESTAMP('" + AssignmentDateStart + "')")
                    .apply("UNIX_TIMESTAMP(assignment_date) <= UNIX_TIMESTAMP('" + AssignmentDateEnd + "')");

        }

        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(allocationPlanVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(allocationPlanVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(allocationPlanVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(allocationPlanVO.getCreateTimeEnd());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return allocationPlanMapper.selectPage(allocationPlanPage, query);

    }

    @Override
    public Result addAllocationPlan(AddAllocationPlanDTO addAllocationPlanDTO) {
        try {
            /**
             * 生成到货检验单单据编码（DBJH+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<AllocationPlan> queryAllocationPlan = new QueryWrapper<>();

            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryAllocationPlan.likeRight("allocation_number", "DBJH" + today);
            /**
             * likeRigh: DBJH+XXXXXXXX(当前年月日)
             */
            AllocationPlan maxAllocationPlan = allocationPlanMapper.selectOne(queryAllocationPlan.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxAllocationPlan)) {
                maxDocNum = maxAllocationPlan.getAllocationNumber();
            }
            String orderNo = null;
            //单据编码前缀-DHJY+年月日
            String code_pfix = "DBJH" + today;
            if (maxDocNum != null && maxAllocationPlan.getAllocationNumber().contains(code_pfix)) {
                String code_end = maxAllocationPlan.getAllocationNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            AllocationPlan allocationPlan = new AllocationPlan();
            BeanUtil.copyProperties(addAllocationPlanDTO, allocationPlan);
            allocationPlan.setAllocationNumber(orderNo);
            int i = allocationPlanMapper.insert(allocationPlan);
            if (i > 0) {
                return Result.success(getAllocationPlanByDocNumber(orderNo), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增调拨计划单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    @Override
    public Result updateAllocationPlan(UpdateAllocationPlanDTO updateAllocationPlanDTO) {
        AllocationPlan allocationPlanOld = getAllocationPlanById(updateAllocationPlanDTO.getId());
        BeanUtil.copyProperties(updateAllocationPlanDTO,allocationPlanOld);
        int update = allocationPlanMapper.updateById(allocationPlanOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public AllocationPlan getAllocationPlanByDocNumber(String docNumber) {
        QueryWrapper<AllocationPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("allocation_number", docNumber);
        return allocationPlanMapper.selectOne(queryWrapper);
    }

    @Override
    public AllocationPlan getAllocationPlanById(Integer id) {
        return allocationPlanMapper.selectById(id);
    }

    @Override
    public AllocationPlan getAllocationPlanByProcessInstanceId(String processInstanceId) {
        QueryWrapper<AllocationPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_instance_id",processInstanceId);
        return allocationPlanMapper.selectOne(queryWrapper);
    }
}
