package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.ArrivalVerificationVO;
import com.huanhong.wms.mapper.ArrivalVerificationMapper;
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

    @Resource
    private IUserService userService;

    @Resource
    private IInventoryDocumentService inventoryDocumentService;

    @Resource
    private IInventoryDocumentDetailsService inventoryDocumentDetailsService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

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
            String warehoueId = arrivalVerification.getWarehouseId();
            if (ObjectUtil.isNotNull(addArrivalVerificationDetailsDTOList)){
                for (AddArrivalVerificationDetailsDTO addArrivalVerificationDetailsDTO : addArrivalVerificationDetailsDTOList) {
                    addArrivalVerificationDetailsDTO.setDocumentNumber(docNum);
                    addArrivalVerificationDetailsDTO.setWarehouseId(warehoueId);
                    Material material = materialService.getById(addArrivalVerificationDetailsDTO.getMaterialId());
                    if(null == material){
                        continue;
                    }
                    addArrivalVerificationDetailsDTO.setMaterialId(addArrivalVerificationDetailsDTO.getMaterialId());
                    addArrivalVerificationDetailsDTO.setMaterialName(material.getMaterialName());
                    addArrivalVerificationDetailsDTO.setMaterialCoding(material.getMaterialCoding());
                }
                arrivalVerificationDetailsService.addArrivalVerificationDetails(addArrivalVerificationDetailsDTOList);
            }
            return result;
        } catch (Exception e) {
            log.error("到货检验计划失败",e);
            return Result.failure("系统异常，到货检验计划失败！");
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
            ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationById(updateArrivalVerificationDTO.getId());

            Result resultDetails = arrivalVerificationDetailsService.updateArrivalVerificationDetails(updateArrivalVerificationDetailsDTOList);
            /**
             * 如果主表更新为全部检验，根据检验明细list更新库存中的是否检验字段
             */
            UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
            if (arrivalVerification.getVerificationStatus()==2){
                for (UpdateArrivalVerificationDetailsDTO updateArrivalVerificationDetailsDTO : updateArrivalVerificationDetailsDTOList) {
                    String materialCoding = updateArrivalVerificationDetailsDTO.getMaterialCoding();
                    String batch = updateArrivalVerificationDetailsDTO.getBatch();
                    String warehouseId = updateArrivalVerificationDTO.getWarehouseId();
                    List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(materialCoding,batch,warehouseId);
                    if (ObjectUtil.isNotNull(inventoryInformationList)){
                        for (InventoryInformation inventoryInformation:inventoryInformationList) {
                            //更新库存信息为已检验
                            BeanUtil.copyProperties(inventoryInformation,updateInventoryInformationDTO);
                            updateInventoryInformationDTO.setIsVerification(1);
                            inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                        }
                    }
                }
            }
            return resultDetails;
        } catch (Exception e) {
            log.error("更新到货检验失败");
            return Result.failure("系统异常：更新到货检验失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除到货检验主表", notes = "生成代码")
    @DeleteMapping("delte/{id}")
    public Result delete(@PathVariable Integer id) {

        ArrivalVerification arrivalVerification= arrivalVerificationService.getArrivalVerificationById(id);

        if (ObjectUtil.isNull(arrivalVerification)){
            return Result.failure("单据不存在！");
        }
        boolean delete = arrivalVerificationService.removeById(id);

        //主表删除成功,删除明细
        if (delete){
            String docNum = arrivalVerification.getVerificationDocumentNumber();
            List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsByDocNumAndWarehouseId(arrivalVerification.getVerificationDocumentNumber(), arrivalVerification.getWarehouseId());
            for (ArrivalVerificationDetails arrivalVerificationDetails:arrivalVerificationDetailsList) {
                arrivalVerificationDetailsService.removeById(arrivalVerificationDetails.getId());
            }
            // 恢复清点单可导入
            String originalDocumentNumber = arrivalVerification.getOriginalDocumentNumber();
            String[] originalDocumentNumbers = JSON.parseArray(originalDocumentNumber).toArray(new String[]{});
            inventoryDocumentService.updateIsImportedByDocumentNumbers(0,"",originalDocumentNumbers);
        }
        return Result.success("删除成功");
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
        if(StrUtil.isNotBlank(arrivalVerification.getCheckerIds())){
            List<User> checkUsers  = userService.list(Wrappers.<User>lambdaQuery().select(User::getId,User::getUserName)
                    .in(User::getId, arrivalVerification.getCheckerIds().split(",")));
            arrivalVerification.setCheckerUsers(checkUsers);
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
        if(StrUtil.isNotBlank(arrivalVerification.getCheckerIds())){
            List<User> checkUsers  = userService.list(Wrappers.<User>lambdaQuery().select(User::getId,User::getUserName)
                    .in(User::getId, arrivalVerification.getCheckerIds().split(",")));
            arrivalVerification.setCheckerUsers(checkUsers);
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
    @GetMapping("/getArrivalVerificationByDocNumAndWarehouseId")
    public Result getArrivalVerificationByDocNumAndWarehouseId(
            @RequestParam String docNum, @RequestParam String warehouseId
    ) {
        JSONObject jsonObject = new JSONObject();
        ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationByDocNumberAndWarhouseId(docNum,warehouseId);
        if (ObjectUtil.isEmpty(arrivalVerification)) {
            return Result.failure("未找到对应信息！");
        }
        if(StrUtil.isNotBlank(arrivalVerification.getCheckerIds())){
            List<User> checkUsers  = userService.list(Wrappers.<User>lambdaQuery().select(User::getId,User::getUserName)
                    .in(User::getId, arrivalVerification.getCheckerIds().split(",")));
            arrivalVerification.setCheckerUsers(checkUsers);
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
                if(StrUtil.isNotBlank(arrivalVerification.getCheckerIds())){
                    List<User> checkUsers  = userService.list(Wrappers.<User>lambdaQuery().select(User::getId,User::getUserName)
                            .in(User::getId, arrivalVerification.getCheckerIds().split(",")));
                    arrivalVerification.setCheckerUsers(checkUsers);
                }
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
                jsonResult.put("mainKey","updateArrivalVerificationDTO");
                jsonResult.put("detailKey","updateArrivalVerificationDetailsDTOList");
                jsonResult.put("mainUpdate", "/wms/api/v1/arrival-verification/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/arrival-verification-details/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/arrival-verification/missionCompleted");
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


    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "合并清点单成到货检验单")
    @PutMapping("/consolidatedArrivalVerification")
    public Result consolidatedArrivalVerification(@Valid @RequestBody List<InventoryDocument> inventoryDocumentList){
        try {
            /**
             * 筛查接收的清点单list中是否有不属于同一询价单的
             */
//            String rfqNum = inventoryDocumentList.get(0).getRfqNumber();
//            for (InventoryDocument inventoryDocument:inventoryDocumentList) {
//                if (!inventoryDocument.getRfqNumber().equals(rfqNum)){
//                    return Result.failure("询价单编号不一致,不能合并！");
//                }
//            }
            LoginUser loginUser = this.getLoginUser();
            User user = userService.getById(loginUser.getId());
            List<String> listOriginalDocumentNumber =  new ArrayList<>();
            AddArrivalVerificationDTO addArrivalVerificationDTO = new AddArrivalVerificationDTO();
            List<AddArrivalVerificationDetailsDTO> addArrivalVerificationDetailsDTOList = new ArrayList<>();
            //获取清点单
            int i = 0;
            for (InventoryDocument inventoryDocument:inventoryDocumentList) {
                //将清点单编号放入检验单
                listOriginalDocumentNumber.add(inventoryDocument.getDocumentNumber());
                //获取到货检验明细表
                List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(inventoryDocument.getDocumentNumber(),inventoryDocument.getWarehouse());
                for (InventoryDocumentDetails inventoryDocumentDetails:inventoryDocumentDetailsList) {
                    int flag = 0;
                    String materialCoding = inventoryDocumentDetails.getMaterialCoding();
                    String batch = inventoryDocumentDetails.getBatch();
                    if (i==0){
                        //拼装到货检验明细表
                        AddArrivalVerificationDetailsDTO addArrivalVerificationDetailsDTOFirst = new AddArrivalVerificationDetailsDTO();
                        //仓库
                        addArrivalVerificationDetailsDTOFirst.setWarehouseId(inventoryDocumentDetails.getWarehouse());
                        //检验状态
                        addArrivalVerificationDetailsDTOFirst.setVerificationStatus(0);
                        //物料编码
                        addArrivalVerificationDetailsDTOFirst.setMaterialCoding(materialCoding);
                        //批次
                        addArrivalVerificationDetailsDTOFirst.setBatch(inventoryDocumentDetails.getBatch());
                        //到货数量
                        addArrivalVerificationDetailsDTOFirst.setArrivalQuantity(inventoryDocumentDetails.getArrivalQuantity());
                        //合格数量
//                        addArrivalVerificationDetailsDTOFirst.setQualifiedQuantity(inventoryDocumentDetails.getArrivalQuantity());
                        //备注
                        addArrivalVerificationDetailsDTOFirst.setRemark("系统自动生成");

                        addArrivalVerificationDetailsDTOList.add(addArrivalVerificationDetailsDTOFirst);

                    }else {
                        for (int j =0; j<addArrivalVerificationDetailsDTOList.size();j++) {
                            AddArrivalVerificationDetailsDTO addArrivalVerificationDetailsDTO = addArrivalVerificationDetailsDTOList.get(j);
                            //判断此物料此批次是否已经进入到货检验明细
                            if (addArrivalVerificationDetailsDTO.getMaterialCoding().equals(materialCoding)&&addArrivalVerificationDetailsDTO.getBatch().equals(batch)){
                                //若此物料已有到货检验明细
                                //变更到货数量
                                addArrivalVerificationDetailsDTO.setArrivalQuantity(NumberUtil.add(addArrivalVerificationDetailsDTO.getArrivalQuantity(),inventoryDocumentDetails.getArrivalQuantity()));
                                //变更合格数量=新到货数量
//                                addArrivalVerificationDetailsDTO.setQualifiedQuantity(addArrivalVerificationDetailsDTO.getArrivalQuantity());
                                //备注
                                addArrivalVerificationDetailsDTO.setRemark("系统自动生成");
                                //执行更新操作后代表当前需求计划明细已经并入采购计划明细中,flag++;
                                flag++;
                                //合并后跳出对比物料编码的循环
                                break;
                            }
                        }
                        //判断明细已经通过合并操作并入采购计划明细 0-无相同物料在采购计划明细中所以未经合并 1-已合并
                        if (flag==0){
                            //拼装到货检验明细表
                            AddArrivalVerificationDetailsDTO addArrivalVerificationDetailsDTONew = new AddArrivalVerificationDetailsDTO();
                            //仓库
                            addArrivalVerificationDetailsDTONew.setWarehouseId(inventoryDocumentDetails.getWarehouse());
                            //检验状态
                            addArrivalVerificationDetailsDTONew.setVerificationStatus(0);
                            //物料编码
                            addArrivalVerificationDetailsDTONew.setMaterialCoding(materialCoding);
                            //批次
                            addArrivalVerificationDetailsDTONew.setBatch(inventoryDocumentDetails.getBatch());
                            //到货数量
                            addArrivalVerificationDetailsDTONew.setArrivalQuantity(inventoryDocumentDetails.getArrivalQuantity());
                            //合格数量
//                            addArrivalVerificationDetailsDTONew.setQualifiedQuantity(inventoryDocumentDetails.getArrivalQuantity());
                            //备注
                            addArrivalVerificationDetailsDTONew.setRemark("系统自动生成");

                            addArrivalVerificationDetailsDTOList.add(addArrivalVerificationDetailsDTONew);
                        }
                    }
                }
                i++;
            }
            //装填到货检验主表
            //询价单编号
            addArrivalVerificationDTO.setRfqNumber(inventoryDocumentList.get(0).getRfqNumber());
            //原单据编号
            addArrivalVerificationDTO.setOriginalDocumentNumber(JSON.toJSONString(listOriginalDocumentNumber));
            //计划类别-1正常、2加急、3补计划、请选择（默认）
            addArrivalVerificationDTO.setPlanClassification(1);
            //状态: 1草拟 2审批中 3审批生效 4作废
            addArrivalVerificationDTO.setPlanStatus(1);
            //检验状态：0-未检验，1-部分检验，2-全部检验
            addArrivalVerificationDTO.setVerificationStatus(0);
            //到货日期
            addArrivalVerificationDTO.setDeliveryDate(inventoryDocumentList.get(0).getCreateTime());
            //理货人
            addArrivalVerificationDTO.setInspector(user.getId().toString());
            //仓库
            addArrivalVerificationDTO.setWarehouseId(inventoryDocumentList.get(0).getWarehouse());
            //备注
            addArrivalVerificationDTO.setRemark("系统自动生成");

            //将主表和明细装填入dto，调用新增方法（接口）
            AddArrivalVerificationAndDetailsDTO addArrivalVerificationAndDetailsDTO = new AddArrivalVerificationAndDetailsDTO();
            addArrivalVerificationAndDetailsDTO.setAddArrivalVerificationDTO(addArrivalVerificationDTO);
            addArrivalVerificationAndDetailsDTO.setAddArrivalVerificationDetailsDTOList(addArrivalVerificationDetailsDTOList);
            Result result = add(addArrivalVerificationAndDetailsDTO);
            if (result.isOk()){
                ArrivalVerification arrivalVerification = (ArrivalVerification) result.getData();
                String docNum = arrivalVerification.getVerificationDocumentNumber();
                String warehouseId = arrivalVerification.getWarehouseId();
                // 批量更新清点单已导入
                List<InventoryDocument> list = inventoryDocumentList.stream().map(r->{
                    InventoryDocument temp = new InventoryDocument();
                    temp.setIsImported(1);
                    temp.setId(r.getId());
                    temp.setDocumentNumberImported(docNum);
                    return temp;
                }).collect(Collectors.toList());
                inventoryDocumentService.saveOrUpdateBatch(list);
                List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsByDocNumAndWarehouseId(docNum,warehouseId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("main",arrivalVerification);
                jsonObject.put("details",arrivalVerificationDetailsList);
                return Result.success(jsonObject);
            }else {
                return Result.failure("合并新增到货检验单失败！");
            }
        }catch (Exception e){
            log.error("系统异常,新增到货检验单失败！",e);
            return Result.failure("系统异常,新增到货检验单失败！");
        }
    }



    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "PDA端分页查询")
    @GetMapping("/PDApage")
    public Result<Page<ArrivalVerification>> pagePda(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            ArrivalVerificationVO arrivalVerificationVO
    ){
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ArrivalVerification> pageResult = arrivalVerificationService.pageFuzzyQueryPDA(new Page<>(current,size),arrivalVerificationVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到到货检验单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }
}

