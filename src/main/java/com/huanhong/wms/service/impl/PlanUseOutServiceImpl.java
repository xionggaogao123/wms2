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
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.PlanUseOutVO;
import com.huanhong.wms.mapper.PlanUseOutMapper;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IOutboundRecordService;
import com.huanhong.wms.service.IPlanUseOutDetailsService;
import com.huanhong.wms.service.IPlanUseOutService;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Resource
    private IPlanUseOutDetailsService planUseOutDetailsService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IOutboundRecordService outboundRecordService;

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

        query.like(StringUtils.isNotBlank(planUseOutVO.getRecipient()), "recipient", planUseOutVO.getRecipient());

        query.like(StringUtils.isNotBlank(planUseOutVO.getWarehouseId()), "warehouse_id", planUseOutVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(planUseOutVO.getLibrarian()), "librarian", planUseOutVO.getLibrarian());

        query.eq(ObjectUtil.isNotNull(planUseOutVO.getOutStatus()), "out_status", planUseOutVO.getOutStatus());

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

    /**
     * @param planUseOutPage
     * @param planUseOutVO
     * @return
     */
    @Override
    public Page<PlanUseOut> pageFuzzyQueryPDA(Page<PlanUseOut> planUseOutPage, PlanUseOutVO planUseOutVO) {
        //新建QueryWrapper对象
        QueryWrapper<PlanUseOut> query = new QueryWrapper<>();

        //根据id排序
        query.orderByAsc("id");

        //单据编号
        query.like(ObjectUtil.isNotNull(planUseOutVO.getDocumentNumber()), "document_number", planUseOutVO.getDocumentNumber());

        //仓库
        query.like(ObjectUtil.isNotNull(planUseOutVO.getWarehouseId()), "warehouse_id", planUseOutVO.getWarehouseId());

        //单据状态
        if (ObjectUtil.isNotNull(planUseOutVO.getOutStatus()) && planUseOutVO.getOutStatus() == 0) {
            query.eq("out_status", 0).or().eq("out_status", 1);
        } else if (ObjectUtil.isNotNull(planUseOutVO.getOutStatus()) && planUseOutVO.getOutStatus() == 1) {
            query.eq("out_status", 2);
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
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getProcessInstanceId())) {
            planUseOutOld.setProcessInstanceId(updatePlanUseOutDTO.getProcessInstanceId());
        }

        //单据状态
        if (ObjectUtil.isNotNull(updatePlanUseOutDTO.getStatus())) {
            planUseOutOld.setStatus(updatePlanUseOutDTO.getStatus());
        }
        //计划类别
        if (ObjectUtil.isNotNull(updatePlanUseOutDTO.getPlanClassification())) {
            planUseOutOld.setPlanClassification(updatePlanUseOutDTO.getPlanClassification());
        }
        //领用单位
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRequisitioningUnit())) {
            planUseOutOld.setRequisitioningUnit(updatePlanUseOutDTO.getRequisitioningUnit());
        }
        //库房员
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getLibrarian())) {
            planUseOutOld.setLibrarian(updatePlanUseOutDTO.getLibrarian());
        }
        //费用承担单位
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getCostBearingUnit())) {
            planUseOutOld.setCostBearingUnit(updatePlanUseOutDTO.getCostBearingUnit());
        }
        //费用项目
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getExpenseItem())) {
            planUseOutOld.setExpenseItem(updatePlanUseOutDTO.getExpenseItem());
        }
        //物资用途
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getMaterialUse())) {
            planUseOutOld.setMaterialUse(updatePlanUseOutDTO.getMaterialUse());
        }
        //领用用途
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRequisitionUse())) {
            planUseOutOld.setRequisitionUse(updatePlanUseOutDTO.getRequisitionUse());
        }
        //领用人
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRecipient())) {
            planUseOutOld.setRecipient(updatePlanUseOutDTO.getRecipient());
        }
        //已完成的明细id,格式：以逗号隔开的字符串
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getDetailIds())) {
            //获取已经存储的已完成明细Id
            List<String> listOld = new ArrayList<>();
            //更新的Id
            List<String> listPSL = new ArrayList<>();
            listPSL = Collections.singletonList(updatePlanUseOutDTO.getDetailIds());
            if (ObjectUtil.isNotNull(planUseOutOld.getDetailIds())) {
                String s = planUseOutOld.getDetailIds();
                listOld = Arrays.stream(StringUtils.split(s, ",")).map(s1 -> s1.trim()).collect(Collectors.toList());
            }
            listPSL = Stream.of(listPSL, listOld)
                    .flatMap(Collection::stream).distinct().collect(Collectors.toList());
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            planUseOutOld.setDetailIds(resultString);
        } else {
            List<String> listPSL = new ArrayList<>();
            listPSL = Collections.singletonList(updatePlanUseOutDTO.getDetailIds());
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            planUseOutOld.setDetailIds(resultString);
        }

        //出库状态
        if (ObjectUtil.isNotNull(updatePlanUseOutDTO.getOutStatus())) {
            planUseOutOld.setOutStatus(updatePlanUseOutDTO.getOutStatus());
        }

        //备注
        if (StringUtils.isNotBlank(updatePlanUseOutDTO.getRemark())) {
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
        queryWrapper.eq("document_number", docNumber);
        queryWrapper.eq("warehouse_id", warhouseId);
        PlanUseOut planUseOut = planUseOutMapper.selectOne(queryWrapper);
        return planUseOut;
    }


    @Override
    public PlanUseOut getPlanUseOutByProcessInstanceId(String processInstanceId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_instance_id", processInstanceId);
        PlanUseOut planUseOut = planUseOutMapper.selectOne(queryWrapper);
        return planUseOut;
    }

    @Override
    public Result addOutboundRecordUpdateInventory(PlanUseOut planUseOut) {
        List<PlanUseOutDetails> planUseOutDetailsList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId());
        //留存出库记录
        AddOutboundRecordDTO addOutboundRecordDTO = new AddOutboundRecordDTO();
        /**
         * 获取当前库存是否满足领用
         * 1.warehouseId和materialCoding
         */
        if (ObjectUtil.isNotEmpty(planUseOutDetailsList)) {
            for (PlanUseOutDetails planUseOutDetails : planUseOutDetailsList) {
                BigDecimal nowNum = BigDecimal.valueOf(inventoryInformationService.getNumByMaterialCodingAndWarehouseId(planUseOutDetails.getMaterialCoding(), planUseOutDetails.getWarehouseId()));
                BigDecimal planNum = BigDecimal.valueOf(planUseOutDetails.getRequisitionQuantity());
                int event = nowNum.compareTo(planNum);
                /**
                 * event = -1 : planNuM > nowNum
                 * event =  0 : planNuM = nowNum
                 * event =  1 : planNuM < nowNum
                 */
                if (event >= 0) {
                    BigDecimal tempNum = planNum;
                    List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndWarehouseId(planUseOutDetails.getMaterialCoding(), planUseOutDetails.getWarehouseId());
                    for (InventoryInformation inventoryInformation : inventoryInformationList) {
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
                                } else {
                                    log.error("更新库存失败");
                                    return Result.failure("更新库存失败");
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    /**
                     * 单据进入流程时，根据领用数量生成出库记录
                     * 1.原单据编号
                     * 2.库房ID
                     * 3.物料编码
                     * 4.状态：0-审批中（锁库存）1-审批生效（出库）
                     * 5.出库类型：1-领料出库 2-调拨出库
                     */
                    addOutboundRecordDTO.setDocumentNumber(planUseOutDetails.getUsePlanningDocumentNumber());
                    addOutboundRecordDTO.setWarehouseId(planUseOutDetails.getWarehouseId());
                    addOutboundRecordDTO.setMaterialCoding(planUseOutDetails.getMaterialCoding());
                    addOutboundRecordDTO.setStatus(0);
                    addOutboundRecordDTO.setOutType(1);
                    //放入新增出库记录List
                    Result result = outboundRecordService.addOutboundRecord(addOutboundRecordDTO);

                    if (!result.isOk()) {
                        return Result.failure("新增库存记录失败");
                    } else {
                        return Result.success("新增库存记录成功");
                    }
                } else {
                    return Result.failure("物料：" + planUseOutDetails.getMaterialCoding() + " 库存不足，请重拟领用单！");
                }
            }
        } else {
            return Result.failure("未查询到明细单据信息");
        }
        return Result.failure("未知错误");
    }

    @Override
    public Result checkStock(PlanUseOut planUseOut) {
        List<PlanUseOutDetails> planUseOutDetailsList = planUseOutDetailsService
                .getListPlanUseOutDetailsByDocNumberAndWarehosue(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId());
        if (ObjectUtil.isEmpty(planUseOutDetailsList)) {
            return  Result.failure("未查询到明细单据信息");
        }
        for (PlanUseOutDetails planUseOutDetails : planUseOutDetailsList) {
            BigDecimal nowNum = BigDecimal.valueOf(inventoryInformationService.getNumByMaterialCodingAndWarehouseId(planUseOutDetails.getMaterialCoding(), planUseOutDetails.getWarehouseId()));
            BigDecimal planNum = BigDecimal.valueOf(planUseOutDetails.getRequisitionQuantity());
            int event = nowNum.compareTo(planNum);
            /**
             * event = -1 : planNuM > nowNum
             * event =  0 : planNuM = nowNum
             * event =  1 : planNuM < nowNum
             */
            if (event < 0) {
                return Result.failure("物料：" + planUseOutDetails.getMaterialCoding() + " 库存不足，请重拟领用单！");
            }
        }

        return Result.success();
    }

    @Override
    public Result updateOutboundRecordAndInventory(PlanUseOut planUseOut) {
        //获取此单据下的明细单，校验批准数量是否等于应出数量，若不同回滚库存并更新详细信息
        String docNum = planUseOut.getDocumentNumber();
        String warehousId = planUseOut.getWarehouseId();
        List<PlanUseOutDetails> planUseOutDetailsList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(docNum, warehousId);
        for (PlanUseOutDetails planUseOutDetails : planUseOutDetailsList) {
            //如果批准数量不为空并不为零
            if (ObjectUtil.isNotNull(planUseOutDetails.getApprovalsQuantity()) && BigDecimal.valueOf(planUseOutDetails.getApprovalsQuantity()).compareTo(BigDecimal.valueOf(0)) > 0) {
                //领用数量
                BigDecimal requisitionQuantity = BigDecimal.valueOf(planUseOutDetails.getRequisitionQuantity());
                //批准数量
                BigDecimal approvalsQuantity = BigDecimal.valueOf(planUseOutDetails.getApprovalsQuantity());
                if (requisitionQuantity.compareTo(approvalsQuantity) != 0) {
                    List<OutboundRecord> outboundRecordList = outboundRecordService.getOutboundRecordListByDocNumAndWarehouseId(docNum, warehousId);
                    Result result = handleOutboundRecordAndInventory(outboundRecordList, planUseOutDetails.getApprovalsQuantity());
                    if(!result.isOk()){
                        return result;
                    }
                }
            }
        }

        return Result.success();
    }

    /**
     * 完整审批时-如果批准数量和应出数量不一致--回滚库存
     * 出库明细单据已更新,需要根据批准数量-应出数量=出库数量回滚部分库存并更新出库记录
     *
     * @param outboundRecordList 需要更新的出库记录
     * @param newOutQuantity     从应出数量改为批准数量
     * @return
     */
    public Result handleOutboundRecordAndInventory(List<OutboundRecord> outboundRecordList, Double newOutQuantity) {

        try {
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
