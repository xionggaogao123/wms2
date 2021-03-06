package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.DataUtil;
import com.huanhong.common.units.StrUtils;
import com.huanhong.common.units.excel.ExportExcel;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.param.AllocationDetailPage;
import com.huanhong.wms.entity.vo.AllocationDetailVo;
import com.huanhong.wms.entity.vo.AllocationPlanVO;
import com.huanhong.wms.mapper.AllocationPlanMapper;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.IAllocationPlanDetailService;
import com.huanhong.wms.service.IAllocationPlanService;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IOutboundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private IAllocationPlanDetailService allocationPlanDetailService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IOutboundRecordService outboundRecordService;

    @Autowired
    private OssProperties ossProperties;

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

        query.like(ObjectUtil.isNotNull(allocationPlanVO.getPlanStatus()), "plan_status", allocationPlanVO.getPlanStatus());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getSendWarehouse()), "send_warehouse", allocationPlanVO.getSendWarehouse());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getReceiveWarehouse()), "receive_warehouse", allocationPlanVO.getReceiveWarehouse());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getApplicant()), "applicant", allocationPlanVO.getApplicant());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getSendUser()), "send_user", allocationPlanVO.getSendUser());

        query.like(StringUtils.isNotBlank(allocationPlanVO.getReceiveUser()), "receive_user", allocationPlanVO.getReceiveUser());

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
        } catch (Exception e) {
            log.error("新增调拨计划单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    @Override
    public Result updateAllocationPlan(UpdateAllocationPlanDTO updateAllocationPlanDTO) {
        AllocationPlan allocationPlanOld = getAllocationPlanById(updateAllocationPlanDTO.getId());
        BeanUtil.copyProperties(updateAllocationPlanDTO, allocationPlanOld);
        int update = allocationPlanMapper.updateById(allocationPlanOld);
        return update > 0 ? Result.success() : Result.failure("更新失败！");
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
        queryWrapper.eq("process_instance_id", processInstanceId);
        return allocationPlanMapper.selectOne(queryWrapper);
    }

    @Override
    public Result addOutboundRecordUpdateInventory(AllocationPlan allocationPlan) {
        List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
        List<AddOutboundRecordDTO> addOutboundRecordDTOList = new ArrayList<>();
        /**
         * 获取当前库存是否满足领用
         * 1.warehouseId和materialCoding和批次
         */
        if (ObjectUtil.isNotEmpty(allocationPlanDetailsList)) {
            for (AllocationPlanDetail allocationPlanDetail : allocationPlanDetailsList
            ) {
                BigDecimal nowNum = BigDecimal.valueOf(inventoryInformationService.getNumByMaterialCodingAndBatchAndWarehouseId(allocationPlanDetail.getMaterialCoding(), allocationPlanDetail.getBatch(), allocationPlan.getSendWarehouse()));
                BigDecimal planNum = BigDecimal.valueOf(allocationPlanDetail.getRequestQuantity());
                int event = nowNum.compareTo(planNum);
                /**
                 * event = -1 : planNuM > nowNum
                 * event =  0 : planNuM = nowNum
                 * event =  1 : planNuM < nowNum
                 */
                if (event >= 0) {
                    BigDecimal tempNum = planNum;
                    List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(allocationPlanDetail.getMaterialCoding(), allocationPlanDetail.getBatch(), allocationPlan.getSendWarehouse());
                    for (InventoryInformation inventoryInformation : inventoryInformationList) {
                        //留存出库记录
                        AddOutboundRecordDTO addOutboundRecordDTO = new AddOutboundRecordDTO();
                        /**
                         * 1.将一条库存的数据（编码、批次、货位）中的库存数量放入出库记录的出库数量中：库存数量更新为零，出库数量新增一条数据
                         * 2.每搬空一条库存数据，tempNum减去对应的数量
                         * 3.tempNum不为零之前（满足计划领用数量之前）一直循环
                         * 4.在编辑库存数据之前，判断目前的tempNum是否已经小于此条库存数据的库存数。若大于清空此条库存并循环下一条数据，若小于则更新对应数量
                         */
                        if (tempNum.compareTo(BigDecimal.valueOf(0)) > 0) {
                            //tempNum大于等于此条数据的库存数量
                            if (tempNum.compareTo(BigDecimal.valueOf(inventoryInformation.getInventoryCredit())) >= 0) {
                                //更新原库存为零
                                UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                                BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);
                                updateInventoryInformationDTO.setInventoryCredit((double) 0);
                                Result update = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                                if (update.isOk()) {
                                    //新增一条出库记录
                                    addOutboundRecordDTO.setCargoSpaceId(inventoryInformation.getCargoSpaceId());
                                    addOutboundRecordDTO.setBatch(inventoryInformation.getBatch());
                                    addOutboundRecordDTO.setOutQuantity(inventoryInformation.getInventoryCredit());
                                    tempNum = tempNum.subtract(BigDecimal.valueOf(inventoryInformation.getInventoryCredit()));
                                    /**
                                     * 单据进入流程时，根据领用数量生成出库记录
                                     * 1.原单据编号
                                     * 2.库房ID
                                     * 3.物料编码
                                     * 4.状态：0-审批中（锁库存）1-审批生效（出库）
                                     * 5.出库类型：1-领料出库 2-调拨出库
                                     */
                                    addOutboundRecordDTO.setDocumentNumber(allocationPlanDetail.getAllocationNumber());
                                    addOutboundRecordDTO.setWarehouseId(allocationPlan.getSendWarehouse());
                                    addOutboundRecordDTO.setMaterialCoding(allocationPlanDetail.getMaterialCoding());
                                    addOutboundRecordDTO.setStatus(0);
                                    addOutboundRecordDTO.setOutType(2);
                                    addOutboundRecordDTOList.add(addOutboundRecordDTO);
                                } else {
                                    log.error("更新库存失败");
                                    return Result.failure("更新库存失败");
                                }
                            } else {
                                //更新库存为原库存-tempNum
                                UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                                BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);
                                BigDecimal newInventoryNum = BigDecimal.valueOf(inventoryInformation.getInventoryCredit()).subtract(tempNum);
                                updateInventoryInformationDTO.setInventoryCredit(newInventoryNum.doubleValue());
                                Result update = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                                if (update.isOk()) {
                                    //新增一条出库记录
                                    addOutboundRecordDTO.setCargoSpaceId(inventoryInformation.getCargoSpaceId());
                                    addOutboundRecordDTO.setBatch(inventoryInformation.getBatch());
                                    addOutboundRecordDTO.setOutQuantity(tempNum.doubleValue());
                                    tempNum = tempNum.subtract(BigDecimal.valueOf(inventoryInformation.getInventoryCredit()));
                                    /**
                                     * 单据进入流程时，根据领用数量生成出库记录
                                     * 1.原单据编号
                                     * 2.库房ID
                                     * 3.物料编码
                                     * 4.状态：0-审批中（锁库存）1-审批生效（出库）
                                     * 5.出库类型：1-领料出库 2-调拨出库
                                     */
                                    addOutboundRecordDTO.setDocumentNumber(allocationPlanDetail.getAllocationNumber());
                                    addOutboundRecordDTO.setWarehouseId(allocationPlan.getSendWarehouse());
                                    addOutboundRecordDTO.setMaterialCoding(allocationPlanDetail.getMaterialCoding());
                                    addOutboundRecordDTO.setStatus(0);
                                    addOutboundRecordDTO.setOutType(2);
                                    addOutboundRecordDTOList.add(addOutboundRecordDTO);
                                } else {
                                    log.error("更新库存失败");
                                    return Result.failure("更新库存失败");
                                }
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    return Result.failure("物料：" + allocationPlanDetail.getMaterialCoding() + " 库存不足，请重拟领用单！");
                }
            }
            //放入新增出库记录List
            Result result = outboundRecordService.addOutboundRecordList(addOutboundRecordDTOList);
            if (!result.isOk()) {
                return Result.failure("新增库存记录失败");
            } else {
                return Result.success("新增库存记录成功");
            }
        } else {
            return Result.failure("未查询到明细单据信息");
        }
    }

    @Override
    public Result checkStock(AllocationPlan allocationPlan) {
        List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService
                .getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
        if (ObjectUtil.isEmpty(allocationPlanDetailsList)) {
            return Result.failure("未查询到明细单据信息");
        }
        for (AllocationPlanDetail allocationPlanDetail : allocationPlanDetailsList) {
            BigDecimal nowNum = BigDecimal.valueOf(inventoryInformationService.getNumByMaterialCodingAndBatchAndWarehouseId(allocationPlanDetail.getMaterialCoding(), allocationPlanDetail.getBatch(), allocationPlan.getSendWarehouse()));
            BigDecimal planNum = BigDecimal.valueOf(allocationPlanDetail.getRequestQuantity());
            int event = nowNum.compareTo(planNum);
            /**
             * event = -1 : planNuM > nowNum
             * event =  0 : planNuM = nowNum
             * event =  1 : planNuM < nowNum
             */
            if (event < 0) {
                return Result.failure("物料：" + allocationPlanDetail.getMaterialCoding() + " 库存不足，请重拟领用单！");
            }
        }

        return Result.success();
    }

    @Override
    public Result updateOutboundRecordAndInventory(AllocationPlan allocationPlan) {
        String docNum = allocationPlan.getAllocationNumber();
        String warehousId = allocationPlan.getSendWarehouse();

        List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(docNum);

        for (AllocationPlanDetail allocationPlanDetail : allocationPlanDetailsList) {
            //如果批准数量不为空并不为零
            if (ObjectUtil.isNotNull(allocationPlanDetail.getCalibrationQuantity()) && BigDecimal.valueOf(allocationPlanDetail.getCalibrationQuantity()).compareTo(BigDecimal.valueOf(0)) > 0) {

                //请调数量
                BigDecimal requisitionQuantity = BigDecimal.valueOf(allocationPlanDetail.getRequestQuantity());
                //准调数量
                BigDecimal approvalsQuantity = BigDecimal.valueOf(allocationPlanDetail.getCalibrationQuantity());

                if (requisitionQuantity.compareTo(approvalsQuantity) != 0) {
                    List<OutboundRecord> outboundRecordList = outboundRecordService.getOutboundRecordListByDocNumAndWarehouseId(docNum, warehousId);
                    Result result = handleOutboundRecordAndInventory(outboundRecordList, allocationPlanDetail.getCalibrationQuantity());
                    if (!result.isOk()) {
                        return result;
                    }
                }
            }
        }
        return Result.success();
    }

    @Override
    public Result<Page<AllocationDetailVo>> allocationDetail(AllocationDetailPage page) {
        Page<AllocationDetailVo> pageData = allocationPlanMapper.allocationDetail(page);
        int i = 1;
        for (AllocationDetailVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void allocationDetailExport(AllocationDetailPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<AllocationDetailVo> pageData = allocationPlanMapper.allocationDetail(page);
        int i = 1;
        for (AllocationDetailVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        params.put("gmtStart", page.getGmtStart());
        params.put("gmtEnd", page.getGmtEnd());
        String templatePath = ossProperties.getPath() + "templates/allocationDetail.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "调拨明细汇总表.xlsx", params, request, response);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result add(AddAllocationPlanAndDetailsDTO addAllocationPlanAndDetailsDTO) {
        AddAllocationPlanDTO addAllocationPlanDTO = addAllocationPlanAndDetailsDTO.getAddAllocationPlanDTO();
        List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList = addAllocationPlanAndDetailsDTO.getAddAllocationPlanDetailDTOList();
        Result result = addAllocationPlan(addAllocationPlanDTO);
        if (!result.isOk()) {
            return Result.failure("新增调拨计失败！");
        }
        AllocationPlan allocationPlan = (AllocationPlan) result.getData();
        String docNum = allocationPlan.getAllocationNumber();
        if (ObjectUtil.isNotNull(addAllocationPlanDetailDTOList)) {
            for (AddAllocationPlanDetailDTO addAllocationPlanDetailDTO : addAllocationPlanDetailDTOList) {
                addAllocationPlanDetailDTO.setAllocationNumber(docNum);
            }
        }
        allocationPlanDetailService.addAllocationPlanDetails(addAllocationPlanDetailDTOList);
        return result;
    }

    @Override
    public Result delete(Integer id) {
        AllocationPlan allocationPlan = allocationPlanMapper.selectById(id);
        if (ObjectUtil.isNull(allocationPlan)) {
            return Result.failure("单据不存在！");
        }
        boolean delete = this.removeById(id);
        //主表删除成功,删除明细
        if (delete) {
            allocationPlanDetailService.removeByPlanNos(Collections.singletonList(allocationPlan.getAllocationNumber()));
        }
        return Result.success("删除成功");
    }

    @Override
    public Result deleteByPlanNos(List<String> planNos) {
        List<AllocationPlan> allocationPlans = allocationPlanMapper.selectList(Wrappers.<AllocationPlan>lambdaQuery().in(AllocationPlan::getAllocationNumber, planNos));
        if (CollectionUtil.isNotEmpty(allocationPlans)) {
            List<Integer> ids = allocationPlans.stream().map(AllocationPlan::getId).collect(Collectors.toList());
            boolean delete = this.removeByIds(ids);
            //主表删除成功,删除明细
            if (delete) {
                allocationPlanDetailService.removeByPlanNos(planNos);
            }
        }
        return Result.success("删除成功");
    }

    /**
     * 完整审批时-如果请调数量和准调数量不一致--回滚库存
     * 出库明细单据已更新,需要根据批准数量-应出数量=出库数量回滚部分库存并更新出库记录
     *
     * @param outboundRecordList 需要更新的出库记录
     * @param newOutQuantity     从请调数量改为准调数量
     * @return
     */
    public Result handleOutboundRecordAndInventory(List<OutboundRecord> outboundRecordList, Double newOutQuantity) {

        try {
            //TODO 调拨入库
            /**
             * 根据出库记录list和新的数量(批准数量)
             */
            BigDecimal tempNum = BigDecimal.valueOf(newOutQuantity);
            UpdateOutboundRecordDTO updateOutboundRecordDTO = new UpdateOutboundRecordDTO();

            for (OutboundRecord outboundRecord : outboundRecordList) {
                int event = tempNum.compareTo(BigDecimal.valueOf(0));
                //遍历出库记录，只要tempNum不等于0，就将此条记录的数量加给tempNum.且此条数据不作变更
                if (tempNum.compareTo(BigDecimal.valueOf(0)) > 0) {
                    //判断此时tempNum是否已经小于此条数据的出库数量
                    if (tempNum.compareTo(BigDecimal.valueOf(outboundRecord.getOutQuantity())) < 0) {

                        //更新库存--此条数据的数量减去tempNum
                        BigDecimal newInventory = BigDecimal.valueOf(outboundRecord.getOutQuantity()).subtract(tempNum);
                        AddInventoryInformationDTO addInventoryInformationDTO = new AddInventoryInformationDTO();

                        //存入新数量
                        InventoryInformation inventoryInformation = inventoryInformationService.getInventoryInformation(outboundRecord.getMaterialCoding(), outboundRecord.getBatch(), outboundRecord.getCargoSpaceId());
                        if (ObjectUtil.isEmpty(inventoryInformation)) {
                            return Result.failure("未找到库存信息");
                        }
                        BeanUtil.copyProperties(inventoryInformation, addInventoryInformationDTO);
                        addInventoryInformationDTO.setInventoryCredit(newInventory.doubleValue());
                        Result result = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                        if (result.isOk()) {
                            //更新成功,明细中的数量改为tempNum
                            outboundRecord.setOutQuantity(tempNum.doubleValue());
                        } else {
                            log.error("回滚库存失败!");
                            return Result.failure("回滚库存失败");
                        }
                    } else {
                        //当tempNum不为零且大于当前数据的数量，temp数量减去此数量,出库记录及库存信息不更新
                        tempNum = tempNum.subtract(BigDecimal.valueOf(outboundRecord.getOutQuantity()));
                    }
                } else {
                    //当tempNum等于0,剩余的出库记录全部回滚库存--失败无补偿手段
                    AddInventoryInformationDTO addInventoryInformationDTO = new AddInventoryInformationDTO();
                    InventoryInformation inventoryInformation = inventoryInformationService.getInventoryInformation(outboundRecord.getMaterialCoding(), outboundRecord.getBatch(), outboundRecord.getCargoSpaceId());
                    if (ObjectUtil.isEmpty(inventoryInformation)) {
                        return Result.failure("未找到库存信息");
                    }
                    BeanUtil.copyProperties(inventoryInformation, addInventoryInformationDTO);
                    addInventoryInformationDTO.setInventoryCredit(outboundRecord.getOutQuantity());
                    Result result = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                    if (!result.isOk()) {
                        return result;
                    }
                }
                //更新明细
                BeanUtil.copyProperties(outboundRecord, updateOutboundRecordDTO);
                Result result = outboundRecordService.updateOutboundRecord(updateOutboundRecordDTO);
                if (result.isOk()) {
                    return Result.success("出库记录处理成功！");
                } else {
                    return Result.failure("出库记录处理失败！");
                }
            }
        } catch (Exception e) {
            log.error("回滚库存或更新详细信息异常", e);
            return Result.failure("回滚库存或更新详细信息失败");
        }
        return Result.success();
    }
}
