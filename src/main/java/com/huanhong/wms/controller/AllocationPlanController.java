package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.ArrivalVerification;
import com.huanhong.wms.entity.ArrivalVerificationDetails;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.AllocationPlanVO;
import com.huanhong.wms.service.IAllocationPlanDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.AllocationPlanMapper;
import com.huanhong.wms.service.IAllocationPlanService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1//allocation-plan")
@ApiSort()
@Api(tags = "调拨计划主表")
@Slf4j
public class AllocationPlanController extends BaseController {

    @Resource
    private IAllocationPlanService allocationPlanService;

    @Resource
    private AllocationPlanMapper allocationPlanMapper;

    @Resource
    private IAllocationPlanDetailService allocationPlanDetailService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询调拨计划主表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<AllocationPlan>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             AllocationPlanVO allocationPlanVO
                                             ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<AllocationPlan> pageResult = allocationPlanService.pageFuzzyQuery(new Page<>(current, size), allocationPlanVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到调拨单计划信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加调拨计划主表")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddAllocationPlanAndDetailsDTO addAllocationPlanAndDetailsDTO) {
            try {
                AddAllocationPlanDTO addAllocationPlanDTO = addAllocationPlanAndDetailsDTO.getAddAllocationPlanDTO();
                List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList = addAllocationPlanAndDetailsDTO.getAddAllocationPlanDetailDTOList();
                Result result = allocationPlanService.addAllocationPlan(addAllocationPlanDTO);
                if (!result.isOk()) {
                    return Result.failure("新增调拨计失败！");
                }
                AllocationPlan allocationPlan = (AllocationPlan) result.getData();
                String docNum = allocationPlan.getAllocationNumber();
                for (AddAllocationPlanDetailDTO addAllocationPlanDetailDTO : addAllocationPlanDetailDTOList) {
                    addAllocationPlanDetailDTO.setAllocationNumber(docNum);
                }
                return allocationPlanDetailService.addAllocationPlanDetails(addAllocationPlanDetailDTOList);
            } catch (Exception e) {
                log.error("新增调拨计划失败");
                return Result.failure("系统异常，新增调拨计划失败！");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新调拨计划主表")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateAllocationPlanAndDetailsDTO updateAllocationPlanAndDetailsDTO) {
            try {
                UpdateAllocationPlanDTO updateAllocationPlanDTO = updateAllocationPlanAndDetailsDTO.getUpdateAllocationPlanDTO();
                List<UpdateAllocationPlanDetailDTO> updateAllocationPlanDetailDTOList = updateAllocationPlanAndDetailsDTO.getUpdateAllocationPlanDetailDTOList();
                Result result = allocationPlanService.updateAllocationPlan(updateAllocationPlanDTO);
                if (!result.isOk()) {
                    return Result.failure("更新调拨计划单失败！");
                }
                return allocationPlanDetailService.updateAllocationPlanDetails(updateAllocationPlanDetailDTOList);
            } catch (Exception e) {
                log.error("更新调拨计划单失败");
                return Result.failure("系统异常：更新调拨计划失败!");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除调拨计划主表", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            int i = allocationPlanMapper.deleteById(id);
            return render(i > 0);
        }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取调拨计划表及其明细")
    @GetMapping("getAllocationPlanAndDetailsById/{id}")
    public Result getAllocationPlanAndDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        AllocationPlan allocationPlan = allocationPlanService.getAllocationPlanById(id);
        if (ObjectUtil.isEmpty(allocationPlan)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
        jsonObject.put("doc", allocationPlan);
        jsonObject.put("details", allocationPlanDetailsList);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号获取到货检验单及其明细")
    @GetMapping("/getAllocationPlanAndDetailsByDocNum")
    public Result getAllocationPlanAndDetailsByDocNum(@RequestParam String docNum) {
        JSONObject jsonObject = new JSONObject();
        AllocationPlan allocationPlan = allocationPlanService.getAllocationPlanByDocNumber(docNum);
        if (ObjectUtil.isEmpty(allocationPlan)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
        jsonObject.put("doc", allocationPlan);
        jsonObject.put("details", allocationPlanDetailsList);
        return Result.success(jsonObject);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "流程引擎-到货检验-查询")
    @GetMapping("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id) {

        EntityUtils entityUtils = new EntityUtils();
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            AllocationPlan allocationPlan = allocationPlanService.getAllocationPlanById(id);
            if (ObjectUtil.isNotEmpty(allocationPlan)) {
                List<AllocationPlanDetail> allocationPlanDetails = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
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
                jsonResult.put("tableName", "allocation_plan");
                jsonResult.put("main", entityUtils.jsonField("allocationPlan", new AllocationPlan()));
                jsonResult.put("details", entityUtils.jsonField("allocationPlan", new AllocationPlanDetail()));
                jsonResult.put("mainValue", allocationPlan);
                jsonResult.put("detailsValue", allocationPlanDetails);
                jsonResult.put("mainUpdate", "/wms/api/v1/allocation-plan/update");
                jsonResult.put("detailsUpdate", "/wms/api/allocation-plan-detail/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/allocation-plan/missionCompleted");
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
            AllocationPlan allocationPlan = allocationPlanService.getAllocationPlanById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotEmpty(allocationPlan)) {
                UpdateAllocationPlanDTO updateAllocationPlanDTO = new UpdateAllocationPlanDTO();
                updateAllocationPlanDTO.setId(id);
                updateAllocationPlanDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateAllocationPlanDTO.setPlanStatus(2);
                Result result = allocationPlanService.updateAllocationPlan(updateAllocationPlanDTO);
                if (result.isOk()) {
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("调拨计划单异常,无法进入流程引擎");
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
            AllocationPlan allocationPlan = allocationPlanService.getAllocationPlanByProcessInstanceId(processInstanceId);
            if (ObjectUtil.isNotEmpty(allocationPlan)) {
                UpdateAllocationPlanDTO updateAllocationPlanDTO = new UpdateAllocationPlanDTO();
                updateAllocationPlanDTO.setId(allocationPlan.getId());
                /**
                 *  单据状态由审批中改为审批生效
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateAllocationPlanDTO.setPlanStatus(3);
                return allocationPlanService.updateAllocationPlan(updateAllocationPlanDTO);
            } else {
                return Result.failure("单据异常无法完成");
            }
        } catch (Exception e) {
            log.error("完成审批接口异常", e);
            return Result.failure("系统异常");
        }
    }

}

