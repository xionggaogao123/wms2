package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcurementPlan;
import com.huanhong.wms.entity.ProcurementPlanDetails;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.ProcurementPlanVO;
import com.huanhong.wms.mapper.ProcurementPlanMapper;
import com.huanhong.wms.service.IProcurementPlanDetailsService;
import com.huanhong.wms.service.IProcurementPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1//procurement-plan")
@ApiSort()
@Api(tags = "采购计划主表")
public class ProcurementPlanController extends BaseController {

    @Resource
    private IProcurementPlanService procurementPlanService;

    @Resource
    private ProcurementPlanMapper procurementPlanMapper;

    @Resource
    private IProcurementPlanDetailsService procurementPlanDetailsService;

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
        try {
            AddProcurementPlanDTO addProcurementPlanDTO = addProcurementPlanAndDetailsDTO.getAddProcurementPlanDTO();
            List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList = addProcurementPlanAndDetailsDTO.getAddProcurementPlanDetailsDTOList();
            Result result = procurementPlanService.addProcurementPlan(addProcurementPlanDTO);
            if (!result.isOk()) {
                return Result.failure("新增采购计划失败！");
            }
            ProcurementPlan procurementPlan = (ProcurementPlan) result.getData();
            String docNum = procurementPlan.getPlanNumber();
            String warehouseId = procurementPlan.getWarehouseId();
            if (ObjectUtil.isNotNull(addProcurementPlanDetailsDTOList)){
                for (AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTO : addProcurementPlanDetailsDTOList) {
                    addProcurementPlanDetailsDTO.setPlanNumber(docNum);
                    addProcurementPlanDetailsDTO.setWarehouseId(warehouseId);
                }
                procurementPlanDetailsService.addProcurementPlanDetails(addProcurementPlanDetailsDTOList);
            }
            return result;
        } catch (Exception e) {
            log.error("新增采购计划失败");
            return Result.failure("系统异常，新增采购计划失败！");
        }
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
        int i = procurementPlanMapper.deleteById(id);
        return render(i > 0);
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
}

