package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.MakeInventoryVO;
import com.huanhong.wms.mapper.MakeInventoryMapper;
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
import java.beans.beancontext.BeanContext;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1//make-inventory")
@ApiSort()
@Api(tags = "盘点单据管理")
public class MakeInventoryController extends BaseController {

    @Resource
    private IMakeInventoryService makeInventoryService;

    @Resource
    private IMakeInventoryDetailsService makeInventoryDetailsService;

    @Resource
    private MakeInventoryMapper makeInventoryMapper;

    @Resource
    private IMaterialService materialService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<MakeInventory>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            MakeInventoryVO makeInventoryVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<MakeInventory> pageResult = makeInventoryService.pageFuzzyQuery(new Page<>(current, size), makeInventoryVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到盘点单据信息");
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
    public Result add(@Valid @RequestBody AddMakeInventoryAndDetailsDTO addMakeInventoryAndDetailsDTO) {
        try {
            /**
             * 先新增主表
             */
            Result result = makeInventoryService.addMakeInventory(addMakeInventoryAndDetailsDTO.getAddMakeInventoryDTO());

            if (!result.isOk()) {
                return Result.failure("新增入库单失败");
            }

            MakeInventory makeInventory = (MakeInventory) result.getData();

            String docNum = makeInventory.getDocumentNumber();

            String warehouseId = makeInventory.getWarehouseId();

            /**
             * 判断是否为全盘
             */
            //非全盘的明细使用用户提交的明细
            if (makeInventory.getAllMake()==0){

                List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList = addMakeInventoryAndDetailsDTO.getAddMakeInventoryDetailsDTOList();

                if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                    for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                    ) {
                        addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                        addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                    }
                    makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                }
                return result;
            }

            //若为全盘,查询 0-暂存库存 1-正式库存  2-临时库存  作为全盘明细的依据
            List<InventoryInformation> inventoryInformationListZero = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryType(warehouseId,0);
            List<InventoryInformation> inventoryInformationListOne = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryType(warehouseId,1);
            List<TemporaryLibrary> temporaryLibraryList = temporaryLibraryService.getTemporaryLibraryListByWarehouseId(warehouseId);
            List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList = new ArrayList<>();

            for (InventoryInformation inventoryInformation : inventoryInformationListZero
                 ) {
                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                BeanUtil.copyProperties(inventoryInformation,addMakeInventoryAndDetailsDTO);
                BeanUtil.copyProperties(material,addMakeInventoryAndDetailsDTO);
                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0,6));
                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0,7));
                addMakeInventoryDetailsDTO.setInventoryType(0);
                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
            }

            for (InventoryInformation inventoryInformation : inventoryInformationListOne
            ) {
                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                BeanUtil.copyProperties(inventoryInformation,addMakeInventoryAndDetailsDTO);
                BeanUtil.copyProperties(material,addMakeInventoryAndDetailsDTO);
                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0,6));
                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0,7));
                addMakeInventoryDetailsDTO.setInventoryType(1);
                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
            }

            for (TemporaryLibrary temporaryLibrary : temporaryLibraryList
                 ) {
                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                Material material = materialService.getMeterialByMeterialCode(temporaryLibrary.getMaterialCoding());
                BeanUtil.copyProperties(temporaryLibrary,addMakeInventoryAndDetailsDTO);
                BeanUtil.copyProperties(material,addMakeInventoryAndDetailsDTO);
                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                addMakeInventoryDetailsDTO.setSublibraryId(temporaryLibrary.getCargoSpaceId().substring(0,6));
                addMakeInventoryDetailsDTO.setWarehouseAreaId(temporaryLibrary.getCargoSpaceId().substring(0,7));
                addMakeInventoryDetailsDTO.setInventoryType(2);
                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
            }
            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
            return result;
        } catch (Exception e) {
            log.error("添加入库单出错，异常", e);
            return Result.failure("系统异常：入库单添加失败。");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新盘点单", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateMakeInventoryAndDetailsDTO updateMakeInventoryAndDetailsDTO) {
        try {

            /**
             * 如果当前时间晚于盘点开始时间则不能更新
             */
            //如果想比较日期则写成"yyyy-MM-dd"就可以了
            //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //将字符串形式的时间转化为Date类型的时间
            Date planTime = DateUtil.parseDate(updateMakeInventoryAndDetailsDTO.getUpdateMakeInventoryDTO().getStartTime().toString());
            Date nowTime = DateUtil.parseDate(DateUtil.now());

            if(nowTime.getTime()-planTime.getTime()>0){
                return Result.failure("盘点已开始,无法更新单据！");
            }

            UpdateMakeInventoryDTO updateMakeInventoryDTO = updateMakeInventoryAndDetailsDTO.getUpdateMakeInventoryDTO();

            List<UpdateMakeInventoryDetailsDTO> updateMakeInventoryDetailsDTOList = updateMakeInventoryAndDetailsDTO.getUpdateMakeInventoryDetailsDTOList();

            Result result = makeInventoryService.updateMakeInventory(updateMakeInventoryDTO);

            if (!result.isOk()) {
                return Result.failure("更新盘点单失败！");
            }

            return makeInventoryDetailsService.updateMakeInventoryDetails(updateMakeInventoryDetailsDTOList);

        } catch (Exception e) {
            log.error("更新盘点单失败!");
            return Result.failure("系统异常：更新盘点单失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {

        MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);

        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }
        boolean delete = makeInventoryService.removeById(id);

        //主表删除成功,删除明细
        if (delete){
            String docNum = makeInventory.getDocumentNumber();

            List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

            for (MakeInventoryDetails makeInventoryDetails:makeInventoryDetailsList
            ) {
                makeInventoryDetailsService.removeById(makeInventoryDetails.getId());
            }
        }
        return Result.success("删除成功");
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取盘点单数据")
    @GetMapping("/getMakeInventoryById/{id}")
    public Result getMakeInvenrory(@PathVariable Integer id) {

        MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }

        JSONObject jsonObject = new JSONObject();
        List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

        jsonObject.put("doc",makeInventory);
        jsonObject.put("details",makeInventoryDetailsList);
        return Result.success(jsonObject);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号和子库编号获取盘点单及明细数据")
    @GetMapping("/getMakeInventoryByDocNumAndSublibraryId/{docNum}&{sublibraryId}")
    public Result getMakeInventoryByDocNumAndSublibraryId(@PathVariable String docNum,
                                                          @PathVariable String sublibraryId
    ) {
        String warehouseId = sublibraryId.substring(0, 4);

        MakeInventory makeInventory = makeInventoryService.getMakeInventoryByDocNumAndWarehouse(docNum, warehouseId);

        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }

        JSONObject jsonObject = new JSONObject();
        List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());
        jsonObject.put("doc",makeInventory);
        jsonObject.put("details",makeInventoryDetailsList);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据子库ID获取当前仓库的待盘点任务数")
    @GetMapping("/getCountBySublibraryId/{sublibraryId}")
    public Result getCountBySublibraryId(@PathVariable String sublibraryId) {
        String warehouseId = sublibraryId.substring(0, 4);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.likeRight("sublibrary_id", warehouseId);
        queryWrapper.eq("check_status", 0);
        Integer count = makeInventoryMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据");
    }


    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "PDA-根据单据编号和子库编号获取盘点单数据(附带物料详情)")
    @GetMapping("/getMakeInventoryForPda")
    public Result getMakeInventoryByDocNumAndSublibraryIdForPDA(@RequestParam String docNum,
                                                                @RequestParam String sublibraryId
    ) {
        String warehouseId = sublibraryId.substring(0, 4);
        MakeInventory makeInventory = makeInventoryService.getMakeInventoryByDocNumAndWarehouse(docNum, warehouseId);
        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }
        List<MakeInventoryDetails> makeInventoryDetailsList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for (MakeInventoryDetails makeInventoryDetails:makeInventoryDetailsList
             ) {
            JSONObject jsonObject = new JSONObject();
            Material material = materialService.getMeterialByMeterialCode(makeInventoryDetails.getMaterialCoding());
            jsonObject.put("details",makeInventoryDetails);
            jsonObject.put("material",material);
            jsonArray.add(jsonObject);
        }

        JSONObject jsonObjectResult = new JSONObject();
        jsonObjectResult.put("doc",makeInventory);
        jsonObjectResult.put("details",jsonArray);
        return Result.success(jsonObjectResult);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "流程引擎-采购入库-查询")
    @GetMapping("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id) {

        EntityUtils entityUtils = new EntityUtils();
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
            if (ObjectUtil.isNotNull(makeInventory)) {
                List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

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
                jsonResult.put("tableName", "make_inventory");
                jsonResult.put("main", entityUtils.jsonField("makeInventory", new MakeInventory()));
                jsonResult.put("details", entityUtils.jsonField("makeInventory", new MakeInventoryDetails()));
                jsonResult.put("mainValue", makeInventory);
                jsonResult.put("detailsValue", makeInventoryDetailsList);
                jsonResult.put("mainKey","updateMakeInventoryDTO");
                jsonResult.put("detailKey","updateMakeInventoryDetailsDTO");
                jsonResult.put("mainUpdate", "/wms/api/v1/make-inventory/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/make-inventory-details/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/enter-warehouse/missionCompleted");
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
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "流程引擎-采购入库-发起")
    @PutMapping("/missionStarts")
    public Result missionStarts(@RequestParam Integer id,
                                @RequestParam String processInstanceId) {

        try {
            MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotNull(makeInventory)) {
                UpdateMakeInventoryDTO updateMakeInventoryDTO = new UpdateMakeInventoryDTO();
                updateMakeInventoryDTO.setId(id);
                updateMakeInventoryDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateMakeInventoryDTO.setPlanStatus(2);
                Result result = makeInventoryService.updateMakeInventory(updateMakeInventoryDTO);
                if (result.isOk()) {
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
}

