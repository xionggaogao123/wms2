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
import com.huanhong.wms.entity.AllocationOut;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.dto.AddAllocationOutDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDTO;
import com.huanhong.wms.entity.vo.AllocationOutVO;
import com.huanhong.wms.mapper.AllocationOutMapper;
import com.huanhong.wms.service.IAllocationOutService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-30
 */
@Service
public class AllocationOutServiceImpl extends SuperServiceImpl<AllocationOutMapper, AllocationOut> implements IAllocationOutService {


    @Resource
    private AllocationOutMapper allocationOutMapper;

    @Override
    public Page<AllocationOut> pageFuzzyQuery(Page<AllocationOut> allocationOutPage, AllocationOutVO allocationOutVO) {
        //新建QueryWrapper对象
        QueryWrapper<AllocationOut> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(allocationOutVO)) {
            return allocationOutMapper.selectPage(allocationOutPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(allocationOutVO.getAllocationOutNumber()), "allocation_out_number", allocationOutVO.getAllocationOutNumber());

        query.like(StringUtils.isNotBlank(allocationOutVO.getAllocationNumber()), "allocation_number", allocationOutVO.getAllocationNumber());

        query.like(StringUtils.isNotBlank(allocationOutVO.getSendWarehouse()), "send_warehouse", allocationOutVO.getSendWarehouse());

        query.like(ObjectUtil.isNotNull(allocationOutVO.getOutStatus()),"out_status",allocationOutVO.getOutStatus());

        query.like(StringUtils.isNotBlank(allocationOutVO.getEnterWarehouse()), "enter_warehouse", allocationOutVO.getEnterWarehouse());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(allocationOutVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(allocationOutVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(allocationOutVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(allocationOutVO.getCreateTimeEnd());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return allocationOutMapper.selectPage(allocationOutPage, query);
    }

    @Override
    public Page<AllocationOut> pageFuzzyQueryPDA(Page<AllocationOut> allocationOutPage, AllocationOutVO allocationOutVO) {

        //新建QueryWrapper对象
        QueryWrapper<AllocationOut> query = new QueryWrapper<>();

        //根据id排序
        query.orderByAsc("id");

        //单据编号
        query.like(ObjectUtil.isNotNull(allocationOutVO.getAllocationOutNumber()), "allocation_out_number", allocationOutVO.getAllocationOutNumber());

        //单据状态
        if (ObjectUtil.isNotNull(allocationOutVO.getOutStatus()) && allocationOutVO.getOutStatus() == 0) {
            query.eq("out_status", 0).or().eq("out_status", 1);
        } else if (ObjectUtil.isNotNull(allocationOutVO.getOutStatus()) && allocationOutVO.getOutStatus() == 1) {
            query.eq("out_status", 2);
        }

        return allocationOutMapper.selectPage(allocationOutPage, query);
    }

    @Override
    public Result addAllocationOutDTO(AddAllocationOutDTO addAllocationOutDTO) {
        try {
            /**
             * 生成调拨出库单编码（DBCK+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后四位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<AllocationOut> queryAllocationOut = new QueryWrapper<>();

            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryAllocationOut.likeRight("allocation_out_number", "DBCK" + today);
            /**
             * likeRigh: DBCK+XXXXXXXX(当前年月日)
             */
            AllocationOut maxAllocationOut = allocationOutMapper.selectOne(queryAllocationOut.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxAllocationOut)) {
                maxDocNum = maxAllocationOut.getAllocationOutNumber();
            }
            String orderNo = null;
            //单据编码前缀-DBCK+年月日
            String code_pfix = "DBCK" + today;
            if (maxDocNum != null && maxAllocationOut.getAllocationOutNumber().contains(code_pfix)) {
                String code_end = maxAllocationOut.getAllocationOutNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            AllocationOut allocationOut = new AllocationOut();
            BeanUtil.copyProperties(addAllocationOutDTO, allocationOut);
            allocationOut.setAllocationOutNumber(orderNo);
            int i = allocationOutMapper.insert(allocationOut);
            if (i > 0) {
                return Result.success(getAllocationOutByDocNumber(orderNo), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增调拨计划单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    @Override
    public Result update(UpdateAllocationOutDTO updateAllocationOutDTO) {
        AllocationOut allocationOutOld = getAllocationOutById(updateAllocationOutDTO.getId());
        BeanUtil.copyProperties(updateAllocationOutDTO,allocationOutOld);
        int update = allocationOutMapper.updateById(allocationOutOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public AllocationOut getAllocationOutByDocNumber(String docNumber) {
        QueryWrapper<AllocationOut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("allocation_out_number", docNumber);
        return allocationOutMapper.selectOne(queryWrapper);
    }

    @Override
    public AllocationOut getAllocationOutById(Integer id) {
        return allocationOutMapper.selectById(id);
    }

    @Override
    public AllocationOut getAllocationOutByProcessInstanceId(String processInstanceId) {
        QueryWrapper<AllocationOut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_instance_id",processInstanceId);
        return allocationOutMapper.selectOne(queryWrapper);
    }
}
