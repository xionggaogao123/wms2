package com.huanhong.wms.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.ProcurementPlanVO;
import com.huanhong.wms.mapper.ProcurementPlanMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/procurement-plan")
@ApiSort()
@Api(tags = "采购计划主表")
public class ProcurementPlanController extends BaseController {

    @Resource
    private IProcurementPlanService procurementPlanService;

    @Resource
    private ProcurementPlanMapper procurementPlanMapper;

    @Resource
    private IProcurementPlanDetailsService procurementPlanDetailsService;

    @Resource
    private IUserService userService;

    @Resource
    private IRequirementsPlanningService requirementsPlanningService;

    @Resource
    private IRequiremetsPlanningDetailsService requiremetsPlanningDetailsService;

    @Resource
    private IInventoryInformationService inventoryInformationService;


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询采购计划主表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<ProcurementPlan>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              ProcurementPlanVO procurementPlanVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ProcurementPlan> pageResult = procurementPlanService.pageFuzzyQuery(new Page<>(current, size), procurementPlanVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到采购计划信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加采购计划主表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddProcurementPlanAndDetailsDTO addProcurementPlanAndDetailsDTO) {
        return procurementPlanService.add(addProcurementPlanAndDetailsDTO);

    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新采购计划主表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateProcurementPlanAndDetailsDTO updateProcurementPlanAndDetailsDTO) {
        try {
            UpdateProcurementPlanDTO updateProcurementPlanDTO = updateProcurementPlanAndDetailsDTO.getUpdateProcurementPlanDTO();
            List<UpdateProcurementPlanDetailsDTO> updateProcurementPlanDetailsDTOList = updateProcurementPlanAndDetailsDTO.getUpdateProcurementPlanDetailsDTOList();
            Result result = procurementPlanService.updateProcurementPlan(updateProcurementPlanDTO);
            if (!result.isOk()) {
                return Result.failure("更新采购计划表失败！");
            }
            return procurementPlanDetailsService.updateProcurementPlanDetails(updateProcurementPlanDetailsDTOList);
        } catch (Exception e) {
            log.error("更新采购计划失败");
            return Result.failure("系统异常：更新采购计划失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除采购计划主表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {

        ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanById(id);
        if (ObjectUtil.isNull(procurementPlan)){
            return Result.failure("单据不存在！");
        }
        boolean delete = procurementPlanService.removeById(id);
        //主表删除成功,删除明细
        if (delete){
            String docNum = procurementPlan.getPlanNumber();
            List<ProcurementPlanDetails> procurementPlanDetailsList= procurementPlanDetailsService.getProcurementPlanDetailsByDocNumAndWarehouseId(docNum,procurementPlan.getWarehouseId());
            for (ProcurementPlanDetails procurementPlanDetails:procurementPlanDetailsList) {
                procurementPlanDetailsService.removeById(procurementPlanDetails.getId());
            }
            // 恢复采购计划可导入
            String originalDocumentNumber = procurementPlan.getOriginalDocumentNumber();
            String[] originalDocumentNumbers = JSON.parseArray(originalDocumentNumber).toArray(new String[]{});
            requirementsPlanningService.updateIsImportedByPlanNumbers(0,"",originalDocumentNumbers);
        }
        return Result.success("删除成功");
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取采购计划表及其明细")
    @GetMapping("getProcurementPlanAndDetailsById/{id}")
    public Result getProcurementPlanAndDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanById(id);
        if (ObjectUtil.isEmpty(procurementPlan)) {
            return Result.failure("未找到对应信息！");
        }
        List<ProcurementPlanDetails> procurementPlanDetailsList = procurementPlanDetailsService.getProcurementPlanDetailsByDocNumAndWarehouseId(procurementPlan.getPlanNumber(), procurementPlan.getWarehouseId());
        jsonObject.put("doc", procurementPlan);
        jsonObject.put("details", procurementPlanDetailsList);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号和仓库Id获取需求计划表及其明细")
    @GetMapping("/getRequirementsPlanningByDocNumAndWarehouseId")
    public Result getRequirementsPlanningByDocNumAndWarehouseId(
            @RequestParam String docNum, @RequestParam String warehouseId
    ) {
        JSONObject jsonObject = new JSONObject();
        ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanByDocNumAndWarehouseId(docNum, warehouseId);
        if (ObjectUtil.isEmpty(procurementPlan)) {
            return Result.failure("未找到对应信息！");
        }
        List<ProcurementPlanDetails> procurementPlanDetailsList = procurementPlanDetailsService.getProcurementPlanDetailsByDocNumAndWarehouseId(docNum, warehouseId);
        jsonObject.put("doc", procurementPlan);
        jsonObject.put("details", procurementPlanDetailsList);
        return Result.success(jsonObject);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "流程引擎-需求计划-查询")
    @GetMapping("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id) {

        EntityUtils entityUtils = new EntityUtils();
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanById(id);
            if (ObjectUtil.isNotEmpty(procurementPlan)) {
                List<ProcurementPlanDetails> procurementPlanDetailsList = procurementPlanDetailsService.getProcurementPlanDetailsByDocNumAndWarehouseId(procurementPlan.getPlanNumber(), procurementPlan.getWarehouseId());
                /**
                 * 当查询到主表事进行数据封装
                 * 1.表头--主表表明--用于判断应该进入那个流程-tableName
                 * 2.主表字段名对照-main
                 * 3.明细表字段名对照-details
                 * 4.主表数据-mainValue
                 * 5.明细表数据-detailsValue
                 * 6.主表更新接口-mainUpdate
                 * 7.明细表更新接口-detailsUpdate
                 */
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("tableName", "procurement_plan");
                jsonResult.put("main", entityUtils.jsonField("procurementPlan", new ProcurementPlan()));
                jsonResult.put("details", entityUtils.jsonField("procurementPlan", new ProcurementPlanDetails()));
                jsonResult.put("mainValue", procurementPlan);
                jsonResult.put("detailsValue", procurementPlanDetailsList);
                jsonResult.put("mainKey","updateProcurementPlanDTO");
                jsonResult.put("detailKey","updateProcurementPlanDetailsDTOList");
                jsonResult.put("mainUpdate", "/wms/api/v1/procurement-plan/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/procurement-plan-details/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/procurement-plan/missionCompleted");
                return Result.success(jsonResult);
            } else {
                return Result.failure("未查询到相关信息");
            }
        } catch (Exception e) {
            log.error("查询失败,异常：", e);
            return Result.failure("查询失败，系统异常！");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
            @ApiImplicitParam(name = "processInstanceId", value = "流程Id")
    })
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "流程引擎-需求计划-发起")
    @PutMapping("/missionStarts")
    public Result missionStarts(@RequestParam Integer id,
                                @RequestParam String processInstanceId) {

        try {
            ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotEmpty(procurementPlan)) {
                UpdateProcurementPlanDTO updateProcurementPlanDTO = new UpdateProcurementPlanDTO();
                updateProcurementPlanDTO.setId(id);
                updateProcurementPlanDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateProcurementPlanDTO.setStatus(2);
                Result result = procurementPlanService.updateProcurementPlan(updateProcurementPlanDTO);
                if (result.isOk()) {
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("采购计划单异常,无法进入流程引擎");
            }
        } catch (Exception e) {
            log.error("流程启动接口异常", e);
            return Result.failure("系统异常");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "流程Id")
    })
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "流程引擎-需求计划-完成审批")
    @PutMapping("/missionCompleted")
    public Result missionCompleted(@RequestParam String processInstanceId) {
        try {
            //通过流程Id查询出单据Id
            ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanByProcessInstanceId(processInstanceId);
            if (ObjectUtil.isNotEmpty(procurementPlan)) {
                UpdateProcurementPlanDTO updateProcurementPlanDTO = new UpdateProcurementPlanDTO();
                updateProcurementPlanDTO.setId(procurementPlan.getId());
                /**
                 *  单据状态由审批中改为审批生效
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateProcurementPlanDTO.setStatus(3);
                return procurementPlanService.updateProcurementPlan(updateProcurementPlanDTO);
            } else {
                return Result.failure("单据异常无法完成");
            }
        } catch (Exception e) {
            log.error("完成审批接口异常", e);
            return Result.failure("系统异常");
        }
    }


    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "多条需求计划合并采购计划")
    @PutMapping("/consolidatedProcurementPlan")
    public Result consolidatedProcurementPlan(@Valid @RequestBody List<RequirementsPlanning> requirementsPlanningList){
        try {
            LoginUser loginUser = this.getLoginUser();
            AddProcurementPlanDTO addProcurementPlanDTO = new AddProcurementPlanDTO();
            User user = userService.getById(loginUser.getId());
            List<String> listOriginalDocumentNumber =  new ArrayList<>();
            List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList = new ArrayList<>();
            //获取需求计划表
            int i = 0;
            for (RequirementsPlanning requirementsPlanning:requirementsPlanningList) {
                listOriginalDocumentNumber.add(requirementsPlanning.getPlanNumber());
                //获取需求计划明细表
                List<RequiremetsPlanningDetails> requiremetsPlanningDetailsList = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsByDocNumAndWarehouseId(requirementsPlanning.getPlanNumber(), requirementsPlanning.getWarehouseId());
                for (RequiremetsPlanningDetails requiremetsPlanningDetails:requiremetsPlanningDetailsList) {
                    int flag = 0;
                    String materialCoding = requiremetsPlanningDetails.getMaterialCoding();
                    if (i==0){
                        //此物料尚未进入采购计划明细，新增采购计划明细表
                        AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTOFirst = new AddProcurementPlanDetailsDTO();
                        //物料编码
                        addProcurementPlanDetailsDTOFirst.setMaterialCoding(requiremetsPlanningDetails.getMaterialCoding());
                        //采购需求数量=需求计划批准数量
                        addProcurementPlanDetailsDTOFirst.setRequiredQuantity(requiremetsPlanningDetails.getApprovedQuantity());
                        //计划采购数量=需求计划批准数量
                        addProcurementPlanDetailsDTOFirst.setPlannedPurchaseQuantity(requiremetsPlanningDetails.getApprovedQuantity());
                        //预估单价-使用单位单价历史价位
                        addProcurementPlanDetailsDTOFirst.setEstimatedUnitPrice(requiremetsPlanningDetails.getEstimatedUnitPrice());
                        //预估金额
                        addProcurementPlanDetailsDTOFirst.setEstimatedAmount(requiremetsPlanningDetails.getEstimatedAmount());
                        //仓库id
                        addProcurementPlanDetailsDTOFirst.setWarehouseId(requirementsPlanning.getWarehouseId());
                        //使用地点
                        addProcurementPlanDetailsDTOFirst.setUsePlace(requiremetsPlanningDetails.getUsePlace());
                        //使用用途
                        addProcurementPlanDetailsDTOFirst.setUsePurpose(requiremetsPlanningDetails.getUsePurpose());
                        //备注
                        addProcurementPlanDetailsDTOFirst.setRemark("系统自动生成");
                        // 到货时间
                        addProcurementPlanDetailsDTOFirst.setRequestArrivalTime(requiremetsPlanningDetails.getArrivalTime());
                        addProcurementPlanDetailsDTOFirst.setMaterialId(requiremetsPlanningDetails.getMaterialId());
                        addProcurementPlanDetailsDTOFirst.setMaterialName(requiremetsPlanningDetails.getMaterialName());
                        addProcurementPlanDetailsDTOList.add(addProcurementPlanDetailsDTOFirst);
                    }else {
                        for (int j =0; j<addProcurementPlanDetailsDTOList.size();j++) {
                            AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTO = addProcurementPlanDetailsDTOList.get(j);
                            //判断物料是否已经进入采购计划
                            if (addProcurementPlanDetailsDTO.getMaterialCoding().equals(materialCoding)){
                                //若此物料已有采购明细
                                //采购明细中此物料的需求数量等于现需求数量加上此物料的需求明细的批准数量
                                addProcurementPlanDetailsDTO.setRequiredQuantity(NumberUtil.add(addProcurementPlanDetailsDTO.getRequiredQuantity(),requiremetsPlanningDetails.getApprovedQuantity()));
                                //同上处理计划采购数量=新增后的需求数量
                                addProcurementPlanDetailsDTO.setPlannedPurchaseQuantity(addProcurementPlanDetailsDTO.getRequiredQuantity());
                                //预估金额  采购计划明细的预估金额+需求计划明细的预估金额
                                addProcurementPlanDetailsDTO.setEstimatedAmount(NumberUtil.add(addProcurementPlanDetailsDTO.getEstimatedAmount(),requiremetsPlanningDetails.getEstimatedAmount()));
                                //使用地点
                                addProcurementPlanDetailsDTO.setUsePlace(addProcurementPlanDetailsDTO.getUsePlace()+","+requiremetsPlanningDetails.getUsePlace());
                                //使用用途
                                addProcurementPlanDetailsDTO.setUsePurpose(addProcurementPlanDetailsDTO.getUsePurpose()+","+requiremetsPlanningDetails.getUsePurpose());
                                //备注
                                addProcurementPlanDetailsDTO.setRemark("系统自动生成");
                                if(addProcurementPlanDetailsDTO.getRequestArrivalTime().compareTo(requiremetsPlanningDetails.getArrivalTime())>0){
                                    addProcurementPlanDetailsDTO.setRequestArrivalTime(requiremetsPlanningDetails.getArrivalTime());
                                }
                                //执行更新操作后代表当前需求计划明细已经并入采购计划明细中,flag++;
                                flag++;
                                //合并后跳出对比物料编码的循环
                                break;
                            }
                        }
                        //判断明细已经通过合并操作并入采购计划明细 0-无相同物料在采购计划明细中所以未经合并 1-已合并
                        if (flag==0){
                            //此物料尚未被合并操作并入采购计划明细，新增采购计划明细表
                            AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTONew = new AddProcurementPlanDetailsDTO();
                            //物料编码
                            addProcurementPlanDetailsDTONew.setMaterialCoding(requiremetsPlanningDetails.getMaterialCoding());
                            //采购需求数量=需求计划批准数量
                            addProcurementPlanDetailsDTONew.setRequiredQuantity(requiremetsPlanningDetails.getApprovedQuantity());
                            //计划采购数量=需求计划批准数量
                            addProcurementPlanDetailsDTONew.setPlannedPurchaseQuantity(requiremetsPlanningDetails.getApprovedQuantity());
                            //预估单价-使用单位单价历史价位
                            addProcurementPlanDetailsDTONew.setEstimatedUnitPrice(requiremetsPlanningDetails.getEstimatedUnitPrice());
                            //预估金额
                            addProcurementPlanDetailsDTONew.setEstimatedAmount(requiremetsPlanningDetails.getEstimatedAmount());
                            //预期到货时间
                            addProcurementPlanDetailsDTONew.setRequestArrivalTime(requiremetsPlanningDetails.getArrivalTime());
                            addProcurementPlanDetailsDTONew.setWarehouseId(requirementsPlanning.getWarehouseId());
                            //使用地点
                            addProcurementPlanDetailsDTONew.setUsePlace(requiremetsPlanningDetails.getUsePlace());
                            //使用用途
                            addProcurementPlanDetailsDTONew.setUsePurpose(requiremetsPlanningDetails.getUsePurpose());
                            //备注
                            addProcurementPlanDetailsDTONew.setRemark("系统自动生成");
                            addProcurementPlanDetailsDTONew.setMaterialId(requiremetsPlanningDetails.getMaterialId());
                            addProcurementPlanDetailsDTONew.setMaterialName(requiremetsPlanningDetails.getMaterialName());
                            addProcurementPlanDetailsDTOList.add(addProcurementPlanDetailsDTONew);
                         }
                       }
                    }
                i++;
            }

            //将参数赋给采购计划DTO
            addProcurementPlanDTO.setOriginalDocumentNumber(JSON.toJSONString(listOriginalDocumentNumber));
            addProcurementPlanDTO.setMaterialUse(requirementsPlanningList.get(0).getMaterialUse());
            addProcurementPlanDTO.setPlanClassification(requirementsPlanningList.get(0).getPlanClassification());
            addProcurementPlanDTO.setStatus(1);
            addProcurementPlanDTO.setPlanningDepartment(user.getDeptId().toString());
            addProcurementPlanDTO.setPlanner(user.getId().toString());
            addProcurementPlanDTO.setWarehouseId(requirementsPlanningList.get(0).getWarehouseId());
            addProcurementPlanDTO.setDemandDepartment(requirementsPlanningList.get(0).getPlanUnit());
            addProcurementPlanDTO.setRemark("系统自动生成");

            //将主表和明细装填入dto，调用新增方法（接口）
            AddProcurementPlanAndDetailsDTO addProcurementPlanAndDetailsDTO = new AddProcurementPlanAndDetailsDTO();
            addProcurementPlanAndDetailsDTO.setAddProcurementPlanDTO(addProcurementPlanDTO);
            addProcurementPlanAndDetailsDTO.setAddProcurementPlanDetailsDTOList(addProcurementPlanDetailsDTOList);
            Result result = add(addProcurementPlanAndDetailsDTO);
            if (result.isOk()){
                ProcurementPlan procurementPlan = (ProcurementPlan) result.getData();
                String docNum = procurementPlan.getPlanNumber();
                String warehouseId = procurementPlan.getWarehouseId();

                // 批量更新需求计划已导入
                List<RequirementsPlanning> requirementsPlannings = requirementsPlanningList.stream().map(r->{
                    RequirementsPlanning requirementsPlanning = new RequirementsPlanning();
                    requirementsPlanning.setIsImported(1);
                    requirementsPlanning.setId(r.getId());
                    requirementsPlanning.setDocumentNumberImported(docNum);
                    return requirementsPlanning;
                }).collect(Collectors.toList());

                requirementsPlanningService.saveOrUpdateBatch(requirementsPlannings);
                List<ProcurementPlanDetails> procurementPlanDetailsList = procurementPlanDetailsService.getProcurementPlanDetailsByDocNumAndWarehouseId(docNum,warehouseId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("main",procurementPlan);
                jsonObject.put("details",procurementPlanDetailsList);
                return Result.success(jsonObject);
            }else {
                return Result.failure("合并新增采购计划单失败！");
            }
        }catch (Exception e){
            log.error("系统异常,新增采购计划单失败！",e);
            return Result.failure("系统异常,合并新增采购计划单失败！");
        }
    }
}

