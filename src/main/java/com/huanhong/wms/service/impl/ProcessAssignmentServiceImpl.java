package com.huanhong.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.units.task.HistoryTaskVo;
import com.huanhong.common.units.task.TaskQueryUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.IProcessAssignmentService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Result<Integer> syncProcessAssignment() {
       Result taskResult = TaskQueryUtil.list();
       if (taskResult.isOk()){
           if (taskResult.getData() != null){
               JSONObject jSONObject;
               JSONArray array = JSONArray.parseArray(taskResult.getData().toString());
               if (array.size()==0){
                   log.error("当前没有任务");
               }else {
                   List<ProcessAssignment> list = new ArrayList<>();
                   for (int i = 0; i < array.size(); i++) {
                       HistoryTaskVo historyTaskVo = array.getObject(i,HistoryTaskVo.class);
                       //查询是否已同步该任务
                       int count = processAssignmentMapper.selectCount(new QueryWrapper<ProcessAssignment>().eq("task_id",historyTaskVo.getId()));
                       if (count>0){
                           continue;
                       }
                       String type = historyTaskVo.getProcessDefinitionKey();
                       String processInstanceId = historyTaskVo.getProcessInstanceId();
                       ProcessAssignment p = new ProcessAssignment();
                       p.setName(historyTaskVo.getName());
                       p.setProcessDefinitionKey(type);
                       switch (type){
                           //出库
                           case "plan_use_out":
                               List<PlanUseOut> planUseOuts = planUseOutMapper.selectList(new QueryWrapper<PlanUseOut>().eq("process_instance_id",processInstanceId));
                               if (planUseOuts.size()>0){
                                   PlanUseOut planUseOut = planUseOuts.get(0);
                                   p.setDocumentNumber(planUseOut.getDocumentNumber());
                               }else {
                                   continue;
                               }
                               break;
                           //    入库
                           case "enter_warehouse":
                               List<EnterWarehouse> enterWarehouses = enterWarehouseMapper.selectList(new QueryWrapper<EnterWarehouse>().eq("process_instance_id",processInstanceId));
                               if (enterWarehouses.size()>0){
                                   EnterWarehouse enterWarehouse = enterWarehouses.get(0);
                                   p.setDocumentNumber(enterWarehouse.getDocumentNumber());
                               }else {
                                   continue;
                               }
                               break;
                           //    调拨
                           case "allocation_plan":
                               List<AllocationPlan> allocationPlans = allocationPlanMapper.selectList(new QueryWrapper<AllocationPlan>().eq("process_instance_id",processInstanceId));
                               if (allocationPlans.size()>0){
                                   AllocationPlan allocationPlan = allocationPlans.get(0);
                                   p.setDocumentNumber(allocationPlan.getAllocationNumber());
                               }else {
                                   continue;
                               }
                               break;
                           //    采购计划
                           case "procurement_plan":
                               List<ProcurementPlan> procurementPlans = procurementPlanMapper.selectList(new QueryWrapper<ProcurementPlan>().eq("process_instance_id",processInstanceId));
                               if (procurementPlans.size()>0){
                                   ProcurementPlan procurementPlan = procurementPlans.get(0);
                                   p.setDocumentNumber(procurementPlan.getPlanNumber());
                               }else {
                                   continue;
                               }
                               break;
                           //    需求计划
                           case "requirements_planning":
                               List<RequirementsPlanning> requirementsPlannings = requirementsPlanningMapper.selectList(new QueryWrapper<RequirementsPlanning>().eq("process_instance_id",processInstanceId));
                               if (requirementsPlannings.size()>0){
                                   RequirementsPlanning requirementsPlanning = requirementsPlannings.get(0);
                                   p.setDocumentNumber(requirementsPlanning.getPlanNumber());
                               }else {
                                   continue;
                               }
                               break;
                           //    到货检验
                           case "arrival_verification":
                               List<ArrivalVerification> arrivalVerifications = arrivalVerificationMapper.selectList(new QueryWrapper<ArrivalVerification>().eq("process_instance_id",processInstanceId));
                               if (arrivalVerifications.size()>0){
                                   ArrivalVerification arrivalVerification = arrivalVerifications.get(0);
                                   p.setDocumentNumber(arrivalVerification.getVerificationDocumentNumber());
                               }else {
                                   continue;
                               }
                               break;
                       }
                       list.add(p);
                   }
                   if (list.size()>0){
                       this.saveBatch(list);
                   }
               }
           }
       }else {
           log.error("请求当前任务错误");
           return null;
       }
       return null;
    }
}
