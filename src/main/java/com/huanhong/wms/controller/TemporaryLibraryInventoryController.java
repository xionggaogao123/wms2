package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.TemporaryLibraryInventoryVO;
import com.huanhong.wms.service.IMaterialService;
import com.huanhong.wms.service.ITemporaryLibraryInventoryDetailsService;
import com.huanhong.wms.service.ITemporaryLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.TemporaryLibraryInventoryMapper;
import com.huanhong.wms.service.ITemporaryLibraryInventoryService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "临库清点单")
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/temporary-library-inventory")
public class TemporaryLibraryInventoryController extends BaseController {

    @Resource
    private ITemporaryLibraryInventoryService temporaryLibraryInventoryService;

    @Resource
    private ITemporaryLibraryInventoryDetailsService temporaryLibraryInventoryDetailsService;

    @Resource
    private IMaterialService materialService;

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryLibraryInventory>> page(@RequestParam(defaultValue = "1") Integer current,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        TemporaryLibraryInventoryVO temporaryLibraryInventoryVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryLibraryInventory> pageResult = temporaryLibraryInventoryService.pageFuzzyQuery(new Page<>(current, size), temporaryLibraryInventoryVO);
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
    public Result add(@Valid @RequestBody AddTemporaryLibraryInventoryAndDetailsDTO addTemporaryLibraryInventoryAndDetailsDTO) {
        log.info("接收添加的参数为:{}", JsonUtil.obj2String(addTemporaryLibraryInventoryAndDetailsDTO));
        try {
            AddTemporaryLibraryInventoryDTO addTemporaryLibraryInventoryDTO = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDTO();
            List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList();
            Result result = temporaryLibraryInventoryService.addTemporaryLibraryInventory(addTemporaryLibraryInventoryDTO);
            if (!result.isOk()) {
                return Result.failure("新增点验单失败！");
            }
            TemporaryLibraryInventory temporaryLibraryInventory = (TemporaryLibraryInventory) result.getData();
            String docNum = temporaryLibraryInventory.getDocumentNumber();
            for (AddTemporaryLibraryInventoryDetailsDTO addTemporaryLibraryInventoryDetailsDTO : addTemporaryLibraryInventoryDetailsDTOList) {
                addTemporaryLibraryInventoryDetailsDTO.setDocumentNumber(docNum);
            }
            return temporaryLibraryInventoryDetailsService.addInventoryDocumentDetailsList(addTemporaryLibraryInventoryDetailsDTOList);
        } catch (Exception e) {
            log.error("新增点验单失败");
            return Result.failure("系统异常，新增点验单失败！");
        }
    }

//        @ApiOperationSupport(order = 3)
//        @ApiOperation(value = "更新", notes = "生成代码")
//        @PutMapping("/update")
//        public Result update(@Valid @RequestBody UpdateTemporaryLibraryInventoryAndDetailsDTO updateTemporaryLibraryInventoryAndDetailsDTO) {
//
//            try {
//
//                LoginUser loginUser = this.getLoginUser();
//
//                UpdateTemporaryLibraryInventoryDTO updateTemporaryLibraryInventoryDTO = updateTemporaryLibraryInventoryAndDetailsDTO.getUpdateTemporaryLibraryInventoryDTO();
//                List<UpdateTemporaryLibraryInventoryDetailsDTO> updateTemporaryLibraryInventoryDetailsDTOList = updateTemporaryLibraryInventoryAndDetailsDTO.getUpdateTemporaryLibraryInventoryDetailsDTOList();
//                /**
//                 * 先判断主表是否已经点验完成 若已完成则不能更新
//                 */
//                TemporaryLibraryInventory temporaryLibraryInventory = temporaryLibraryInventoryService.getTemporaryLibraryInventoryById(updateTemporaryLibraryInventoryDTO.getId());
//                if (temporaryLibraryInventory.getComplete() == 1) {
//                    return Result.failure("单据已完成,不能更新");
//                }
//                String docNum = temporaryLibraryInventory.getDocumentNumber();
//                String warehouseId = temporaryLibraryInventory.getWarehouseId();
//                /**
//                 * 如果此次更新主表要更新为已完成，判断其明细是否都已完成，若没有则主表不能更新为已完成
//                 */
//                if (null != updateTemporaryLibraryInventoryDTO.getComplete() && updateTemporaryLibraryInventoryDTO.getComplete() == 1) {
//                    //查询已经点验完成的明细以及本次更新的明细总数是否等于明细
//                    int updateNum = 0;
//                    for (UpdateTemporaryLibraryInventoryDetailsDTO updateTemporaryLibraryInventoryDetailsDTO : updateTemporaryLibraryInventoryDetailsDTOList
//                    ) {
//                        if (updateTemporaryLibraryInventoryDetailsDTO.getComplete() == 1) {
//                            updateNum++;
//                        }
//                    }
//                    int completeNum = temporaryLibraryInventoryDetailsService.getCompleteNum(docNum, warehouseId, 1);
//                    completeNum += updateNum;
//                    int allNum = temporaryLibraryInventoryDetailsService.getTemporaryLibraryInventoryDetailsListByDocNumberAndWarehosueId(docNum, warehouseId).size();
//                    if (allNum != completeNum) {
//                        return Result.failure("有明细未完成点验,主表无法完成！");
//                    }
//                }
//
//                Result result = temporaryLibraryInventoryService.updateTemporaryLibraryInventory(updateTemporaryLibraryInventoryDTO);
//                if (!result.isOk()) {
//                    return Result.failure("更新点验单失败！");
//                }
//                Result resultDetails = temporaryLibraryInventoryDetailsService.updateTemporaryLibraryInventoryDetailsList(updateTemporaryLibraryInventoryDetailsDTOList);
//
//                HashMap map = (HashMap) resultDetails.getData();
//
//                List<UpdateTemporaryLibraryInventoryDetailsDTO> UpdateTemporaryLibraryInventoryDetailsDTOListFalse = (List<UpdateTemporaryLibraryInventoryDetailsDTO>) map.get("false");
//
//                if (UpdateTemporaryLibraryInventoryDetailsDTOListFalse.size() != 0) {
//                    return Result.success(UpdateTemporaryLibraryInventoryDetailsDTOListFalse, "有明细更新失败！请重试！");
//                }
//                //点验单及明细完成后变动库存
//                List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetailsList = temporaryLibraryInventoryDetailsService.getTemporaryLibraryInventoryDetailsListByDocNumberAndWarehosueId(docNum, warehouseId);
//
//                AddTemporaryLibraryDTO addTemporaryLibraryDTO = new AddTemporaryLibraryDTO();
//
//                int count = 0;
//
//                List<TemporaryLibraryInventoryDetails> listfalse = new ArrayList<>();
//
//                for (TemporaryLibraryInventoryDetails temporaryLibraryInventoryDetails : temporaryLibraryInventoryDetailsList
//                ) {
//                    addTemporaryLibraryDTO.setMaterialCoding(temporaryLibraryInventoryDetails.getMaterialCoding());
//                    Material material = materialService.getMeterialByMeterialCode(temporaryLibraryInventoryDetails.getMaterialCoding());
//                    addTemporaryLibraryDTO.setMaterialName(material.getMaterialName());
//                    addTemporaryLibraryDTO.setMeasurementUnit(material.getMeasurementUnit());
//                    if (ObjectUtil.isNotNull(material.getAuxiliaryUnit())) {
//                        addTemporaryLibraryDTO.setAuxiliaryUnit(material.getAuxiliaryUnit());
//                    }
//                    addTemporaryLibraryDTO.setCargoSpaceId(temporaryLibraryInventoryDetails.getWarehouseId() + "01AA9999");
//                    addTemporaryLibraryDTO.setInventoryCredit(temporaryLibraryInventoryDetails.getArrivalQuantity());
//                    addTemporaryLibraryDTO.setBatch(temporaryLibraryInventoryDetails.getBatch());
//                    addTemporaryLibraryDTO.setWarehouseId(warehouseId);
//                    addTemporaryLibraryDTO.setInventoryCredit(temporaryLibraryInventoryDetails.getArrivalQuantity());
//                    addTemporaryLibraryDTO.setRemark("系统自动生成");
//
//                    Result resultAddIventory = temporaryLibraryService.addTemporaryLibrary(addTemporaryLibraryDTO);
//
//                    if (resultAddIventory.isOk()) {
//                        //库存新增成功后，新增入库记录
//                        AddTemporaryRecordDTO addTemporaryRecordDTO = new AddTemporaryRecordDTO();
//                        addTemporaryRecordDTO.setDocumentNumber(docNum);
//                        addTemporaryRecordDTO.setRecordType(1);
//                        addTemporaryRecordDTO.setWarehouseId(warehouseId);
//                        addTemporaryRecordDTO.setMaterialCoding(material.getMaterialCoding());
//                        addTemporaryRecordDTO.setMaterialName(material.getMaterialName());
//                        addTemporaryRecordDTO.setBatch(addTemporaryLibraryDTO.getBatch());
//                        addTemporaryRecordDTO.setCargoSpaceId(addTemporaryLibraryDTO.getCargoSpaceId());
//                        addTemporaryRecordDTO.setEnterQuantity(addTemporaryLibraryDTO.getInventoryCredit());
//                        addTemporaryRecordDTO.setWarehouseManager(loginUser.getLoginName());
//                        count++;
//                    } else {
//                        listfalse.add(temporaryLibraryInventoryDetails);
//                    }
//                }
//                return count == temporaryLibraryInventoryDetailsList.size() ? Result.success("库存新增成功！") : Result.success(listfalse, "若干点验单新增库存失败！");
//                } catch(Exception e){
//                    log.error("更新点验单失败", e);
//                    return Result.failure("系统异常：更新点验单失败!");
//                }
//        }


    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("deleteById/{id}")
    public Result delete(@PathVariable Integer id) {

        TemporaryLibraryInventory temporaryLibraryInventory = temporaryLibraryInventoryService.getTemporaryLibraryInventoryById(id);

        if (ObjectUtil.isNull(temporaryLibraryInventory)) {
            return Result.failure("单据不存在！");
        }

        boolean delete = temporaryLibraryInventoryService.removeById(id);

        //主表删除成功,删除明细
        if (delete) {
            String docNum = temporaryLibraryInventory.getDocumentNumber();

            List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetailsList = temporaryLibraryInventoryDetailsService.getTemporaryLibraryInventoryDetailsListByMaterialCodeAndWarehouseId(temporaryLibraryInventory.getDocumentNumber(), temporaryLibraryInventory.getWarehouseId());

            for (TemporaryLibraryInventoryDetails temporaryLibraryInventoryDetails : temporaryLibraryInventoryDetailsList
            ) {
                temporaryLibraryInventoryDetailsService.removeById(temporaryLibraryInventoryDetails.getId());
            }
        }
        return Result.success("删除成功");
    }

}

