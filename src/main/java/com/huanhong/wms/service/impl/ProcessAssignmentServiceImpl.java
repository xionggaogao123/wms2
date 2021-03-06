package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.common.units.task.HistoryTaskVo;
import com.huanhong.common.units.task.RejectParam;
import com.huanhong.common.units.task.TaskCompleteParam;
import com.huanhong.common.units.task.TaskQueryUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.UpPaStatus;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.param.ApproveParam;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;
import com.huanhong.wms.entity.param.StartProcessParam;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程任务表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-17
 */
@Slf4j
@Service
public class ProcessAssignmentServiceImpl extends SuperServiceImpl<ProcessAssignmentMapper, ProcessAssignment> implements IProcessAssignmentService {
    @Resource
    private ProcessAssignmentMapper processAssignmentMapper;
    @Resource
    private AllocationPlanMapper allocationPlanMapper;
    @Resource
    private ArrivalVerificationMapper arrivalVerificationMapper;
    @Resource
    private ProcurementPlanMapper procurementPlanMapper;
    @Resource
    private RequirementsPlanningMapper requirementsPlanningMapper;
    @Resource
    private PlanUseOutMapper planUseOutMapper;
    @Resource
    private EnterWarehouseMapper enterWarehouseMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private IMessageService messageService;
    @Resource
    private IPlanUseOutService planUseOutService;
    @Resource
    private IAllocationPlanService allocationPlanService;

    @Resource
    private IPlanUseOutDetailsService planUseOutDetailsService;

    @Resource
    private IOutboundRecordService outboundRecordService;

    @Resource
    private IEnterWarehouseService enterWarehouseService;

    @Resource
    private IEnterWarehouseDetailsService enterWarehouseDetailsService;

    @Resource
    private PlanUseOutDetailsMapper planUseOutDetailsMapper;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Resource
    private TemporaryOutWarehouseMapper temporaryOutWarehouseMapper;

    @Resource
    private IMaterialService materialService;

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @Resource
    private MakeInventoryMapper makeInventoryMapper;

    @Resource
    private MakeInventoryReportMapper makeInventoryReportMapper;

    @Resource
    private IMakeInventoryReportDetailsService makeInventoryReportDetailsService;
    @Resource
    private IAllocationOutService allocationOutService;
    @Resource
    private IAllocationEnterService allocationEnterService;
    @Resource
    private IAllocationPlanDetailService allocationPlanDetailService;
    @Resource
    private BalanceLibraryRecordMapper balanceLibraryRecordMapper;
    @Resource
    private BalanceLibraryDetailMapper balanceLibraryDetailMapper;
    @Resource
    private ProcurementPlanDetailsMapper procurementPlanDetailsMapper;
    @Resource
    private BalanceLibraryMapper balanceLibraryMapper;

    @Resource
    private MaterialPriceService materialPriceService;

