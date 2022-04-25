package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.*;
import com.huanhong.wms.mapper.PlanUseOutMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/v1//plan-use-out")
@ApiSort()
@Api(tags = "领料出库主表")
public class PlanUseOutController extends BaseController {

    @Resource
    private IPlanUseOutService planUseOutService;

    @Resource
    private PlanUseOutMapper planUseOutMapper;

    @Resource
    private IPlanUseOutDetailsService planUseOutDetailsService;

    @Resource
    private IProcessTemplateService processTemplateService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IOutboundRecordService outboundRecordService;

    @Resource
    private IMaterialService materialService;

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private IVariableService variableService;

    @Resource
    private IAllocationOutService allocationOutService;


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询领料出库主表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<PlanUseOut>> page(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         PlanUseOutVO planUseOutVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<PlanUseOut> pageResult = planUseOutService.pageFuzzyQuery(new Page<>(current, size), planUseOutVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到出库单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 14)
    @ApiOperation(value = ("PDA端分页查询"))
    @GetMapping("/PDApage")
    public Result<Page<PlanUseOut>> pagePda(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         PlanUseOutVO planUseOutVO
    ){
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<PlanUseOut> pageResult = planUseOutService.pageFuzzyQueryPDA(new Page<>(current,size),planUseOutVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到出库单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加领料出库主表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddPlanUseOutAndDetails addPlanUseOutAndDetails) {
        try {
            Result result = planUseOutService.addPlanUseOut(addPlanUseOutAndDetails.getAddPlanUseOutDTO());

            if (!result.isOk()) {
                return Result.failure("新增出库失败");
            }
            PlanUseOut planUseOut = (PlanUseOut) result.getData();
            String docNum = planUseOut.getDocumentNumber();
            String warehouseId = planUseOut.getWarehouseId();
            List<AddPlanUseOutDetailsDTO> addPlanUseOutDetailsDTOList = addPlanUseOutAndDetails.getAddPlanUseOutDetailsDTOList();
            if (ObjectUtil.isNotNull(addPlanUseOutDetailsDTOList)) {
                for (AddPlanUseOutDetailsDTO details : addPlanUseOutDetailsDTOList
                ) {
                    details.setUsePlanningDocumentNumber(docNum);
                    details.setWarehouseId(warehouseId);
                    details.setApprovalsQuantity(details.getRequisitionQuantity());
                }
                planUseOutDetailsService.addPlanUseOutDetails(addPlanUseOutDetailsDTOList);
            }
            return result;
        } catch (Exception e) {
            log.error("添加出库单出错，异常", e);
            return Result.failure("系统异常：出库单添加失败。");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新领料出库主表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdatePlanUseOutDTO updatePlanUseOutDTO) {
        return planUseOutService.updatePlanUseOut(updatePlanUseOutDTO);
    }

    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "更新领料出库主表及明细", notes = "生成代码")
    @PutMapping("/updatePlanUseOutAndDetails")
    public Result updatePlanUseOutAndDetails(@Valid @RequestBody UpdatePlanUseOutAndDetailsDTO updatePlanUseOutAndDetailsDTO) {
        try {
            Result resultUpdatePlanUseOut = planUseOutService.updatePlanUseOut(updatePlanUseOutAndDetailsDTO.getUpdatePlanUseOutDTO());
            if (resultUpdatePlanUseOut.isOk()) {
                return planUseOutDetailsService.updatePlanUseOutDetails(updatePlanUseOutAndDetailsDTO.getUpdatePlanUseOutDetailsDTOList());
            } else {
                return Result.failure("更新失败");
            }
        } catch (Exception e) {
            return Result.failure("系统异常，更新失败");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除领料出库主表", notes = "生成代码")
    @DeleteMapping("/deleteById/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = planUseOutMapper.deleteById(id);
        return render(i > 0);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "documentNumber", value = "单据编号"),
            @ApiImplicitParam(name = "wareHouseId", value = "仓库编号")
    })
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据单据编号和仓库id获取详细信息")
    @GetMapping("/getPlanUseOutByDocNumAndWarhouseId/{documentNumber}&{wareHouseId}")
    public Result getPlanUseOutByDocNumAndWarhouseId(@PathVariable String documentNumber,
                                                     @PathVariable String wareHouseId) {
        try {
            Map map = new HashMap();
            PlanUseOut planUseOut = planUseOutService.getPlanUseOutByDocNumAndWarhouseId(documentNumber, wareHouseId);
            List<PlanUseOutDetails> planUseOutList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(documentNumber, wareHouseId);
            if (ObjectUtil.isNotEmpty(planUseOut)) {
                map.put("doc", planUseOut);
                map.put("details", planUseOutList);
            } else {
                return Result.failure("未查询到相关信息");
            }
            return Result.success(map);
        } catch (Exception e) {
            log.error("查询失败,异常：", e);
            return Result.failure("查询失败，系统异常！");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据ID获取单据信息")
    @GetMapping("getPlanUseOutById/{id}")
    public Result getPlanUseOutById(@PathVariable Integer id) {
        try {
            Map map = new HashMap();
            PlanUseOut planUseOut = planUseOutService.getPlanUseOutById(id);
            List<PlanUseOutDetails> planUseOutList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId());
            if (ObjectUtil.isNotEmpty(planUseOut)) {
                map.put("plan_use_out", planUseOut);
                map.put("plan_use_out_details", planUseOutList);
            } else {
                return Result.failure("未查询到相关信息");
            }
            return Result.success(map);
        } catch (Exception e) {
            log.error("查询失败,异常：", e);
            return Result.failure("查询失败，系统异常！");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseId", value = "仓库编号")
    })
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据仓库编号获取处于审批中的单据数量")
    @GetMapping("getCountByWarehouseId/{warehouseId}")
    public Result getCountByWarehouseId(@PathVariable String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.eq("status", 2);
        Integer count = planUseOutMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "流程引擎-领料出库-查询")
    @GetMapping("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id) {

        EntityUtils entityUtils = new EntityUtils();
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            PlanUseOut planUseOut = planUseOutService.getPlanUseOutById(id);
            if (ObjectUtil.isNotEmpty(planUseOut)) {
                List<PlanUseOutDetails> planUseOutList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId());
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
                jsonResult.put("tableName", "plan_use_out");
                // 根据领料用途获取对应部门信息
                Variable variable = variableService.getOne(
                        Wrappers.<Variable>lambdaQuery().eq(Variable::getKey, "material_use")
                                .eq(Variable::getValue, planUseOut.getMaterialUse()).last("limit 1"));
                Integer deptId = null;
                if (null != variable) {
                    deptId = Convert.toInt(variable.getRemark());
                }
                jsonResult.put("processTemplate", processTemplateService.getProcessTemplateListByProcessCodeAndWarhouseId("plan_use_out", planUseOut.getWarehouseId(), null, deptId));
                jsonResult.put("main", entityUtils.jsonField("planUseOut", new PlanUseOut()));
                jsonResult.put("details", entityUtils.jsonField("planUseOut", new PlanUseOutDetails()));
                jsonResult.put("mainValue", planUseOut);
                jsonResult.put("detailsValue", planUseOutList);
                jsonResult.put("mainKey","updatePlanUseOutDTO");
                jsonResult.put("detailKey","updatePlanUseOutDetailsDTOList");
                jsonResult.put("mainUpdate", "/wms/api/v1/plan-use-out/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/plan-use-out-details/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/plan-use-out/missionCompleted");
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
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "流程引擎-领料出库-发起")
    @PutMapping("/missionStarts")
    public Result missionStarts(@RequestParam Integer id,
                                @RequestParam String processInstanceId) {

        try {
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            /**
             * 获取出库单信息
             */
            PlanUseOut planUseOut = planUseOutService.getPlanUseOutById(id);

            if (ObjectUtil.isNotNull(planUseOut)) {
                UpdatePlanUseOutDTO updatePlanUseOutDTO = new UpdatePlanUseOutDTO();
                updatePlanUseOutDTO.setId(id);
                updatePlanUseOutDTO.setProcessInstanceId(processInstanceId);
                //提交审批时 将更新数量
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updatePlanUseOutDTO.setStatus(2);
                Result result = planUseOutService.updatePlanUseOut(updatePlanUseOutDTO);
                if (result.isOk()) {
                    //新增出库记录并减库存
                    Result resultAnother = planUseOutService.addOutboundRecordUpdateInventory(planUseOut);
                    if (!resultAnother.isOk()) {
                        return resultAnother;
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
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "流程引擎-领料出库-完成审批")
    @PutMapping("/missionCompleted")
    public Result missionCompleted(@RequestParam String processInstanceId) {

        try {
            //通过流程Id查询出单据Id
            PlanUseOut planUseOut = planUseOutService.getPlanUseOutByProcessInstanceId(processInstanceId);

            if (ObjectUtil.isNotNull(planUseOut)) {

                planUseOutService.updateOutboundRecordAndInventory(planUseOut);

                UpdatePlanUseOutDTO updatePlanUseOutDTO = new UpdatePlanUseOutDTO();
                updatePlanUseOutDTO.setId(planUseOut.getId());
                /**
                 *  单据状态由审批中改为审批生效
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updatePlanUseOutDTO.setStatus(3);
                return planUseOutService.updatePlanUseOut(updatePlanUseOutDTO);
            } else {
                return Result.failure("单据异常无法完成");
            }
        } catch (Exception e) {
            log.error("完成审批接口异常", e);
            return Result.failure("系统异常");
        }
    }

    /**
     * 模糊查询
     *
     * @return
     */
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "物料编码和物料名称模糊查询信息及库存")
    @GetMapping("/pagingFuzzyQueryByMaterialCodingOrName")
    public Result page(PdaMaterialVO pdaMaterialVO
                       ) {
        try {
            List<Material> materialslist = materialService.getMaterialListByKey(pdaMaterialVO);
            if (ObjectUtil.isNull(materialslist)) {
                return Result.failure("未找到对应信息");
            }
            JSONArray jsonArray = new JSONArray();
            for (Material material : materialslist
            ) {
                JSONObject jsonObject = new JSONObject();
                String materialCoding = material.getMaterialCoding();
                Double num = inventoryInformationService.getNumByMaterialCodingAndWarehouseId(materialCoding, pdaMaterialVO.getWarehouseId());
                List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndWarehouseId(materialCoding,pdaMaterialVO.getWarehouseId());
                jsonObject.put("material", material);
                jsonObject.put("inventory", num);
                jsonObject.put("inventoryList", inventoryInformationList);
                jsonArray.add(jsonObject);
            }
            return Result.success(jsonArray);
        } catch (Exception e) {
            log.error("分页查询异常" + e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }


    @ApiOperationSupport(order = 13)
    @ApiOperation(value = "根据出库单和仓库编号获取出库明细加出库记录（预生成、锁库）包含物料详情")
    @GetMapping("/getPlanUseOutAndOutboundRecordByDocNumAndWarehouseId")
    public Result getPlanUseOutAndOutboundRecordByDocNumAndWarehouseId(@RequestParam String docNum, @RequestParam String warehouseId) {
        List listResult = new ArrayList();
        List<PlanUseOutDetails> planUseOutDetailsList;
        //加一层主表数据
        JSONObject jsonObjectMain = new JSONObject();
        //存储于list
        JSONObject jsonObjectList = new JSONObject();
        /**
         * 判断out_status
         * 0-未出库   1-部分出库   2-全部出库
         */
        for (int i = 0; i < 3; i++ ){
            planUseOutDetailsList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosueAndOutStatus(docNum, warehouseId, i);
            if (ObjectUtil.isNotNull(planUseOutDetailsList)) {
                jsonObjectList.put(String.valueOf(i), getOut(planUseOutDetailsList));
            }
        }
        listResult.add(jsonObjectList);
        //主表
        PlanUseOut planUseOut = planUseOutService.getPlanUseOutByDocNumAndWarhouseId(docNum,warehouseId);
        String warehouseName = warehouseManagementService.getWarehouseByWarehouseId(planUseOut.getWarehouseId()).getWarehouseName();
        jsonObjectMain.put("warehouseName",warehouseName);
        jsonObjectMain.put("panUseOut",planUseOut);
        jsonObjectMain.put("list",listResult);
        return Result.success(jsonObjectMain);
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 14)
    @ApiOperation(value = "分页查询领料出库主表及调拨出库主表", notes = "生成代码")
    @GetMapping("/outboundForPDA")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       OutboundDocOfPageQueryForPdaVO outboundDocOfPageQueryForPdaVO

    ) {
        try {
            List<OutboundForPdaVO> outboundForPdaVOList = new ArrayList<>();
            //查询领料出库
            PlanUseOutVO planUseOutVO = new PlanUseOutVO();
            BeanUtil.copyProperties(outboundDocOfPageQueryForPdaVO,planUseOutVO);
            Page<PlanUseOut> pageResultPlanUseOut = planUseOutService.pageFuzzyQueryPDA(new Page<>(current, size), planUseOutVO);
            /**
             * 组装领料出库参数
             */
            for (PlanUseOut planUseOut : pageResultPlanUseOut.getRecords()
            ) {
                OutboundForPdaVO outboundForPdaVO = new OutboundForPdaVO();
                BeanUtil.copyProperties(planUseOut,outboundForPdaVO);
                //单据编号
                outboundForPdaVO.setDocNum(planUseOut.getDocumentNumber());
                //单据类型
                outboundForPdaVO.setDocType(0);
                outboundForPdaVOList.add(outboundForPdaVO);
            }

            //查询调拨出库
            AllocationOutVO allocationOutVO = new AllocationOutVO();
            BeanUtil.copyProperties(outboundDocOfPageQueryForPdaVO,allocationOutVO);
            allocationOutVO.setAllocationOutNumber(outboundDocOfPageQueryForPdaVO.getDocumentNumber());
            Page<AllocationOut> pageResultAllocationOut = allocationOutService.pageFuzzyQueryPDA(new Page<>(current,size),allocationOutVO);
            if (ObjectUtil.isAllEmpty(pageResultAllocationOut.getRecords())&&ObjectUtil.isAllEmpty(pageResultAllocationOut)) {
                return Result.success(null, "未查询到调拨入库单信息");
            }
            /**
             * 组装领料出库参数
             */
            List<OutboundForPdaVO> outboundForPdaVOListNew = new ArrayList<>();
            for (AllocationOut allocationOut: pageResultAllocationOut.getRecords()
            ) {
                OutboundForPdaVO outboundForPdaVO = new OutboundForPdaVO();
                BeanUtil.copyProperties(allocationOut,outboundForPdaVO);
                //单据编号
                outboundForPdaVO.setDocNum(allocationOut.getAllocationOutNumber());
                //单据类型
                outboundForPdaVO.setDocType(1);
                //调入仓库
                outboundForPdaVO.setEnterWarehouse(allocationOut.getEnterWarehouse());
                outboundForPdaVOListNew.add(outboundForPdaVO);
            }
            List<OutboundForPdaVO> outboundForPdaVOListLast = ListUtils.union(outboundForPdaVOList,outboundForPdaVOListNew);
            return Result.success(outboundForPdaVOListLast);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }


    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "PDA根据出库单Id获取领料出库或调拨出库明细list加出库记录（预生成、锁库）包含物料详情")
    @GetMapping("/getPlanUseOutOrAllocationOutById")
    public Result getPlanUseOutAndOutboundRecordById(@RequestParam Integer id) {

        try {

        List listResult = new ArrayList();

        //加一层主表数据
        JSONObject jsonObjectMain = new JSONObject();

        //存储于list
        JSONObject jsonObjectList = new JSONObject();

        //根据单据id获取出库单主表
        PlanUseOut planUseOut = planUseOutService.getPlanUseOutById(id);
        if (ObjectUtil.isEmpty(planUseOut)){
            return Result.failure("未查询到单据信息！");
        }

        List<PlanUseOutDetails> planUseOutDetailsList;
        /**
         * 判断out_status
         * 0-未出库   1-部分出库   2-全部出库
         */
        for (int i = 0; i < 3; i++ ){
            planUseOutDetailsList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosueAndOutStatus(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId(), i);
            if (ObjectUtil.isNotNull(planUseOutDetailsList)) {
                jsonObjectList.put(String.valueOf(i), getOut(planUseOutDetailsList));
            }
        }

        listResult.add(jsonObjectList);
        //主表
        String warehouseName = warehouseManagementService.getWarehouseByWarehouseId(planUseOut.getWarehouseId()).getWarehouseName();
        jsonObjectMain.put("warehouseName",warehouseName);
        jsonObjectMain.put("panUseOut",planUseOut);
        jsonObjectMain.put("list",listResult);
        return Result.success(jsonObjectMain);

        }catch (Exception e){
        log.error("PDA查询领料出库明细异常",e);
        return Result.failure("PDA查询领料出库明细异常!");
       }
    }




    /**
     * 新增库存记录以及更新库存信息--发起时调用
     */
    public Result addOutboundRecordUpdateInventory(PlanUseOut planUseOut) {

        try {
            List<PlanUseOutDetails> planUseOutDetailsList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId());

            //留存出库记录
            AddOutboundRecordDTO addOutboundRecordDTO = new AddOutboundRecordDTO();
            /**
             * 获取当前库存是否满足领用
             * 1.warehouseId和materialCoding
             */
            if (ObjectUtil.isNotEmpty(planUseOutDetailsList)) {
                for (PlanUseOutDetails planUseOutDetails : planUseOutDetailsList
                ) {
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
        } catch (Exception e) {
            log.error("新增出库记录或更新库存异常");
            return Result.failure("新增出库记录或更新库存异常");
        }
    }


    public List<JSONObject> getOut(List<PlanUseOutDetails> planUseOutDetailsList) {
        List<JSONObject> listResult = new ArrayList<>();
        for (PlanUseOutDetails planUseOutDetails : planUseOutDetailsList
        ) {
            JSONObject jsonObject = new JSONObject();
            List<OutboundRecord> outboundRecordList = outboundRecordService.getOutboundRecordListByDocNumAndWarehouseId(planUseOutDetails.getUsePlanningDocumentNumber(), planUseOutDetails.getWarehouseId());
            if (ObjectUtil.isEmpty(outboundRecordList)) {
                Result.failure("未查询到出库记录单相关信息");
            }
            jsonObject.put("planUseOutDetails", planUseOutDetails);
            jsonObject.put("outboundList", outboundRecordList);
            jsonObject.put("material", materialService.getMeterialByMeterialCode(planUseOutDetails.getMaterialCoding()));
            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndWarehouseId(planUseOutDetails.getMaterialCoding(),planUseOutDetails.getWarehouseId());
            jsonObject.put("inventory",inventoryInformationList);
            listResult.add(jsonObject);
        }
        return listResult;
    }


}

