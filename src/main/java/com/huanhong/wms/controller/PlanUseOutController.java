package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddPlanUseOutAndDetails;
import com.huanhong.wms.entity.dto.AddPlanUseOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;
import com.huanhong.wms.mapper.PlanUseOutMapper;
import com.huanhong.wms.service.IPlanUseOutDetailsService;
import com.huanhong.wms.service.IPlanUseOutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
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

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加领料出库主表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddPlanUseOutAndDetails addPlanUseOutAndDetails) {
        try {
            Result result = planUseOutService.addPlanUseOut(addPlanUseOutAndDetails.getAddPlanUseOutDTO());
            PlanUseOut planUseOut = (PlanUseOut) result.getData();
            String docNum = planUseOut.getDocumentNumber();
            String warehouseId = addPlanUseOutAndDetails.getAddPlanUseOutDTO().getWarehouseId();
            List<AddPlanUseOutDetailsDTO> addPlanUseOutDetailsDTOList = addPlanUseOutAndDetails.getAddPlanUseOutDetailsDTOList();
            if (ObjectUtil.isNotNull(addPlanUseOutDetailsDTOList)) {
                for (AddPlanUseOutDetailsDTO details : addPlanUseOutDetailsDTOList
                ) {
                    details.setUsePlanningDocumentNumber(docNum);
                    details.setWarehouseId(warehouseId);
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

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除领料出库主表", notes = "生成代码")
    @DeleteMapping("/deleteById/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = planUseOutMapper.deleteById(id);
        return render(i > 0);
    }

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

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据ID获取单据信息")
    @GetMapping("getPlanUseOutById/{id}")
    public Result getPlanUseOutById(@PathVariable Integer id){
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

    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据仓库编号获取处于审批中的单据数量")
    @GetMapping("getCountByWarehouseId/{warehouseId}")
    public Result getCountByWarehouseId(@PathVariable String warehouseId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("warehouse_id",warehouseId);
        queryWrapper.eq("status",2);
        Integer count =  planUseOutMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据") ;
    }


    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "流程引擎-领料出库")
    @GetMapping ("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id){
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            PlanUseOut planUseOut = planUseOutService.getPlanUseOutById(id);
            List<PlanUseOutDetails> planUseOutList = planUseOutDetailsService.getListPlanUseOutDetailsByDocNumberAndWarehosue(planUseOut.getDocumentNumber(), planUseOut.getWarehouseId());
            if (ObjectUtil.isNotEmpty(planUseOut)) {

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
                jsonResult.put("tableName","plan_use_out");
                jsonResult.put("main",jsonField(new PlanUseOut()));
                jsonResult.put("details",jsonField(new PlanUseOutDetails()));
                jsonResult.put("mainValue",planUseOut);
                jsonResult.put("detailsValue",planUseOutList);
                jsonResult.put("mainUpdate","/wms/api/v1/plan-use-out/update");
                jsonResult.put("detailsUpdate","/wms/api/v1/plan-use-out-details/update");
                return Result.success(jsonResult);
            } else {
                return Result.failure("未查询到相关信息");
            }
        } catch (Exception e) {
            log.error("查询失败,异常：", e);
            return Result.failure("查询失败，系统异常！");
        }
    }

    public JSONObject jsonField(Object object){
        Field[] fields = object.getClass().getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0;i < fields.length; i++){
            Field field = fields[i];
            field.setAccessible(true);
            String key = field.getName();
            jsonObject.put(key,fieldName(key));
        }
        return jsonObject;
    }

    public JSONObject fieldName(String key){
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key){
            case "serialVersionUID":
                jsonObject.put("name","id");
                jsonObject.put("type","text");
                jsonObject.put("class","hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name","单据编号");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "status":
                jsonObject.put("name","单据状态");
                jsonObject.put("type","select");
                jsonObject.put("class","readOnly");
                //下拉菜单的值
                value.put("1","草拟");
                value.put("2","审批中");
                value.put("3","审批生效");
                value.put("4","作废");
                jsonObject.put("value",value);
                return jsonObject;
            case "planClassification":
                jsonObject.put("name","计划类别");
                jsonObject.put("type","select");
                jsonObject.put("class","readOnly");
                //下拉菜单的值
                value.put("1","正常");
                value.put("2","加急");
                value.put("3","补计划");
                jsonObject.put("value",value);
                return jsonObject;
            case "requisitioningUnit":
                jsonObject.put("name","领用单位");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name","库房ID");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "librarian":
                jsonObject.put("name","库管员");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "costBearingUnit":
                jsonObject.put("name","费用承担单位");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "expenseItem":
                jsonObject.put("name","费用项目");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "materialUse":
                jsonObject.put("name","物资用途");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "requisitionUse":
                jsonObject.put("name","领用用途");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "outStatus":
                jsonObject.put("name","出库状态");
                jsonObject.put("type","select");
                jsonObject.put("class","hiden");
                value.put("0","未出库");
                value.put("1","部分出库");
                value.put("2","全部出库");
                jsonObject.put("value",value);
                return jsonObject;
            case "remark":
                jsonObject.put("name","备注");
                jsonObject.put("type","text");
                jsonObject.put("class","input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name","创建时间");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name","最后更新时间");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "usePlanningDocumentNumber":
                jsonObject.put("name","原单据编号");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name","物料编码");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "materialName":
                jsonObject.put("name","物料名称");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "requisitionQuantity":
                jsonObject.put("name","领用数量");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "approvalsQuantity":
                jsonObject.put("name","批准数量");
                jsonObject.put("type","text");
                jsonObject.put("class","input");
                return jsonObject;
            case "outboundQuantity":
                jsonObject.put("name","实出数量");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "inventoryCredit":
                jsonObject.put("name","库存数量");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "usePlace":
                jsonObject.put("name","使用地点");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "purpose":
                jsonObject.put("name","用途");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
                return jsonObject;
            case "version":
                jsonObject.put("name","版本号");
                jsonObject.put("type","text");
                jsonObject.put("class","hiden");
                return jsonObject;
            default:
                return null;
        }
    }
}

