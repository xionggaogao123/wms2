package com.huanhong.wms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.common.units.task.HistoryTaskVo;
import com.huanhong.common.units.task.TaskQueryUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.UpPaStatus;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;
import com.huanhong.wms.mapper.*;
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
        if (param.getObjectType() != null && param.getObjectType() >0 && param.getObjectType() < 5) {
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
