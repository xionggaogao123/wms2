package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ArrivalVerification;
import com.huanhong.wms.entity.ArrivalVerificationDetails;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.ArrivalVerificationVO;
import com.huanhong.wms.mapper.ArrivalVerificationMapper;
import com.huanhong.wms.service.IArrivalVerificationDetailsService;
import com.huanhong.wms.service.IArrivalVerificationService;
import com.huanhong.wms.service.IMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@ApiSort()
@Api(tags = "到货检验主表")
@RestController
@Slf4j
@RequestMapping("/v1//arrival-verification")
public class ArrivalVerificationController extends BaseController {

    @Resource
    private IArrivalVerificationService arrivalVerificationService;

    @Resource
    private ArrivalVerificationMapper arrivalVerificationMapper;

    @Resource
    private IArrivalVerificationDetailsService arrivalVerificationDetailsService;

    @Resource
    private IMaterialService materialService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询到货检验主表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<ArrivalVerification>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  ArrivalVerificationVO arrivalVerificationVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ArrivalVerification> pageResult = arrivalVerificationService.pageFuzzyQuery(new Page<>(current, size), arrivalVerificationVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到到货检验单信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加到货检验主表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddArrivalVerificationAndDetailsDTO addArrivalVerificationAndDetailsDTO) {
        try {
            AddArrivalVerificationDTO addArrivalVerificationDTO = addArrivalVerificationAndDetailsDTO.getAddArrivalVerificationDTO();
            List<AddArrivalVerificationDetailsDTO> addArrivalVerificationDetailsDTOList = addArrivalVerificationAndDetailsDTO.getAddArrivalVerificationDetailsDTOList();
            Result result = arrivalVerificationService.addArrivalVerification(addArrivalVerificationDTO);
            if (!result.isOk()) {
                return Result.failure("新增到货检验计划失败！");
            }
            ArrivalVerification arrivalVerification = (ArrivalVerification) result.getData();
            String docNum = arrivalVerification.getVerificationDocumentNumber();
            for (AddArrivalVerificationDetailsDTO addArrivalVerificationDetailsDTO : addArrivalVerificationDetailsDTOList) {
                addArrivalVerificationDetailsDTO.setDocumentNumber(docNum);
            }
            return arrivalVerificationDetailsService.addArrivalVerificationDetails(addArrivalVerificationDetailsDTOList);
        } catch (Exception e) {
            log.error("新增采购计划失败");
            return Result.failure("系统异常，新增采购计划失败！");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新到货检验主表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateArrivalVerificationAndDetailsDTO updateArrivalVerificationAndDetailsDTO) {
        try {
            UpdateArrivalVerificationDTO updateArrivalVerificationDTO = updateArrivalVerificationAndDetailsDTO.getUpdateArrivalVerificationDTO();
            List<UpdateArrivalVerificationDetailsDTO> updateArrivalVerificationDetailsDTOList = updateArrivalVerificationAndDetailsDTO.getUpdateArrivalVerificationDetailsDTOList();
            Result result = arrivalVerificationService.updateArrivalVerification(updateArrivalVerificationDTO);
            if (!result.isOk()) {
                return Result.failure("更新到货检验计划表失败！");
            }
            return arrivalVerificationDetailsService.updateArrivalVerificationDetails(updateArrivalVerificationDetailsDTOList);
        } catch (Exception e) {
            log.error("更新采购计划失败");
            return Result.failure("系统异常：更新到货检验失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除到货检验主表", notes = "生成代码")
    @DeleteMapping("delte/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(arrivalVerificationService.removeById(id));
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取到货检验计划表及其明细")
    @GetMapping("getArrivalVerificationDetailsById/{id}")
    public Result getArrivalVerificationDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationById(id);
        if (ObjectUtil.isEmpty(arrivalVerification)) {
            return Result.failure("未找到对应信息！");
        }
        List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsByDocNumAndWarehouseId(arrivalVerification.getVerificationDocumentNumber(),arrivalVerification.getWarehouseId());
        jsonObject.put("doc", arrivalVerification);
        jsonObject.put("details", arrivalVerificationDetailsList);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "PDA-根据ID获取到货检验计划表及其明细包含物料详情")
    @GetMapping("getArrivalVerificationDetailsIncludeMaterialById/{id}")
    public Result getArrivalVerificationDetailsIncludeMaterialById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationById(id);
        if (ObjectUtil.isEmpty(arrivalVerification)) {
            return Result.failure("未找到对应信息！");
        }
        JSONArray jsonArray = new JSONArray();
        List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsByDocNumAndWarehouseId(arrivalVerification.getVerificationDocumentNumber(),arrivalVerification.getWarehouseId());
        for (ArrivalVerificationDetails arrivalVerificationDetails:arrivalVerificationDetailsList
             ) {
             JSONObject jsonObjectDetails = new JSONObject();
             Material material = materialService.getMeterialByMeterialCode(arrivalVerificationDetails.getMaterialCoding());
             jsonObjectDetails.put("material",material);
             jsonObjectDetails.put("details", arrivalVerificationDetails);
             jsonArray.add(jsonObjectDetails);
        }

        jsonObject.put("doc", arrivalVerification);
        jsonObject.put("detailsList", jsonArray);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号和仓库Id获取到货检验单及其明细")
    @GetMapping("/getRequirementsPlanningByDocNumAndWarehouseId")
    public Result getRequirementsPlanningByDocNumAndWarehouseId(
            @RequestParam String docNum, @RequestParam String warehouseId
    ) {
        JSONObject jsonObject = new JSONObject();
        ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationByDocNumberAndWarhouseId(docNum,warehouseId);
        if (ObjectUtil.isEmpty(arrivalVerification)) {
            return Result.failure("未找到对应信息！");
        }
        List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsByDocNumAndWarehouseId(docNum,warehouseId);
        jsonObject.put("doc", arrivalVerification);
        jsonObject.put("details", arrivalVerificationDetailsList);
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
            ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationById(id);
            if (ObjectUtil.isNotEmpty(arrivalVerification)) {
                List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsByDocNumAndWarehouseId(arrivalVerification.getVerificationDocumentNumber(),arrivalVerification.getWarehouseId());
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
                jsonResult.put("tableName", "arrival_verification");
                jsonResult.put("main", entityUtils.jsonField("arrivalVerification", new ArrivalVerification()));
                jsonResult.put("details", entityUtils.jsonField("arrivalVerification", new ArrivalVerificationDetails()));
                jsonResult.put("mainValue", arrivalVerification);
                jsonResult.put("detailsValue", arrivalVerificationDetailsList);
                jsonResult.put("mainUpdate", "/wms/api/arrival-verification/update");
                jsonResult.put("detailsUpdate", "/wms/api/arrival-verification-details/update");
                jsonResult.put("missionCompleted", "/wms/api/arrival-verification/missionCompleted");
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
            ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotEmpty(arrivalVerification)) {
                UpdateArrivalVerificationDTO updateArrivalVerificationDTO = new UpdateArrivalVerificationDTO();
                updateArrivalVerificationDTO.setId(id);
                updateArrivalVerificationDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateArrivalVerificationDTO.setPlanStatus(2);
                Result result = arrivalVerificationService.updateArrivalVerification(updateArrivalVerificationDTO);
                if (result.isOk()) {
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("到货检验单异常,无法进入流程引擎");
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
            ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationByProcessInstanceId(processInstanceId);
            if (ObjectUtil.isNotEmpty(arrivalVerification)) {
                UpdateArrivalVerificationDTO updateArrivalVerificationDTO = new UpdateArrivalVerificationDTO();
                updateArrivalVerificationDTO.setId(arrivalVerification.getId());
                /**
                 *  单据状态由审批中改为审批生效
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateArrivalVerificationDTO.setPlanStatus(3);
                return arrivalVerificationService.updateArrivalVerification(updateArrivalVerificationDTO);
            } else {
                return Result.failure("单据异常无法完成");
            }
        } catch (Exception e) {
            log.error("完成审批接口异常", e);
            return Result.failure("系统异常");
        }
    }

}

