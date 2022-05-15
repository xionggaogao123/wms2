package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.RequirementsPlanning;
import com.huanhong.wms.entity.RequiremetsPlanningDetails;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.RequirementsPlanningVO;
import com.huanhong.wms.mapper.RequirementsPlanningMapper;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IRequirementsPlanningService;
import com.huanhong.wms.service.IRequiremetsPlanningDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1//requirements-planning")
@ApiSort()
@Api(tags = "需求计划表")
public class RequirementsPlanningController extends BaseController {

    @Resource
    private IRequirementsPlanningService requirementsPlanningService;

    @Resource
    private RequirementsPlanningMapper requirementsPlanningMapper;

    @Resource
    private IRequiremetsPlanningDetailsService requiremetsPlanningDetailsService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询需求计划表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<RequirementsPlanning>> page(@RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   RequirementsPlanningVO requirementsPlanningVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<RequirementsPlanning> pageResult = requirementsPlanningService.pageFuzzyQuery(new Page<>(current, size), requirementsPlanningVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到需求计划信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加需求计划表及明细", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddRequirementsPlanningAndDetailsDTO addRequirementsPlanningAndDetailsDTO) {
        try {
            AddRequirementsPlanningDTO addRequirementsPlanningDTO = addRequirementsPlanningAndDetailsDTO.getAddRequirementsPlanningDTO();
            List<AddRequiremetsPlanningDetailsDTO> addRequirementsPlanningDetailsDTOList = addRequirementsPlanningAndDetailsDTO.getAddRequiremetsPlanningDetailsDTOList();
            Result result = requirementsPlanningService.addRequirementsPlanning(addRequirementsPlanningDTO);
            if (!result.isOk()) {
                return Result.failure("新增需求计划失败！");
            }
            RequirementsPlanning requirementsPlanning = (RequirementsPlanning)result.getData();
            String docNum = requirementsPlanning.getPlanNumber();
            String warehouseId = requirementsPlanning.getWarehouseId();
            if (ObjectUtil.isNotNull(addRequirementsPlanningDetailsDTOList)){
                for (AddRequiremetsPlanningDetailsDTO addRequiremetsPlanningDetailsDTO : addRequirementsPlanningDetailsDTOList) {
                    addRequiremetsPlanningDetailsDTO.setPlanNumber(docNum);
                    addRequiremetsPlanningDetailsDTO.setWarehouseId(warehouseId);
                }
                requiremetsPlanningDetailsService.addRequiremetsPlanningDetails(addRequirementsPlanningDetailsDTOList);
            }
            return result;
        } catch (Exception e) {
            log.error("新增需求计划失败",e);
            return Result.failure("系统异常，新增需求计划失败！");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新需求计划表及明细", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateRequirementsPlanningAndDetailsDTO updateRequirementsPlanningAndDetailsDTO) {
        try {
            UpdateRequirementsPlanningDTO updateRequirementsPlanningDTO = updateRequirementsPlanningAndDetailsDTO.getUpdateRequirementsPlanningDTO();
            List<UpdateRequiremetsPlanningDetailsDTO> updateRequiremetsPlanningDetailsDTOList = updateRequirementsPlanningAndDetailsDTO.getUpdateRequiremetsPlanningDetailsDTOList();
            Result result = requirementsPlanningService.updateRequirementsPlanning(updateRequirementsPlanningDTO);
            if (!result.isOk()) {
                return Result.failure("更新需求计划表失败！");
            }
            return requiremetsPlanningDetailsService.updateRequiremetsPlanningDetails(updateRequiremetsPlanningDetailsDTOList);
        } catch (Exception e) {
            log.error("更新需求计划失败");
            return Result.failure("系统异常：更新需求计划失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除需求计划表", notes = "生成代码")
    @DeleteMapping("deleteByid/{id}")
    public Result delete(@PathVariable Integer id) {

        RequirementsPlanning requirementsPlanning = requirementsPlanningService.getRequirementsPlanningById(id);

        if (ObjectUtil.isNull(requirementsPlanning)){
            return Result.failure("单据不存在！");
        }
        boolean delete = requirementsPlanningService.removeById(id);

        //主表删除成功,删除明细
        if (delete){
            String docNum = requirementsPlanning.getPlanNumber();

            List<RequiremetsPlanningDetails> requiremetsPlanningDetailsList = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsByDocNumAndWarehouseId(docNum,requirementsPlanning.getWarehouseId());

            for (RequiremetsPlanningDetails requiremetsPlanningDetails:requiremetsPlanningDetailsList
            ) {
                requiremetsPlanningDetailsService.removeById(requiremetsPlanningDetails.getId());
            }
        }
        return Result.success("删除成功");
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取需求计划表及其明细")
    @GetMapping("getRequirementsPlanningAndDetailsById/{id}")
    public Result getRequirementsPlanningAndDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        RequirementsPlanning requirementsPlanning = requirementsPlanningService.getRequirementsPlanningById(id);
        if (ObjectUtil.isEmpty(requirementsPlanning)) {
            return Result.failure("未找到对应信息！");
        }
        List<RequiremetsPlanningDetails> requiremetsPlanningDetailsList = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsByDocNumAndWarehouseId(requirementsPlanning.getPlanNumber(), requirementsPlanning.getWarehouseId());
        jsonObject.put("doc", requirementsPlanning);
        jsonObject.put("details", requiremetsPlanningDetailsList);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号和仓库Id获取需求计划表及其明细")
    @GetMapping("/getRequirementsPlanningByDocNumAndWarehouseId")
    public Result getRequirementsPlanningByDocNumAndWarehouseId(
            @RequestParam String docNum, @RequestParam String warehouseId
    ) {
        JSONObject jsonObject = new JSONObject();
        RequirementsPlanning requirementsPlanning = requirementsPlanningService.getRequirementsPlanningByDocNumAndWarehouseId(docNum,warehouseId);
        if (ObjectUtil.isEmpty(requirementsPlanning)){
            return Result.failure("未找到对应信息！");
        }
        List<RequiremetsPlanningDetails> requiremetsPlanningDetailsList = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsByDocNumAndWarehouseId(docNum,warehouseId);
        jsonObject.put("doc", requirementsPlanning);
        jsonObject.put("details", requiremetsPlanningDetailsList);
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
            RequirementsPlanning requirementsPlanning = requirementsPlanningService.getRequirementsPlanningById(id);
            if (ObjectUtil.isNotEmpty(requirementsPlanning)) {
                List<RequiremetsPlanningDetails> requiremetsPlanningDetailsList = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsByDocNumAndWarehouseId(requirementsPlanning.getPlanNumber(),requirementsPlanning.getWarehouseId());
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
                jsonResult.put("tableName", "requirements_planning");
                jsonResult.put("main", entityUtils.jsonField("requirementsPlanning",new RequirementsPlanning()));
                jsonResult.put("details", entityUtils.jsonField("requirementsPlanning",new RequiremetsPlanningDetails()));
                jsonResult.put("mainValue", requirementsPlanning);
                jsonResult.put("detailsValue", requiremetsPlanningDetailsList);
                jsonResult.put("mainKey","updateRequirementsPlanningDTO");
                jsonResult.put("detailKey","updateRequiremetsPlanningDetailsDTOList");
                jsonResult.put("mainUpdate", "/wms/api/v1/requirements-planning/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/requiremets-planning-details/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/requirements-planning/missionCompleted");
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
            RequirementsPlanning requirementsPlanning = requirementsPlanningService.getRequirementsPlanningById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotEmpty(requirementsPlanning)) {
                UpdateRequirementsPlanningDTO updateRequirementsPlanningDTO = new UpdateRequirementsPlanningDTO();
                updateRequirementsPlanningDTO.setId(id);
                updateRequirementsPlanningDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateRequirementsPlanningDTO.setPlanStatus(2);
                Result result = requirementsPlanningService.updateRequirementsPlanning(updateRequirementsPlanningDTO);
                if (result.isOk()) {
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("需求计划单异常,无法进入流程引擎");
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
            RequirementsPlanning requirementsPlanning = requirementsPlanningService.getRequirementsPlanningByProcessInstanceId(processInstanceId);
            if (ObjectUtil.isNotNull(requirementsPlanning)) {
                UpdateRequirementsPlanningDTO updateRequirementsPlanningDTO = new UpdateRequirementsPlanningDTO();
                updateRequirementsPlanningDTO.setId(requirementsPlanning.getId());
                /**
                 *  单据状态由审批中改为审批生效
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateRequirementsPlanningDTO.setPlanStatus(3);
                return requirementsPlanningService.updateRequirementsPlanning(updateRequirementsPlanningDTO);
            } else {
                return Result.failure("单据异常无法完成");
            }
        } catch (Exception e) {
            log.error("完成审批接口异常", e);
            return Result.failure("系统异常");
        }
    }


}

