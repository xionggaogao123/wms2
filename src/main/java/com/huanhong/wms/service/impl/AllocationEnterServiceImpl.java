package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddAllocationEnterAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationEnterDTO;
import com.huanhong.wms.entity.dto.AddAllocationEnterDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationEnterDTO;
import com.huanhong.wms.entity.vo.AllocationEnterVO;
import com.huanhong.wms.mapper.AllocationEnterMapper;
import com.huanhong.wms.mapper.AllocationOutMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IAllocationEnterDetailsService;
import com.huanhong.wms.service.IAllocationEnterService;
import com.huanhong.wms.service.IAllocationPlanDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
public class AllocationEnterServiceImpl extends SuperServiceImpl<AllocationEnterMapper, AllocationEnter> implements IAllocationEnterService {

    @Resource
    private AllocationEnterMapper allocationEnterMapper;
    @Resource
    private AllocationOutMapper allocationOutMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private IAllocationEnterDetailsService allocationEnterDetailsService;
    @Resource
    private IAllocationPlanDetailService allocationPlanDetailService;

    @Override
    public Page<AllocationEnter> pageFuzzyQuery(Page<AllocationEnter> allocationEnterPage, AllocationEnterVO allocationEnterVO) {

        //新建QueryWrapper对象
        QueryWrapper<AllocationEnter> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(allocationEnterVO)) {
            return allocationEnterMapper.selectPage(allocationEnterPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(allocationEnterVO.getAllocationNumber()), "allocation_number", allocationEnterVO.getAllocationOutNumber());

        query.like(StringUtils.isNotBlank(allocationEnterVO.getAllocationOutNumber()), "allocation_out_number", allocationEnterVO.getAllocationOutNumber());

        query.like(StringUtils.isNotBlank(allocationEnterVO.getAllocationEnterNumber()), "allocation_enter_number", allocationEnterVO.getAllocationEnterNumber());

        query.like(StringUtils.isNotBlank(allocationEnterVO.getSendWarehouse()), "send_warehouse", allocationEnterVO.getSendWarehouse());

        query.like(StringUtils.isNotBlank(allocationEnterVO.getEnterWarehouse()), "enter_warehouse", allocationEnterVO.getEnterWarehouse());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(allocationEnterVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(allocationEnterVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(allocationEnterVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(allocationEnterVO.getCreateTimeEnd());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return allocationEnterMapper.selectPage(allocationEnterPage, query);
    }

    @Override
    public Result addAllocationEnterDTO(AddAllocationEnterDTO addAllocationEnterDTO) {
        try {
            /**
             * 生成调拨出库单编码（DBRK+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后四位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<AllocationEnter> queryAllocationEnter = new QueryWrapper<>();

            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryAllocationEnter.likeRight("allocation_enter_number", "DBRK" + today);
            /**
             * likeRigh: DBRK+XXXXXXXX(当前年月日)
             */
            AllocationEnter maxAllocationEnter = allocationEnterMapper.selectOne(queryAllocationEnter.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxAllocationEnter)) {
                maxDocNum = maxAllocationEnter.getAllocationEnterNumber();
            }
            String orderNo = null;
            //单据编码前缀-DBCK+年月日
            String code_pfix = "DBRK" + today;
            if (maxDocNum != null && maxAllocationEnter.getAllocationEnterNumber().contains(code_pfix)) {
                String code_end = maxAllocationEnter.getAllocationEnterNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            AllocationEnter allocationEnter = new AllocationEnter();
            BeanUtil.copyProperties(addAllocationEnterDTO, allocationEnter);
            allocationEnter.setAllocationEnterNumber(orderNo);
            int i = allocationEnterMapper.insert(allocationEnter);
            if (i > 0) {
                return Result.success(getAllocationEnterByDocNumber(orderNo), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增调拨入库单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    @Override
    public Result update(UpdateAllocationEnterDTO updateAllocationEnterDTO) {
        AllocationEnter allocationEnterOld = getAllocationEnterById(updateAllocationEnterDTO.getId());
        BeanUtil.copyProperties(updateAllocationEnterDTO,allocationEnterOld);
        int update = allocationEnterMapper.updateById(allocationEnterOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public AllocationEnter getAllocationEnterByDocNumber(String docNumber) {
        QueryWrapper<AllocationEnter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("allocation_enter_number", docNumber);
        return allocationEnterMapper.selectOne(queryWrapper);
    }

    @Override
    public AllocationEnter getAllocationEnterById(Integer id) {
        return allocationEnterMapper.selectById(id);
    }

    @Override
    public AllocationEnter getAllocationEnterByProcessInstanceId(String processInstanceId) {
        QueryWrapper<AllocationEnter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_instance_id",processInstanceId);
        return allocationEnterMapper.selectOne(queryWrapper);
    }

    @Override
    public Result add(AddAllocationEnterAndDetailsDTO addAllocationEnterAndDetailsDTO) {
        AddAllocationEnterDTO addAllocationEnterDTO = addAllocationEnterAndDetailsDTO.getAddAllocationEnterDTO();
        List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList = addAllocationEnterAndDetailsDTO.getAddAllocationEnterDetailsDTOList();
        Result result = addAllocationEnterDTO(addAllocationEnterDTO);
        if (!result.isOk()) {
            return Result.failure("新增调拨入库失败！");
        }
        AllocationEnter allocationEnter = (AllocationEnter) result.getData();
        String docNum = allocationEnter.getAllocationEnterNumber();
        for (AddAllocationEnterDetailsDTO addAllocationEnterDetailsDTO : addAllocationEnterDetailsDTOList) {
            addAllocationEnterDetailsDTO.setAllocationEnterNumber(docNum);
        }
        return allocationEnterDetailsService.addAllocationEnterDetails(addAllocationEnterDetailsDTOList);
    }

    @Override
    public Result allocationPlanToAllocationEnter(AllocationPlan allocationPlan) {

        if (ObjectUtil.isNull(allocationPlan)){
            return Result.failure("单据不存在！");
        }
        // 根据调拨单号查询是否已有调拨出库
        List<AllocationOut> allocationOuts = allocationOutMapper.selectList(Wrappers.<AllocationOut>lambdaQuery().eq(AllocationOut::getAllocationNumber,allocationPlan.getAllocationNumber()));
        if(CollectionUtil.isEmpty(allocationOuts)){
            return Result.failure("调拨计划，请先创建调拨出库，再创建调拨入库");
        }
        AddAllocationEnterAndDetailsDTO addAllocationEnterAndDetailsDTO  = new AddAllocationEnterAndDetailsDTO();

        List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList = new ArrayList<>();
        /**
         * 处理主表
         */
        AddAllocationEnterDTO addAllocationEnterDTO = new AddAllocationEnterDTO();

        BeanUtil.copyProperties(allocationPlan,addAllocationEnterDTO);

        //库管员
        addAllocationEnterDTO.setLibrarian(allocationPlan.getSendUser());
        //调入仓库
        addAllocationEnterDTO.setEnterWarehouse(allocationPlan.getReceiveWarehouse());
        addAllocationEnterDTO.setAllocationOutNumber(allocationOuts.get(0).getAllocationOutNumber());
        addAllocationEnterDTO.setRemark("系统自动生成");
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getLoginName,allocationPlan.getSendUser()).last("limit 1"));
        if(null != user && null != user.getCompanyName()){
            addAllocationEnterDTO.setReceiveCompany(user.getCompanyName());
        }
        addAllocationEnterAndDetailsDTO.setAddAllocationEnterDTO(addAllocationEnterDTO);

        /**
         * 处理明细
         */
        List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
        for (AllocationPlanDetail allocationPlanDetail:allocationPlanDetailsList
        ) {
            AddAllocationEnterDetailsDTO addAllocationEnterDetailsDTO = new AddAllocationEnterDetailsDTO();
            BeanUtil.copyProperties(allocationPlanDetail,addAllocationEnterDetailsDTO);
            addAllocationEnterDetailsDTO.setRemark("系统自动生成");
            addAllocationEnterDetailsDTOList.add(addAllocationEnterDetailsDTO);
        }
        addAllocationEnterAndDetailsDTO.setAddAllocationEnterDetailsDTOList(addAllocationEnterDetailsDTOList);
        return  add(addAllocationEnterAndDetailsDTO);
    }
}
