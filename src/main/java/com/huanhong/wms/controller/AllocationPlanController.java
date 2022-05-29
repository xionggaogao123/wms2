package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.AllocationPlanVO;
import com.huanhong.wms.mapper.AllocationPlanMapper;
import com.huanhong.wms.service.IAllocationPlanDetailService;
import com.huanhong.wms.service.IAllocationPlanService;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IOutboundRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

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

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IOutboundRecordService outboundRecordService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数")
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
        return allocationPlanService.add(addAllocationPlanAndDetailsDTO);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新调拨计划主表及明细")
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
        return allocationPlanService.delete(id);
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
                jsonResult.put("mainKey", "updateAllocationOutDTO");
                jsonResult.put("detailKey", "updateAllocationOutDetailsDTOS");
                jsonResult.put("mainUpdate", "/wms/api/v1/allocation-plan/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/allocation-plan-detail/update");
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
                    //新增出库记录并减库存
                    Result resultAnoher = allocationPlanService.addOutboundRecordUpdateInventory(allocationPlan);
                    if (!resultAnoher.isOk()) {
                        return resultAnoher;
                    }
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("未找到此单据,无法进入流程引擎");
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

                //获取此单据下的明细单，校验批准数量是否等于应出数量，若不同回滚库存并更新详细信息
                allocationPlanService.updateOutboundRecordAndInventory(allocationPlan);


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


    private void updateOutboundRecordAndInventory(AllocationPlan allocationPlan) {

    }

    /**
     * 新增库存记录以及更新库存信息--发起时调用
     */
    public Result addOutboundRecordUpdateInventory(AllocationPlan allocationPlan) {

        try {
            List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());

            //留存出库记录
            AddOutboundRecordDTO addOutboundRecordDTO = new AddOutboundRecordDTO();
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
                                        addOutboundRecordDTO.setSalesUnitPrice(inventoryInformation.getSalesUnitPrice());
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
                                        addOutboundRecordDTO.setSalesUnitPrice(inventoryInformation.getSalesUnitPrice());
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
                        addOutboundRecordDTO.setDocumentNumber(allocationPlanDetail.getAllocationNumber());
                        addOutboundRecordDTO.setWarehouseId(allocationPlan.getSendWarehouse());
                        addOutboundRecordDTO.setMaterialCoding(allocationPlanDetail.getMaterialCoding());
                        addOutboundRecordDTO.setStatus(0);
                        addOutboundRecordDTO.setOutType(2);
                        //放入新增出库记录List
                        Result result = outboundRecordService.addOutboundRecord(addOutboundRecordDTO);

                        if (!result.isOk()) {
                            return Result.failure("新增库存记录失败");
                        } else {
                            return Result.success("新增库存记录成功");
                        }
                    } else {
                        return Result.failure("物料：" + allocationPlanDetail.getMaterialCoding() + " 库存不足，请重拟领用单！");
                    }
                }
            } else {
                return Result.failure("未查询到明细单据信息");
            }
            return Result.failure("未知错误");
        } catch (Exception e) {
            log.error("新增出库记录或更新库存异常");
            return Result.failure("新增出库记录或更新库存异常");
        }
    }


}

