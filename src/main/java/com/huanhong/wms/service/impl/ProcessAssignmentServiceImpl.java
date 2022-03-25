package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.common.units.task.HistoryTaskVo;
import com.huanhong.common.units.task.RejectParam;
import com.huanhong.common.units.task.TaskCompleteParam;
import com.huanhong.common.units.task.TaskQueryUtil;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.UpPaStatus;
import com.huanhong.wms.entity.param.ApproveParam;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.IMessageService;
import com.huanhong.wms.service.IProcessAssignmentService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public Result<Integer> syncProcessAssignment() {
        Result taskResult = TaskQueryUtil.list();
        if (taskResult.isOk()) {
            if (taskResult.getData() != null) {
                List<HistoryTaskVo> array = JSONObject.parseArray(taskResult.getData().toString(), HistoryTaskVo.class);
                if (array.size() == 0) {
                    log.error("当前没有任务");
                } else {
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
                }

            }
        } else {
            log.error("请求当前任务错误");
            return null;
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
        ProcessAssignment processAssignment = processAssignmentMapper.selectList(new QueryWrapper<ProcessAssignment>().eq("task_id", taskId).eq("status", 0)).get(0);
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
            Result complete = TaskQueryUtil.complete(completeParam);
            if (complete.isOk()) {
                update = processAssignmentMapper.updateById(pa);
            } else {
                return Result.failure("审批失败，请稍后重试");
            }
        }
        //驳回
        if (param.getType() == 2) {
            RejectParam rejectParam = new RejectParam();
            rejectParam.setTaskId(taskId);
            rejectParam.setIsFirst(param.getIsFirst());
            rejectParam.setUsername(param.getUsername());
            rejectParam.setMessage(param.getMessage());
            rejectParam.setVariables(param.getVariables());
            Result rejectResult = TaskQueryUtil.reject(rejectParam);
            if (rejectResult.isOk()) {
                update = processAssignmentMapper.updateById(pa);
            } else {
                return Result.failure("驳回失败，请稍后重试");
            }

        }
        if (update > 0) {
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
                        if (batchAdd) {
                            //TODO 推送消息
                            return Result.success();
                        } else {
                            return Result.failure("消息抄送失败");
                        }
                    }

                }
            }
            return Result.success();
        }
        return Result.failure("请稍后重试");

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