    @Override
    public Result<Integer> syncProcessAssignment() {
        Result taskResult = TaskQueryUtil.list();
        if (!taskResult.isOk()) {
            log.error("请求当前任务错误");
            return Result.failure("请求当前任务错误");

        }
        if (taskResult.getData() == null) {
            return Result.failure("当前没有任务");
        }
        List<HistoryTaskVo> array = JSONObject.parseArray(taskResult.getData().toString(), HistoryTaskVo.class);
        if (array.size() == 0) {
            log.error("当前没有任务");
            return Result.failure("当前没有任务");
        }
        List<ProcessAssignment> list = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();
        for (HistoryTaskVo historyTaskVo : array) {
            //查询是否已同步该任务
            int count = processAssignmentMapper.selectCount(new QueryWrapper<ProcessAssignment>().eq("task_id", historyTaskVo.getId()));
            if (count > 0) {
                continue;
            }
            String type = historyTaskVo.getProcessDefinitionKey();
            String processInstanceId = historyTaskVo.getProcessInstanceId();
            String taskId = historyTaskVo.getId();
            ProcessAssignment p = new ProcessAssignment();
            p.setProcessInstanceId(processInstanceId);
            p.setProcessDefinitionKey(type);
            p.setProcessName(historyTaskVo.getName());
            p.setTaskDefinitionKey(type);
            p.setUserAccount(historyTaskVo.getAssignee());
            p.setUserName("");
            p.setTaskDefinitionKey(historyTaskVo.getTaskDefinitionKey());
            p.setTaskId(taskId);
            p.setStartTime(historyTaskVo.getStartTime());
            p.setStatus(0);
            switch (type) {
                //出库
                case "plan_use_out":
                    List<PlanUseOut> planUseOuts = planUseOutMapper.selectList(new QueryWrapper<PlanUseOut>().eq("process_instance_id", processInstanceId));
                    if (planUseOuts.size() > 0) {
                        PlanUseOut planUseOut = planUseOuts.get(0);
                        p.setDocumentNumber(planUseOut.getDocumentNumber());
                        p.setName("出库");
                        p.setPlanClassification(planUseOut.getPlanClassification());
                        p.setObjectId(planUseOut.getId());
                        p.setObjectType(1);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    入库
                case "enter_warehouse":
                    List<EnterWarehouse> enterWarehouses = enterWarehouseMapper.selectList(new QueryWrapper<EnterWarehouse>().eq("process_instance_id", processInstanceId));
                    if (enterWarehouses.size() > 0) {
                        EnterWarehouse enterWarehouse = enterWarehouses.get(0);
                        p.setDocumentNumber(enterWarehouse.getDocumentNumber());
                        p.setName("入库");
                        p.setPlanClassification(enterWarehouse.getPlanClassification());
                        p.setObjectId(enterWarehouse.getId());
                        p.setObjectType(2);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    调拨
                case "allocation_plan":
                    List<AllocationPlan> allocationPlans = allocationPlanMapper.selectList(new QueryWrapper<AllocationPlan>().eq("process_instance_id", processInstanceId));
                    if (allocationPlans.size() > 0) {
                        AllocationPlan allocationPlan = allocationPlans.get(0);
                        p.setDocumentNumber(allocationPlan.getAllocationNumber());
                        p.setName("调拨");
                        p.setObjectId(allocationPlan.getId());
                        p.setObjectType(3);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    采购计划
                case "procurement_plan":
                    List<ProcurementPlan> procurementPlans = procurementPlanMapper.selectList(new QueryWrapper<ProcurementPlan>().eq("process_instance_id", processInstanceId));
                    if (procurementPlans.size() > 0) {
                        ProcurementPlan procurementPlan = procurementPlans.get(0);
                        p.setDocumentNumber(procurementPlan.getPlanNumber());
                        p.setName("采购计划");
                        p.setPlanClassification(procurementPlan.getPlanClassification());
                        p.setObjectId(procurementPlan.getId());
                        p.setObjectType(4);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    需求计划
                case "requirements_planning":
                    List<RequirementsPlanning> requirementsPlannings = requirementsPlanningMapper.selectList(new QueryWrapper<RequirementsPlanning>().eq("process_instance_id", processInstanceId));
                    if (requirementsPlannings.size() > 0) {
                        RequirementsPlanning requirementsPlanning = requirementsPlannings.get(0);
                        p.setDocumentNumber(requirementsPlanning.getPlanNumber());
                        p.setName("需求计划");
                        p.setPlanClassification(requirementsPlanning.getPlanClassification());
                        p.setObjectId(requirementsPlanning.getId());
                        p.setObjectType(5);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    到货检验
                case "arrival_verification":
                    List<ArrivalVerification> arrivalVerifications = arrivalVerificationMapper.selectList(new QueryWrapper<ArrivalVerification>().eq("process_instance_id", processInstanceId));
                    if (arrivalVerifications.size() > 0) {
                        ArrivalVerification arrivalVerification = arrivalVerifications.get(0);
                        p.setDocumentNumber(arrivalVerification.getVerificationDocumentNumber());
                        p.setName("到货检验");
                        p.setPlanClassification(arrivalVerification.getPlanClassification());
                        p.setObjectId(arrivalVerification.getId());
                        p.setObjectType(6);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    盘点计划
                case "make_inventory":
                    List<MakeInventory> makeInventories = makeInventoryMapper.selectList(new QueryWrapper<MakeInventory>().eq("process_instance_id", processInstanceId));
                    if (makeInventories.size() > 0) {
                        MakeInventory makeInventory = makeInventories.get(0);
                        p.setDocumentNumber(makeInventory.getDocumentNumber());
                        p.setName("盘点计划");
                        p.setPlanClassification(makeInventory.getPlanClassification());
                        p.setObjectId(makeInventory.getId());
                        p.setObjectType(7);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                //    盘点报告
                case "make_inventory_report":
                    List<MakeInventoryReport> makeInventoryReports = makeInventoryReportMapper.selectList(new QueryWrapper<MakeInventoryReport>().eq("process_instance_id", processInstanceId));
                    if (makeInventoryReports.size() > 0) {
                        MakeInventoryReport makeInventoryReport = makeInventoryReports.get(0);
                        p.setDocumentNumber(makeInventoryReport.getReportNumber());
                        p.setName("盘点报告");
                        p.setPlanClassification(makeInventoryReport.getPlanClassification());
                        p.setObjectId(makeInventoryReport.getId());
                        p.setObjectType(8);
                    } else {
                        processingData(listMap, processInstanceId, taskId);
                        continue;
                    }
                    break;
                default:
                    log.warn("暂未处理的流程：{}", type);
                    break;
            }
            list.add(p);
        }

        if (list.size() > 0) {
            log.debug("新增数据量：" + list.size());
            this.saveBatch(list);
        }
        if (listMap.size() > 0) {
            for (Map.Entry<String, List<String>> entry : listMap.entrySet()) {
                String processInstanceId = entry.getKey();
                List<String> taskIds = entry.getValue();
                log.debug(StrUtils.format("pid: " + processInstanceId + "=======taskIds:" + JSONObject.toJSONString(taskIds)));
                try {
                    TaskQueryUtil.deleteProcess(processInstanceId, "删除无关联流程");
                } catch (Exception e) {
                    log.error("删除异常");
                }
                try {
                    TaskQueryUtil.deleteTasks(taskIds);
                } catch (Exception e) {
                    log.error("删除异常");
                }

            }
        }


        return null;
    }

    @Override
    public Result<Page<ProcessAssignment>> selectPage(Page<ProcessAssignment> page, ProcessAssignmentParam param) {
        QueryWrapper<ProcessAssignment> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (param.getStatus() == null || param.getStatus() > 2) {
            query.in("status", 1, 2);
        } else {
            query.eq("status", param.getStatus());
        }
        if (param.getObjectType() != null && param.getObjectType() > 0 && param.getObjectType() < 5) {
            query.eq("object_type", param.getObjectType());
        }
        if (StrUtil.isNotBlank(param.getDocumentNumber())) {
            query.like("document_number", param.getDocumentNumber());
        }
        if (StrUtil.isNotBlank(param.getName())) {
            query.like("name", param.getName());
        }
        query.eq("user_account", param.getUserAccount());
        return Result.success(processAssignmentMapper.selectPage(page, query));
    }

    @Override
    public Result<Integer> updateProcessAssignmentStatusByParam(UpPaStatus up) {
        if (StrUtil.isBlank(up.getTaskId())) {
            return Result.failure("任务ID不得为空");
        }
        if (up.getStatus() == null || up.getStatus() > 2 || up.getStatus() < 1) {
            return Result.failure("审批参数有误");
        }
        ProcessAssignment processAssignment = processAssignmentMapper.selectList(new QueryWrapper<ProcessAssignment>().eq("task_id", up.getTaskId()).eq("status", 0)).get(0);
        if (processAssignment == null) {
            return Result.failure("该任务不存在");
        }
        ProcessAssignment pa = new ProcessAssignment();
        pa.setId(processAssignment.getId());
        pa.setStatus(up.getStatus());
        pa.setRemark(up.getReason() == null ? "" : up.getReason());
        int update = processAssignmentMapper.updateById(pa);
        if (update > 0) {
            return Result.success();
        }
        return Result.failure("请稍后重试");
    }

    @Override
    public Result<Integer> approveTaskByParam(ApproveParam param) {
        if (param.getType() != 1 && param.getType() != 2) {
            return Result.failure(ErrorCode.PARAM_ERROR, "请选择类型");
        }
        if (StrUtil.isBlank(param.getSignPassWord())) {
            return Result.failure(ErrorCode.PARAM_ERROR, "签名密码为空");
        }
        User user = userMapper.selectById(param.getUserId());
        if (StrUtil.isBlank(user.getSignUrl())) {
            return Result.failure(ErrorCode.PARAM_ERROR, "未设置用户签名");
        }
        if (StrUtil.isBlank(user.getSignPassword())) {
            return Result.failure(ErrorCode.PARAM_ERROR, "未设置签名密码");
        }
        if (StrUtil.isBlank(param.getSignPassWord())) {
            return Result.failure(ErrorCode.PARAM_ERROR, "请输入签名密码");
        }
        if (!user.getSignPassword().equals(param.getSignPassWord())) {
            return Result.failure(ErrorCode.PARAM_ERROR, "签名密码不正确");
        }
        if (StrUtil.isBlank(param.getTaskId())) {
            return Result.failure("任务ID不得为空");
        }
        String taskId = param.getTaskId();
        ProcessAssignment processAssignment = processAssignmentMapper.selectOne(new QueryWrapper<ProcessAssignment>().eq("task_id", taskId).eq("status", 0).last("limit 1"));
        if (processAssignment == null) {
            return Result.failure("该任务不存在");
        }
        ProcessAssignment pa = new ProcessAssignment();
        pa.setId(processAssignment.getId());
        pa.setStatus(param.getType());
        pa.setRemark(param.getMessage() == null ? "" : param.getMessage());
        int update = 0;
        //审批通过
        if (param.getType() == 1) {
            TaskCompleteParam completeParam = new TaskCompleteParam();
            completeParam.setTaskId(taskId);
            completeParam.setMessage(param.getMessage());
            completeParam.setVariables(param.getVariables());
            completeParam.setUsername(param.getUsername());
            completeParam.setName(param.getName());
            Result complete = TaskQueryUtil.complete(completeParam);
            if (!complete.isOk()) {
                return Result.failure(complete.getMessage());
            }
            update = processAssignmentMapper.updateById(pa);
        }
        //驳回
        if (param.getType() == 2) {
            RejectParam rejectParam = new RejectParam();
            rejectParam.setTaskId(taskId);
            rejectParam.setIsFirst(param.getIsFirst());
            rejectParam.setUsername(param.getUsername());
            rejectParam.setMessage(param.getMessage());
            rejectParam.setVariables(param.getVariables());
            rejectParam.setName(param.getName());
            Result rejectResult = TaskQueryUtil.reject(rejectParam);
            if (!rejectResult.isOk()) {
                return Result.failure(rejectResult.getMessage());
            }

            update = processAssignmentMapper.updateById(pa);
            if (null != rejectResult.getData() && Convert.toInt(rejectResult.getData()) == 1) {
                // 需要更新主表状态为驳回并加上驳回原因
                Integer id = processAssignment.getObjectId();
                int f;
                // 更新数据
                switch (processAssignment.getProcessDefinitionKey()) {
                    //出库
                    case "plan_use_out":
                        /**
                         * 获取出库单信息
                         */
                        PlanUseOut planUseOut = planUseOutMapper.selectById(id);
                        if (!ObjectUtil.isNotNull(planUseOut)) {
                            return Result.failure("出库单不存在或已被删除,无法完成");
                        }
                        PlanUseOut tempPlanUseOut = new PlanUseOut();
                        tempPlanUseOut.setId(id);
                        //提交审批时 将更新数量  单据状态由审批中改为驳回
                        tempPlanUseOut.setStatus(5);
                        tempPlanUseOut.setRejectReason(param.getMessage());
                        f = planUseOutMapper.updateById(tempPlanUseOut);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }

                        break;
                    //    入库
                    case "enter_warehouse":
                        EnterWarehouse enterWarehouse = enterWarehouseMapper.selectById(id);

                        if (!ObjectUtil.isNotNull(enterWarehouse)) {
                            return Result.failure("采购入库单不存在或已被删除,无法完成");
                        }
                        EnterWarehouse tempEnterWarehouse = new EnterWarehouse();
                        tempEnterWarehouse.setId(id);
                        //单据状态由审批中改为驳回
                        tempEnterWarehouse.setState(5);
                        tempEnterWarehouse.setRejectReason(param.getMessage());
                        f = enterWarehouseMapper.updateById(tempEnterWarehouse);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }

                        break;
                    //    调拨
                    case "allocation_plan":
                        AllocationPlan allocationPlan = allocationPlanMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(allocationPlan)) {
                            return Result.failure("调拨计划单不存在或已被删除,无法完成");
                        }
                        Result result = allocationPlanService.updateOutboundRecordAndInventory(allocationPlan);
                        if (!result.isOk()) {
                            return result;
                        }
                        AllocationPlan tempAllocationPlan = new AllocationPlan();
                        tempAllocationPlan.setId(id);
                        tempAllocationPlan.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempAllocationPlan.setPlanStatus(5);
                        f = allocationPlanMapper.updateById(tempAllocationPlan);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }

                        break;
                    //    采购计划
                    case "procurement_plan":
                        ProcurementPlan procurementPlan = procurementPlanMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(procurementPlan)) {
                            return Result.failure("采购计划单不存在或已被删除,无法完成");
                        }
                        ProcurementPlan tempProcurementPlan = new ProcurementPlan();
                        tempProcurementPlan.setId(id);
                        tempProcurementPlan.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempProcurementPlan.setStatus(5);
                        f = procurementPlanMapper.updateById(tempProcurementPlan);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    //    需求计划
                    case "requirements_planning":
                        RequirementsPlanning requirementsPlanning = requirementsPlanningMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(requirementsPlanning)) {
                            return Result.failure("需求计划单不存在或已被删除,无法完成");
                        }
                        RequirementsPlanning tempRequirementsPlanning = new RequirementsPlanning();
                        tempRequirementsPlanning.setId(id);
                        tempRequirementsPlanning.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempRequirementsPlanning.setPlanStatus(5);
                        f = requirementsPlanningMapper.updateById(tempRequirementsPlanning);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    //    到货检验
                    case "arrival_verification":
                        ArrivalVerification arrivalVerification = arrivalVerificationMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(arrivalVerification)) {
                            return Result.failure("到货检验单不存在或已被删除,无法完成");
                        }
                        ArrivalVerification tempArrivalVerification = new ArrivalVerification();
                        tempArrivalVerification.setId(id);
                        tempArrivalVerification.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempArrivalVerification.setPlanStatus(5);
                        f = arrivalVerificationMapper.updateById(tempArrivalVerification);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    //    临库入库
                    case "temporary_enter_warehouse":
                        TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(temporaryEnterWarehouse)) {
                            return Result.failure("临库入库单不存在或已被删除,无法完成");
                        }
                        TemporaryEnterWarehouse tempTemporaryEnterWarehouse = new TemporaryEnterWarehouse();
                        tempTemporaryEnterWarehouse.setId(id);
                        tempTemporaryEnterWarehouse.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempTemporaryEnterWarehouse.setState(5);
                        f = temporaryEnterWarehouseMapper.updateById(tempTemporaryEnterWarehouse);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    //    临库出库
                    case "temporary_out_warehouse":
                        TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(temporaryOutWarehouse)) {
                            return Result.failure("临库出库单不存在或已被删除,无法完成");
                        }

                        TemporaryOutWarehouse tempTemporaryOutWarehouse = new TemporaryOutWarehouse();
                        tempTemporaryOutWarehouse.setId(id);
                        tempTemporaryOutWarehouse.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempTemporaryOutWarehouse.setStatus(5);
                        f = temporaryOutWarehouseMapper.updateById(tempTemporaryOutWarehouse);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    //    盘点
                    case "make_inventory":
                        MakeInventory makeInventory = makeInventoryMapper.selectById(id);

                        if (!ObjectUtil.isNotEmpty(makeInventory)) {
                            return Result.failure("盘点单不存在或已被删除,无法完成");
                        }

                        MakeInventory tempMakeInventory = new MakeInventory();
                        tempMakeInventory.setId(id);
                        tempMakeInventory.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempMakeInventory.setPlanStatus(5);
                        f = makeInventoryMapper.updateById(tempMakeInventory);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    //    盘点报告
                    case "make_inventory_report":
                        MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectById(id);
                        if (!ObjectUtil.isNotEmpty(makeInventoryReport)) {
                            return Result.failure("盘点报告不存在或已被删除,无法完成");
                        }
                        MakeInventoryReport tempMakeInventoryReport = new MakeInventoryReport();
                        tempMakeInventoryReport.setId(id);
                        tempMakeInventoryReport.setRejectReason(param.getMessage());
                        //单据状态由审批中改为驳回
                        tempMakeInventoryReport.setPlanStatus(5);
                        f = makeInventoryReportMapper.updateById(tempMakeInventoryReport);
                        if (f <= 0) {
                            return Result.failure("数据未更新，操作失败");
                        }
                        break;
                    default:
                        log.warn("暂未处理的流程：{}", processAssignment.getProcessDefinitionKey());
                        break;
                }
            }
        }
        if (update <= 0) {
            return Result.failure("数据更新失败，请稍后再试");
        }
        if (param.getType() == 1) {
            // 判断当前流程实例任务是否完成，如果已完成更新表单状态
            String processInstanceId = processAssignment.getProcessInstanceId();
            Result countTask = TaskQueryUtil.countTask(processInstanceId);
            log.info("countTask:{}", countTask);
            if (countTask.isOk()) {
                Long count = Convert.toLong(countTask.getData());
                if (count < 1) {
                    // 所有任务都完成了
                    Integer id = processAssignment.getObjectId();
                    int f;
                    // 更新数据
                    switch (processAssignment.getProcessDefinitionKey()) {
                        //出库
                        case "plan_use_out":
                            /**
                             * 获取出库单信息
                             */
                            PlanUseOut planUseOut = planUseOutMapper.selectById(id);

                            if (!ObjectUtil.isNotNull(planUseOut)) {
                                return Result.failure("出库单不存在或已被删除,无法完成");
                            }
                            //获取此单据下的明细单，校验批准数量是否等于应出数量，若不同回滚库存并更新详细信息
                            Result result1 = planUseOutService.updateOutboundRecordAndInventory(planUseOut);
                            log.info("result1:{}", result1);
                            if (!result1.isOk()) {
                                return result1;
                            }
                            PlanUseOut tempPlanUseOut = new PlanUseOut();
                            tempPlanUseOut.setId(id);
                            tempPlanUseOut.setProcessInstanceId(processInstanceId);
                            //提交审批时 将更新数量  单据状态由审批中改为审批生效
                            tempPlanUseOut.setStatus(3);
                            f = planUseOutMapper.updateById(tempPlanUseOut);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            //TODO 计算价格出库
                            QueryWrapper<PlanUseOutDetails> queryWrapper = new QueryWrapper<PlanUseOutDetails>();
                            queryWrapper.eq("use_planning_document_number",planUseOut.getDocumentNumber());
                            List<PlanUseOutDetails> planUseOutDetails = planUseOutDetailsMapper.selectList(queryWrapper);
                            break;
                        //    入库
                        case "enter_warehouse":
                            EnterWarehouse enterWarehouse = enterWarehouseMapper.selectById(id);

                            if (!ObjectUtil.isNotNull(enterWarehouse)) {
                                return Result.failure("采购入库单不存在或已被删除,无法完成");
                            }
                            EnterWarehouse tempEnterWarehouse = new EnterWarehouse();
                            tempEnterWarehouse.setId(id);
                            tempEnterWarehouse.setProcessInstanceId(processInstanceId);
                            //单据状态由审批中改为审批生效
                            tempEnterWarehouse.setState(3);
                            f = enterWarehouseMapper.updateById(tempEnterWarehouse);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }

                            List<EnterWarehouseDetails> enterWarehouseDetailsList = enterWarehouseDetailsService.getListEnterWarehouseDetailsByDocNumberAndWarehosue(enterWarehouse.getDocumentNumber(), enterWarehouse.getWarehouse());
                            if (ObjectUtil.isNotNull(enterWarehouseDetailsList)) {
                                UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                                for (EnterWarehouseDetails enterWarehouseDetails : enterWarehouseDetailsList
                                ) {
                                    String materialCoding = enterWarehouseDetails.getMaterialCoding();
                                    String batch = enterWarehouseDetails.getBatch();
                                    String warehouseId = enterWarehouseDetails.getWarehouse();
                                    List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(materialCoding, batch, warehouseId);
                                    for (InventoryInformation inventoryInformation : inventoryInformationList
                                    ) {
                                        //更新库存信息为已入库 入库时间 入库单编号
                                        BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);
                                        //已入库
                                        updateInventoryInformationDTO.setIsEnter(1);
                                        //入库时间
                                        updateInventoryInformationDTO.setInDate(DateUtil.date());
                                        //入库单单据编号
                                        updateInventoryInformationDTO.setDocumentNumber(enterWarehouse.getDocumentNumber());

                                        inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                                    }
                                }
                            }
                            break;
                        //    调拨
                        case "allocation_plan":
                            AllocationPlan allocationPlan = allocationPlanMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(allocationPlan)) {
                                return Result.failure("调拨计划单不存在或已被删除,无法完成");
                            }
                            Result result = allocationPlanService.updateOutboundRecordAndInventory(allocationPlan);
                            if (!result.isOk()) {
                                return result;
                            }
                            AllocationPlan tempAllocationPlan = new AllocationPlan();
                            tempAllocationPlan.setId(id);
                            tempAllocationPlan.setProcessInstanceId(processInstanceId);
                            //单据状态由审批中改为审批生效
                            tempAllocationPlan.setPlanStatus(3);
                            f = allocationPlanMapper.updateById(tempAllocationPlan);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            //调拨计划审批生效后生成调拨入库调拨出库
                            // 调拨出库
                            allocationOutService.allocationPlanToAllocationOut(allocationPlan);
                            // 调拨入库
                            allocationEnterService.allocationPlanToAllocationEnter(allocationPlan);
                            // 判断是否属于平衡利库生成的
                            if (StrUtil.isNotBlank(allocationPlan.getBalanceLibraryNo())) {
                                int countBalanceLibrary = balanceLibraryMapper.selectCount(Wrappers.<BalanceLibrary>lambdaQuery().eq(BalanceLibrary::getBalanceLibraryNo, allocationPlan.getBalanceLibraryNo()));
                                if (countBalanceLibrary > 0) {
                                    // 查询调拨记录明细
                                    //更新平衡利库记录
                                    List<AllocationPlanDetail> allocationPlanDetails = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
                                    if (CollectionUtil.isNotEmpty(allocationPlanDetails)) {

                                        for (AllocationPlanDetail allocationPlanDetail : allocationPlanDetails) {
                                            // 根据物料编码、仓库、货主、调拨编号查询记录
                                            BalanceLibraryRecord balanceLibraryRecord = balanceLibraryRecordMapper.selectOne(Wrappers.<BalanceLibraryRecord>lambdaQuery()
                                                    .eq(BalanceLibraryRecord::getOutWarehouseId, allocationPlan.getSendWarehouse())
                                                    .eq(BalanceLibraryRecord::getConsignor, allocationPlanDetail.getConsignor())
                                                    .eq(BalanceLibraryRecord::getMaterialCoding, allocationPlanDetail.getMaterialCoding())
                                                    .and(wrapper -> wrapper.eq(BalanceLibraryRecord::getPlanNo, allocationPlan.getAllocationNumber())
                                                            .or().eq(BalanceLibraryRecord::getPlanNo2, allocationPlan.getAllocationNumber())
                                                            .or().eq(BalanceLibraryRecord::getPlanNo3, allocationPlan.getAllocationNumber())).last("limit 1"));
                                            if(null != balanceLibraryRecord){
                                                BalanceLibraryRecord temp = new BalanceLibraryRecord();
                                                temp.setId(balanceLibraryRecord.getId());
                                                if (allocationPlan.getAllocationNumber().equals(balanceLibraryRecord.getPlanNo())) {
                                                    temp.setCalibrationQuantity(allocationPlanDetail.getCalibrationQuantity());
                                                    temp.setCalibrationStatus(3);
                                                } else if (allocationPlan.getAllocationNumber().equals(balanceLibraryRecord.getPlanNo2())) {
                                                    temp.setCalibrationQuantity2(allocationPlanDetail.getCalibrationQuantity());
                                                    temp.setCalibrationStatus2(3);
                                                } else if (allocationPlan.getAllocationNumber().equals(balanceLibraryRecord.getPlanNo3())) {
                                                    temp.setCalibrationQuantity3(allocationPlanDetail.getCalibrationQuantity());
                                                    temp.setCalibrationStatus3(3);
                                                }
                                                balanceLibraryRecordMapper.updateById(temp);
                                            }
                                        }

                                    }
                                }
                            }
                            break;
                        //    采购计划
                        case "procurement_plan":
                            ProcurementPlan procurementPlan = procurementPlanMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(procurementPlan)) {
                                return Result.failure("采购计划单不存在或已被删除,无法完成");
                            }
                            ProcurementPlan tempProcurementPlan = new ProcurementPlan();
                            tempProcurementPlan.setId(id);
                            tempProcurementPlan.setProcessInstanceId(processInstanceId);
                            //单据状态由审批中改为审批生效
                            tempProcurementPlan.setStatus(3);
                            f = procurementPlanMapper.updateById(tempProcurementPlan);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            // 判断是否属于平衡利库生成的
                            if (null != procurementPlan.getBalanceLibraryDetailId() && procurementPlan.getBalanceLibraryDetailId() > 0) {
                                BalanceLibraryDetail balanceLibraryDetail = balanceLibraryDetailMapper.selectById(procurementPlan.getBalanceLibraryDetailId());
                                if (null != balanceLibraryDetail) {
                                    List<ProcurementPlanDetails> procurementPlanDetails = procurementPlanDetailsMapper.selectList(Wrappers.<ProcurementPlanDetails>lambdaQuery()
                                            .eq(ProcurementPlanDetails::getPlanNumber, procurementPlan.getPlanNumber()));
                                    if (CollectionUtil.isNotEmpty(procurementPlanDetails)) {
                                        ProcurementPlanDetails planDetails = procurementPlanDetails.get(0);
                                        BalanceLibraryDetail temp = new BalanceLibraryDetail();
                                        temp.setId(balanceLibraryDetail.getId());
                                        temp.setApprovedPurchasedQuantity(planDetails.getApprovedQuantity());
                                        balanceLibraryDetailMapper.updateById(temp);
                                    }

                                }
                            }
                            break;
                        //    需求计划
                        case "requirements_planning":
                            RequirementsPlanning requirementsPlanning = requirementsPlanningMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(requirementsPlanning)) {
                                return Result.failure("需求计划单不存在或已被删除,无法完成");
                            }
                            RequirementsPlanning tempRequirementsPlanning = new RequirementsPlanning();
                            tempRequirementsPlanning.setId(id);
                            tempRequirementsPlanning.setProcessInstanceId(processInstanceId);
                            //单据状态由审批中改为审批生效
                            tempRequirementsPlanning.setPlanStatus(3);
                            f = requirementsPlanningMapper.updateById(tempRequirementsPlanning);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            break;
                        //    到货检验
                        case "arrival_verification":
                            ArrivalVerification arrivalVerification = arrivalVerificationMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(arrivalVerification)) {
                                return Result.failure("到货检验单不存在或已被删除,无法完成");
                            }
                            ArrivalVerification tempArrivalVerification = new ArrivalVerification();
                            tempArrivalVerification.setId(id);
                            tempArrivalVerification.setProcessInstanceId(processInstanceId);
                            //单据状态由草拟转为审批中
                            tempArrivalVerification.setPlanStatus(3);
                            f = arrivalVerificationMapper.updateById(tempArrivalVerification);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            // 到货检验审批生效后生成采购入库草拟表单
                            //TODO 经办人暂时用最后一个节点的审批人
                            enterWarehouseService.arrivalVerificationToEnterWarehouse(user.getId(), arrivalVerification);
                            break;
                        //    临库入库
                        case "temporary_enter_warehouse":
                            TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(temporaryEnterWarehouse)) {
                                return Result.failure("临库入库单不存在或已被删除,无法完成");
                            }
                            TemporaryEnterWarehouse tempTemporaryEnterWarehouse = new TemporaryEnterWarehouse();
                            tempTemporaryEnterWarehouse.setId(id);
                            tempTemporaryEnterWarehouse.setProcessInstanceId(processInstanceId);
                            //单据状态由草拟转为审批中
                            tempTemporaryEnterWarehouse.setState(3);
                            f = temporaryEnterWarehouseMapper.updateById(tempTemporaryEnterWarehouse);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            break;
                        //    临库出库
                        case "temporary_out_warehouse":
                            TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(temporaryOutWarehouse)) {
                                return Result.failure("临库出库单不存在或已被删除,无法完成");
                            }

                            TemporaryOutWarehouse tempTemporaryOutWarehouse = new TemporaryOutWarehouse();
                            tempTemporaryOutWarehouse.setId(id);
                            tempTemporaryOutWarehouse.setProcessInstanceId(processInstanceId);
                            //单据状态由草拟转为审批中
                            tempTemporaryOutWarehouse.setStatus(3);
                            f = temporaryOutWarehouseMapper.updateById(tempTemporaryOutWarehouse);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            break;
                        //    盘点
                        case "make_inventory":
                            MakeInventory makeInventory = makeInventoryMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(makeInventory)) {
                                return Result.failure("盘点单不存在或已被删除,无法完成");
                            }

                            MakeInventory tempMakeInventory = new MakeInventory();
                            tempMakeInventory.setId(id);
                            tempMakeInventory.setProcessInstanceId(processInstanceId);
                            //单据状态由草拟转为审批中
                            tempMakeInventory.setPlanStatus(3);
                            f = makeInventoryMapper.updateById(tempMakeInventory);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            break;
                        //    盘点报告
                        case "make_inventory_report":
                            MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectById(id);

                            if (!ObjectUtil.isNotEmpty(makeInventoryReport)) {
                                return Result.failure("盘点报告不存在或已被删除,无法完成");
                            }

                            MakeInventoryReport tempMakeInventoryReport = new MakeInventoryReport();
                            tempMakeInventoryReport.setId(id);
                            tempMakeInventoryReport.setProcessInstanceId(processInstanceId);
                            //单据状态由草拟转为审批中
                            tempMakeInventoryReport.setPlanStatus(3);
                            f = makeInventoryReportMapper.updateById(tempMakeInventoryReport);
                            if (f <= 0) {
                                return Result.failure("数据未更新，流程完成失败");
                            }
                            break;
                        default:
                            log.warn("暂未处理的流程：{}", processAssignment.getProcessDefinitionKey());
                            break;
                    }
                }
            }
        }
        if (param.getAccounts() != null) {
            if (param.getAccounts().size() > 0) {
                // TODO 推送抄送消息
                List<String> accounts = param.getAccounts();
                List<Message> messages = new ArrayList<>();
                for (String account : accounts) {
                    User receiver = userMapper.getUserByAccount(account);
                    if (receiver == null) {
                        continue;
                    }
                    Message message = new Message();
                    message.setStatus(0);
                    message.setUserName(receiver.getUserName());
                    message.setUserId(receiver.getId());
                    message.setDocumentNumber(processAssignment.getDocumentNumber());
                    message.setObjectId(processAssignment.getObjectId());
                    message.setObjectType(processAssignment.getObjectType());
                    message.setType(0);
                    message.setHandleUserId(user.getId());
                    message.setHandleUserName(user.getUserName());
                    message.setPlanClassification(processAssignment.getPlanClassification());
                    message.setProcessInstanceId(processAssignment.getProcessInstanceId());
                    messages.add(message);
                }
                if (messages.size() > 0) {
                    boolean batchAdd = messageService.saveBatch(messages);
                    if (!batchAdd) {
                        return Result.failure("消息抄送失败");
                    }
                    //TODO 推送消息

                }

            }
        }

        return Result.success();

    }

    @Override
    public Result start(StartProcessParam param) {
        Integer id = param.getId();
        PlanUseOut planUseOut = null;
        AllocationPlan allocationPlan = null;
        // 出库先验证库存是否够 不够直接返回
        switch (param.getProcessDefinitionKey()) {
            //出库
            case "plan_use_out":
                /**
                 * 获取出库单信息
                 */
                planUseOut = planUseOutMapper.selectById(id);
                if (!ObjectUtil.isNotNull(planUseOut)) {
                    return Result.failure("出库单不存在或已被删除,无法进入流程引擎");
                }
                Result checkStock = planUseOutService.checkStock(planUseOut);
                if (!checkStock.isOk()) {
                    return checkStock;
                }
                break;
            case "allocation_plan":
                allocationPlan = allocationPlanMapper.selectById(id);

                if (!ObjectUtil.isNotEmpty(allocationPlan)) {
                    return Result.failure("调拨计划单不存在或已被删除,无法进入流程引擎");
                }
                Result checkStock2 = allocationPlanService.checkStock(allocationPlan);
                if (!checkStock2.isOk()) {
                    return checkStock2;
                }
                break;

            default:
                break;
        }
        // 前置条件判断完成 启动流程
        Result result = TaskQueryUtil.start(param);
        if (result.isOk()) {
            // 流程启动成功  处理业务数据
            String processInstanceId = Convert.toStr(result.getData());
            int f = 0;
            // 更新数据
            switch (param.getProcessDefinitionKey()) {
                //出库
                case "plan_use_out":
                    PlanUseOut tempPlanUseOut = new PlanUseOut();
                    tempPlanUseOut.setId(id);
                    tempPlanUseOut.setProcessInstanceId(processInstanceId);
                    //提交审批时 将更新数量  单据状态由草拟转为审批中
                    tempPlanUseOut.setStatus(2);
                    f = planUseOutMapper.updateById(tempPlanUseOut);
                    if (f <= 0) {
                        return Result.failure("未进入流程");
                    }
                    //新增出库记录并减库存
                    Result resultAnother = planUseOutService.addOutboundRecordUpdateInventory(planUseOut);
                    if (!resultAnother.isOk()) {
                        return resultAnother;
                    }
                    break;
                //    盘点报告
                case "make_inventory_report":
                    MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectById(id);

                    if (!ObjectUtil.isNotEmpty(makeInventoryReport)) {
                        return Result.failure("盘点报告不存在或已被删除,无法完成");
                    }

                    MakeInventoryReport tempMakeInventoryReport = new MakeInventoryReport();
                    tempMakeInventoryReport.setId(id);
                    tempMakeInventoryReport.setProcessInstanceId(processInstanceId);
                    //单据状态由草拟转为审批中
                    tempMakeInventoryReport.setPlanStatus(3);
                    f = makeInventoryReportMapper.updateById(tempMakeInventoryReport);
                    if (f <= 0) {
                        return Result.failure("数据未更新，流程完成失败");
                    }
                    //盘点报告完成审批后，更新库存
                    List<MakeInventoryReportDetails> makeInventoryReportDetailsList = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByDocNumberAndWarehosueId(makeInventoryReport.getReportNumber(), makeInventoryReport.getWarehouseId());
                    for (MakeInventoryReportDetails makeInventoryReportDetails : makeInventoryReportDetailsList
                    ) {
                        InventoryInformation inventoryInformation = inventoryInformationService.getInventoryInformation(makeInventoryReportDetails.getMaterialCoding(), makeInventoryReportDetails.getBatch(), makeInventoryReportDetails.getCargoSpaceId());
                        UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                        BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);
                        updateInventoryInformationDTO.setInventoryCredit(makeInventoryReportDetails.getCheckCredit());
                        Result resultUpdateInventory = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                        log.info("盘点报告审批完成,更新库存{}", resultUpdateInventory);
                    }
                    break;
                //    入库 TODO
                case "enter_warehouse":
                    EnterWarehouse enterWarehouse = enterWarehouseMapper.selectById(id);

                    if (!ObjectUtil.isNotNull(enterWarehouse)) {
                        return Result.failure("采购入库单不存在或已被删除,无法进入流程引擎");
                    }
                    EnterWarehouse tempEnterWarehouse = new EnterWarehouse();
                    tempEnterWarehouse.setId(id);
                    tempEnterWarehouse.setProcessInstanceId(processInstanceId);
                    //单据状态由草拟转为审批中
                    tempEnterWarehouse.setState(2);
                    f = enterWarehouseMapper.updateById(tempEnterWarehouse);
                    if (f <= 0) {
                        return Result.failure("未进入流程");
                    }
                    break;
                //    调拨
                case "allocation_plan":

                    AllocationPlan tempAllocationPlan = new AllocationPlan();
                    tempAllocationPlan.setId(id);
                    tempAllocationPlan.setProcessInstanceId(processInstanceId);
                    //单据状态由草拟转为审批中
                    tempAllocationPlan.setPlanStatus(2);
                    f = allocationPlanMapper.updateById(tempAllocationPlan);
                    if (f <= 0) {
                        return Result.failure("未进入流程");
                    }
                    //新增出库记录并减库存
                    Result resultAnother2 = allocationPlanService.addOutboundRecordUpdateInventory(allocationPlan);
                    if (!resultAnother2.isOk()) {
                        return resultAnother2;
                    }
                    break;
                //    采购计划
                case "procurement_plan":
                    ProcurementPlan procurementPlan = procurementPlanMapper.selectById(id);

                    if (!ObjectUtil.isNotEmpty(procurementPlan)) {
                        return Result.failure("采购计划单不存在或已被删除,无法进入流程引擎");
                    }
                    ProcurementPlan tempProcurementPlan = new ProcurementPlan();
                    tempProcurementPlan.setId(id);
                    tempProcurementPlan.setProcessInstanceId(processInstanceId);
                    //单据状态由草拟转为审批中
                    tempProcurementPlan.setStatus(2);
                    f = procurementPlanMapper.updateById(tempProcurementPlan);
                    if (f <= 0) {
                        return Result.failure("未进入流程");
                    }
                    break;
                //    需求计划
                case "requirements_planning":
                    RequirementsPlanning requirementsPlanning = requirementsPlanningMapper.selectById(id);

                    if (!ObjectUtil.isNotEmpty(requirementsPlanning)) {
                        return Result.failure("需求计划单不存在或已被删除,无法进入流程引擎");
                    }
                    RequirementsPlanning tempRequirementsPlanning = new RequirementsPlanning();
                    tempRequirementsPlanning.setId(id);
                    tempRequirementsPlanning.setProcessInstanceId(processInstanceId);
                    //单据状态由草拟转为审批中
                    tempRequirementsPlanning.setPlanStatus(2);
                    f = requirementsPlanningMapper.updateById(tempRequirementsPlanning);
                    if (f <= 0) {
                        return Result.failure("未进入流程");
                    }
                    break;
                //    到货检验
                case "arrival_verification":
                    ArrivalVerification arrivalVerification = arrivalVerificationMapper.selectById(id);

                    if (!ObjectUtil.isNotEmpty(arrivalVerification)) {
                        return Result.failure("到货检验单不存在或已被删除,无法进入流程引擎");
                    }
                    ArrivalVerification tempArrivalVerification = new ArrivalVerification();
                    tempArrivalVerification.setId(id);
                    tempArrivalVerification.setProcessInstanceId(processInstanceId);
                    //单据状态由草拟转为审批中
                    tempArrivalVerification.setPlanStatus(2);
                    f = arrivalVerificationMapper.updateById(tempArrivalVerification);
                    if (f <= 0) {
                        return Result.failure("未进入流程");
                    }
                    break;
                default:
                    log.warn("暂未处理的流程：{}", param.getProcessDefinitionKey());
                    break;
            }
        }
        return result;
    }

    public Map<String, List<HistoryTaskVo>> groupByProcessInstanceId(List<HistoryTaskVo> historyTaskVos) {
        return historyTaskVos.stream()
                .collect(Collectors.groupingBy(HistoryTaskVo::getProcessInstanceId));
    }

    public Map<String, List<HistoryTaskVo>> groupByProcessKey(List<HistoryTaskVo> historyTaskVos) {
        return historyTaskVos.stream()
                .collect(Collectors.groupingBy(HistoryTaskVo::getProcessDefinitionKey));
    }

    private void processingData(Map<String, List<String>> listMap, String processInstanceId, String taskId) {
        List<String> strings;
        if (listMap.containsKey(processInstanceId)) {
            strings = listMap.get(processInstanceId);
        } else {
            strings = new ArrayList<>();
        }
        strings.add(taskId);
        listMap.put(processInstanceId, strings);
    }

}
