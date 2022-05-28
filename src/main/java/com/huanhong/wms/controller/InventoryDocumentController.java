package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.InventoryDocumentVO;
import com.huanhong.wms.mapper.InventoryDocumentDetailsMapper;
import com.huanhong.wms.mapper.InventoryDocumentMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/v1/inventory-document")
@ApiSort()
@Validated
@Api(tags = "清点单管理")
public class InventoryDocumentController extends BaseController {

    @Resource
    private IInventoryDocumentService inventoryDocumentService;



    @Resource
    private InventoryDocumentService service;
    @Resource
    private IMaterialService materialService;

    @Resource
    private IInventoryDocumentDetailsService inventoryDocumentDetailsService;



    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IWarehousingRecordService warehousingRecordService;

    @Resource
    private IOutboundRecordService outboundRecordService;

    @Resource
    private IAllocationOutService allocationOutService;
    @Resource
    private IOnShelfService onShelfService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<InventoryDocument>> page(@RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                InventoryDocumentVO inventoryDocumentVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<InventoryDocument> pageResult = inventoryDocumentService.pageFuzzyQuery(new Page<>(current, size), inventoryDocumentVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到清点单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddInventoryDocumentAndDetailsDTO addInventoryDocumentAndDetailsDTO) {
        try {
            AddInventoryDocumentDTO addInventoryDocumentDTO = addInventoryDocumentAndDetailsDTO.getAddInventoryDocumentDTO();
            List<AddInventoryDocumentDetailsDTO> addInventoryDocumentDetailsDTOList = addInventoryDocumentAndDetailsDTO.getAddInventoryDocumentDetailsDTOList();
            Result result = inventoryDocumentService.addInventoryDocument(addInventoryDocumentDTO);
            if (!result.isOk()) {
                return Result.failure("新增点验单失败！");
            }
            InventoryDocument inventoryDocument = (InventoryDocument) result.getData();
            String docNum = inventoryDocument.getDocumentNumber();
            for (AddInventoryDocumentDetailsDTO addInventoryDocumentDetailsDTO : addInventoryDocumentDetailsDTOList) {
                addInventoryDocumentDetailsDTO.setDocumentNumber(docNum);
            }
            return inventoryDocumentDetailsService.addInventoryDocumentDetailsLis(addInventoryDocumentDetailsDTOList);
        } catch (Exception e) {
            log.error("新增点验单失败");
            return Result.failure("系统异常，新增点验单失败！");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateInventoryDocumentAndDetailsDTO updateInventoryDocumentAndDetailsDTO) {
        try {
            UpdateInventoryDocumentDTO updateInventoryDocumentDTO = updateInventoryDocumentAndDetailsDTO.getUpdateInventoryDocumentDTO();
            List<UpdateInventoryDocumentDetailsDTO> updateInventoryDocumentDetailsDTOList = updateInventoryDocumentAndDetailsDTO.getUpdateInventoryDocumentDetailsDTOList();
            /**
             * 先判断主表是否已经点验完成 若已完成则不能更新
             */
            InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentById(updateInventoryDocumentDTO.getId());
            if (inventoryDocument.getComplete() == 1) {
                return Result.failure("单据已完成,不能更新");
            }
            String docNum = inventoryDocument.getDocumentNumber();
            String warehouseId = inventoryDocument.getWarehouse();
            /**
             * 如果此次更新主表要更新为已完成，判断其明细是否都已完成，若没有则主表不能更新为已完成
             */
            if (null != updateInventoryDocumentDTO.getComplete() && updateInventoryDocumentDTO.getComplete() == 1) {
                //查询已经点验完成的明细以及本次更新的明细总数是否等于明细
                int updateNum = 0;
                for (UpdateInventoryDocumentDetailsDTO updateInventoryDocumentDetailsDTO : updateInventoryDocumentDetailsDTOList
                ) {
                    if (updateInventoryDocumentDetailsDTO.getComplete() == 1) {
                        updateNum++;
                    }
                }
                int completeNum = inventoryDocumentDetailsService.getCompleteNum(docNum, warehouseId, 1);
                completeNum += updateNum;
                int allNum = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(docNum, warehouseId).size();
                if (allNum > completeNum) {
                    return Result.failure("有明细未完成点验,主表无法完成！");
                }
            }

            //判断询价单信息中填的是否是调拨出库单
            if(StrUtil.isBlank(inventoryDocument.getRfqNumber())||inventoryDocument.getRfqNumber().length()<5){
                return Result.failure("询价单编号不合法");
            }
            String headKey = inventoryDocument.getRfqNumber().substring(0, 4);
            if ("DBCK".equals(headKey)){
                //查询出库单信息
                AllocationOut allocationOut = allocationOutService.getAllocationOutByDocNumber(inventoryDocument.getRfqNumber());
                if (ObjectUtil.isEmpty(allocationOut)){
                    return Result.failure("未找到调拨出库单信息！");
                }
            }
            Result result = inventoryDocumentService.updateInventoryDocument(updateInventoryDocumentDTO);
            if (!result.isOk()) {
                return Result.failure("更新点验单失败！");
            }
            Result resultDetails = inventoryDocumentDetailsService.updateInventoryDocumentDetailsList(updateInventoryDocumentDetailsDTOList);
            HashMap map = (HashMap) resultDetails.getData();
            List<UpdateInventoryDocumentDetailsDTO> updateInventoryDocumentDetailsDTOListFalse = (List<UpdateInventoryDocumentDetailsDTO>) map.get("false");
            if (updateInventoryDocumentDetailsDTOListFalse.size() != 0) {
                return Result.success(updateInventoryDocumentDetailsDTOListFalse, "有明细更新失败！请重试！");
            }
            //点验单及明细完成后变动库存
            List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(docNum, warehouseId);
            AddInventoryInformationDTO addInventoryInformationDTO = new AddInventoryInformationDTO();
            int count = 0;
            List<InventoryDocument> listfalse = new ArrayList<>();


            if ("DBCK".equals(headKey)){
                for (InventoryDocumentDetails inventoryDocumentDetails : inventoryDocumentDetailsList) {
                    //查询出库单信息

                    //根据出库记录新增库存
                    List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(inventoryDocumentDetails.getMaterialCoding(), inventoryDocumentDetails.getBatch(), inventoryDocument.getWarehouse());
                    addInventoryInformationDTO.setMaterialCoding(inventoryDocumentDetails.getMaterialCoding());
                    Material material = materialService.getMeterialByMeterialCode(inventoryDocumentDetails.getMaterialCoding());

                    BeanUtil.copyProperties(inventoryInformationList.get(0), addInventoryInformationDTO);
                    addInventoryInformationDTO.setCargoSpaceId(inventoryDocumentDetails.getWarehouse() + "01AA0000");
                    addInventoryInformationDTO.setWarehouseId(warehouseId);
                    addInventoryInformationDTO.setInventoryCredit(inventoryDocumentDetails.getArrivalQuantity());

                    Result resultAddIventory = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                    if (resultAddIventory.isOk()) {
                        //库存新增成功后，新增入库记录
                        AddWarehousingRecordDTO addWarehousingRecordDTO = new AddWarehousingRecordDTO();
                        addWarehousingRecordDTO.setDocumentNumber(inventoryDocument.getDocumentNumber());
                        addWarehousingRecordDTO.setBatch(addInventoryInformationDTO.getBatch());
                        addWarehousingRecordDTO.setMaterialCoding(addInventoryInformationDTO.getMaterialCoding());
                        addWarehousingRecordDTO.setCargoSpaceId(addInventoryInformationDTO.getCargoSpaceId());
                        addWarehousingRecordDTO.setWarehouseId(inventoryDocument.getWarehouse());
                        addWarehousingRecordDTO.setEnterType(2);
                        addWarehousingRecordDTO.setEnterQuantity(addInventoryInformationDTO.getInventoryCredit());
                        warehousingRecordService.addWarehousingRecord(addWarehousingRecordDTO);
                        count++;
                    } else {
                        listfalse.add(inventoryDocument);
                    }
                }
            }else {
                for (InventoryDocumentDetails inventoryDocumentDetails : inventoryDocumentDetailsList
                ) {
                    addInventoryInformationDTO.setMaterialCoding(inventoryDocumentDetails.getMaterialCoding());
                    Material material = materialService.getMeterialByMeterialCode(inventoryDocumentDetails.getMaterialCoding());
                    addInventoryInformationDTO.setMaterialName(material.getMaterialName());
                    addInventoryInformationDTO.setMeasurementUnit(material.getMeasurementUnit());
                    if (ObjectUtil.isNotNull(material.getAuxiliaryUnit())) {
                        addInventoryInformationDTO.setAuxiliaryUnit(material.getAuxiliaryUnit());
                    }
                    addInventoryInformationDTO.setCargoSpaceId(inventoryDocumentDetails.getWarehouse() + "01AA0000");
                    addInventoryInformationDTO.setInventoryCredit(inventoryDocumentDetails.getArrivalQuantity());
                    addInventoryInformationDTO.setSafeQuantity((double) 0);
                    addInventoryInformationDTO.setBatch(inventoryDocumentDetails.getBatch());
                    addInventoryInformationDTO.setConsignor(0);
                    addInventoryInformationDTO.setWarehouseId(inventoryDocumentDetails.getWarehouse());
                    //现将内部单价定为0 等后面改
                    addInventoryInformationDTO.setUnitPrice(BigDecimal.valueOf(0));
                    addInventoryInformationDTO.setManagementFeeRate(1.1);
                    //现将出卖单价定为0 等后面改
                    addInventoryInformationDTO.setSalesUnitPrice(BigDecimal.valueOf(0));
                    addInventoryInformationDTO.setSupplier("待补");
                    Result resultAddIventory = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                    if (resultAddIventory.isOk()) {
                        //库存新增成功后，新增入库记录
                        AddWarehousingRecordDTO addWarehousingRecordDTO = new AddWarehousingRecordDTO();
                        addWarehousingRecordDTO.setDocumentNumber(inventoryDocument.getDocumentNumber());
                        addWarehousingRecordDTO.setBatch(addInventoryInformationDTO.getBatch());
                        addWarehousingRecordDTO.setMaterialCoding(addInventoryInformationDTO.getMaterialCoding());
                        addWarehousingRecordDTO.setCargoSpaceId(addInventoryInformationDTO.getCargoSpaceId());
                        addWarehousingRecordDTO.setWarehouseId(inventoryDocument.getWarehouse());
                        addWarehousingRecordDTO.setEnterType(1);
                        addWarehousingRecordDTO.setEnterQuantity(addInventoryInformationDTO.getInventoryCredit());
                        warehousingRecordService.addWarehousingRecord(addWarehousingRecordDTO);
                        count++;
                    } else {
                        listfalse.add(inventoryDocument);
                    }
                }
            }
            if (null != updateInventoryDocumentDTO.getComplete() && updateInventoryDocumentDTO.getComplete() == 1) {
                // 清单完成生成上架单
                inventoryDocumentDetailsList.forEach(idd->{
                    AddOnShelfDTO addOnShelfDTO = new AddOnShelfDTO();
                    addOnShelfDTO.setInventoryNo(idd.getDocumentNumber());
                    addOnShelfDTO.setInventoryCredit(idd.getArrivalQuantity());
                    addOnShelfDTO.setBatch(idd.getBatch());
                    addOnShelfDTO.setMaterialCoding(idd.getMaterialCoding());
//                    addOnShelfDTO.setCargoSpaceId();
                    addOnShelfDTO.setWaitingQuantity(idd.getArrivalQuantity());
                    addOnShelfDTO.setWarehouse(idd.getWarehouse());
//                    addOnShelfDTO.setWarehouseAreaId();
                    onShelfService.addOnShelf(addOnShelfDTO);
                });
            }
            return count == inventoryDocumentDetailsList.size() ? Result.success("库存新增成功！") : Result.success(listfalse, "若干点验单新增库存失败！");
        } catch (Exception e) {
            log.error("更新点验单失败",e);
            return Result.failure("系统异常：更新点验单失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {

        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentById(id);

        if (ObjectUtil.isNull(inventoryDocument)){
            return Result.failure("单据不存在！");
        }
        boolean delete = inventoryDocumentService.removeById(id);

        //主表删除成功,删除明细
        if (delete){
            String docNum = inventoryDocument.getDocumentNumber();

            List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(inventoryDocument.getDocumentNumber(),inventoryDocument.getWarehouse());

            for (InventoryDocumentDetails inventoryDocumentDetails:inventoryDocumentDetailsList
            ) {
                inventoryDocumentDetailsService.removeById(inventoryDocumentDetails.getId());
            }
        }
        return Result.success("删除成功");
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "通过ID查询清点单及明细")
    @GetMapping("getInventoryDocumentAndDetailsById/{id}")
    public Result getInventoryDocumentById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentById(id);
        if (ObjectUtil.isEmpty(inventoryDocument)) {
            return Result.failure("未找到对应信息！");
        }
        List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(inventoryDocument.getDocumentNumber(), inventoryDocument.getWarehouse());
        jsonObject.put("doc", inventoryDocument);
        jsonObject.put("details", inventoryDocumentDetailsList);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "通过单据号和仓库id查询清点单及明细")
    @GetMapping("getInventoryDocumentAndDetailsByDocNumAndWarhouseId/{docNum}&{warehouseId}")
    public Result getInventoryDocumentByDocNumAndWarhouseId(@PathVariable String docNum, @PathVariable String warehouseId) {
        JSONObject jsonObject = new JSONObject();
        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentByDocumentNumberAndWarehouseId(docNum, warehouseId);
        if (ObjectUtil.isEmpty(inventoryDocument)) {
            return Result.failure("未找到对应信息！");
        }
        List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(inventoryDocument.getDocumentNumber(), inventoryDocument.getWarehouse());
        jsonObject.put("doc", inventoryDocument);
        jsonObject.put("details", inventoryDocumentDetailsList);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "PDA-通过单据号和仓库id查询清点单及明细")
    @GetMapping("/getInventoryDocumentForPda")
    public Result getInventoryDocumentForPda(@RequestParam String docNum,
                                             @RequestParam String warehouseId
    ) {

        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentByDocumentNumberAndWarehouseId(docNum, warehouseId);

        if (ObjectUtil.isEmpty(inventoryDocument)) {
            return Result.success("未查到相关数据！");
        }
        JSONArray jsonArray = new JSONArray();

        List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(inventoryDocument.getDocumentNumber(), inventoryDocument.getWarehouse());

        for (InventoryDocumentDetails inventoryDocumentDetails : inventoryDocumentDetailsList
        ) {
            JSONObject jsonObjectMaterial = new JSONObject();
            Material material = materialService.getMeterialByMeterialCode(inventoryDocumentDetails.getMaterialCoding());
            jsonObjectMaterial.put("material", material);
            jsonObjectMaterial.put("details", inventoryDocumentDetails);
            jsonArray.add(jsonObjectMaterial);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doc", inventoryDocument);
        jsonObject.put("detailsList", jsonArray);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "PDA-通过id查询清点单及明细")
    @GetMapping("/getInventoryDocumentByIdForPda")
    public Result getInventoryDocumentByIdForPda(@RequestParam Integer id
    ) {
        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentById(id);

        if (ObjectUtil.isEmpty(inventoryDocument)) {
            return Result.success("未查到相关数据！");
        }

        List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByDocNumberAndWarehosue(inventoryDocument.getDocumentNumber(), inventoryDocument.getWarehouse());

        JSONArray jsonArray = new JSONArray();

        for (InventoryDocumentDetails inventoryDocumentDetails : inventoryDocumentDetailsList
        ) {
            JSONObject jsonObjectMaterial = new JSONObject();
            Material material = materialService.getMeterialByMeterialCode(inventoryDocumentDetails.getMaterialCoding());
            jsonObjectMaterial.put("material", material);
            jsonObjectMaterial.put("details", inventoryDocumentDetails);
            jsonArray.add(jsonObjectMaterial);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doc", inventoryDocument);
        jsonObject.put("detailsList", jsonArray);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "查询")
    @GetMapping("selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        try {
           return service.selectById(id);
        } catch (Exception e) {
            return Result.failure("删除失败");
        }
    }
}

