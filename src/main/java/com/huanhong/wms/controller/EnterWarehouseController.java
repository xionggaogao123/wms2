package com.huanhong.wms.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.EnterWarehouseVO;
import com.huanhong.wms.mapper.EnterWarehouseMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/enter-warehouse")
@Validated
@ApiSort()
@Api(tags = "采购入库单主表")
public class EnterWarehouseController extends BaseController {

    @Resource
    private IEnterWarehouseService enter_warehouseService;

    @Resource
    private EnterWarehouseMapper enter_warehouseMapper;

    @Resource
    private IEnterWarehouseDetailsService enterWarehouseDetailsService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IMaterialService materialService;

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询采购入库单主表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<EnterWarehouse>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             EnterWarehouseVO enterWarehouseVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<EnterWarehouse> pageResult = enter_warehouseService.pageFuzzyQuery(new Page<>(current, size), enterWarehouseVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到入库单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加采购入库单主表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddEnterWarehouseAndDetails addEnterWarehouseAndDetails) {
        try {

//            WarehouseManagement warehouse = warehouseManagementService.getWarehouseByWarehouseId(addEnterWarehouseAndDetails.getAddEnterWarehouseDTO().getWarehouse());
//            if(ObjectUtil.isNull(warehouse)){
//                return Result.failure("仓库不存在");
//            }
            Result result = enter_warehouseService.addEnterWarehouse(addEnterWarehouseAndDetails.getAddEnterWarehouseDTO());
            if (!result.isOk()) {
                return Result.failure("新增入库单失败");
            }
            EnterWarehouse enterWarehouse = (EnterWarehouse) result.getData();
            String docNum = enterWarehouse.getDocumentNumber();
            String warehouseId = addEnterWarehouseAndDetails.getAddEnterWarehouseDTO().getWarehouse();
            List<AddEnterWarehouseDetailsDTO> addEnterWarehouseDetailsDTOList = addEnterWarehouseAndDetails.getAddEnterWarehouseDetailsDTOList();
            if (ObjectUtil.isNotNull(addEnterWarehouseDetailsDTOList)) {
                for (AddEnterWarehouseDetailsDTO details : addEnterWarehouseDetailsDTOList
                ) {
                    details.setOriginalDocumentNumber(docNum);
                    details.setWarehouse(warehouseId);
                }
                // 01AA0000
                Result resultAdd = enterWarehouseDetailsService.addEnterWarehouseDetails(addEnterWarehouseDetailsDTOList);

                if (!resultAdd.isOk()) {
                    return Result.failure("新增入库明细单失败");
                }
            }
        } catch (Exception e) {
            log.error("添加入库单出错，异常", e);
            return Result.failure("系统异常：入库单添加失败。");
        }
        return Result.failure("系统异常");
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新采购入库单主表及明细", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateEnterWarehouseAndDetailsDTO updateEnterWarehouseAndDetailsDTO) {
        try {
            Result resultUpdateEnterWarehouse = enter_warehouseService.updateEnterWarehouse(updateEnterWarehouseAndDetailsDTO.getUpdateEnterWarehouseDTO());
            if (resultUpdateEnterWarehouse.isOk()) {
                return enterWarehouseDetailsService.updateEnterWarehouseDetails(updateEnterWarehouseAndDetailsDTO.getUpdateEnterWarehouseDetailsDTOList());
            } else {
                return Result.failure("更新失败");
            }
        } catch (Exception e) {
            return Result.failure("系统异常，更新失败");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除采购入库单主表", notes = "生成代码")
    @DeleteMapping("deleteByid/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = enter_warehouseMapper.deleteById(id);
        return render(i > 0);
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据单据编号和仓库ID获取采购入库单信息")
    @GetMapping("getEnterWarehouseByDocNumAndWarehouse/{documentNumber}&{wareHouse}")
    public Result getEnterWarehouseByDocNumAndWarehouse(@PathVariable String documentNumber,
                                                        @PathVariable String wareHouse
    ) {
        try {
            Map map = new HashMap();
            EnterWarehouse enterWarehouse = enter_warehouseService.getEnterWarehouseByDocNumberAndWarhouse(documentNumber, wareHouse);
            List<EnterWarehouseDetails> enterWarehouseDetailsList = enterWarehouseDetailsService.getListEnterWarehouseDetailsByDocNumberAndWarehosue(documentNumber, wareHouse);
            if (ObjectUtil.isNotEmpty(enterWarehouse)) {
                map.put("doc", enterWarehouse);
                map.put("details", enterWarehouseDetailsList);
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
    @ApiOperation(value = "根据ID获取出库单及其明细")
    @GetMapping("getEnterWarehouseById/{id}")
    public Result getEnterWarehouseById(@PathVariable Integer id) {
        try {
            Map map = new HashMap();
            EnterWarehouse enterWarehouse = enter_warehouseService.getEnterWarehouseById(id);
            if (ObjectUtil.isNotEmpty(enterWarehouse)) {
                List<EnterWarehouseDetails> enterWarehouseDetailsList = enterWarehouseDetailsService.getListEnterWarehouseDetailsByDocNumberAndWarehosue(enterWarehouse.getDocumentNumber(), enterWarehouse.getWarehouse());
                map.put("enter_warehouse", enterWarehouse);
                map.put("enter_warehouse_details", enterWarehouseDetailsList);
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
        queryWrapper.eq("warehouse", warehouseId);
        queryWrapper.eq("status", 2);
        Integer count = enter_warehouseMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "流程引擎-采购入库-查询")
    @GetMapping("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id) {

        EntityUtils entityUtils = new EntityUtils();
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            EnterWarehouse enterWarehouse = enter_warehouseService.getEnterWarehouseById(id);
            if (ObjectUtil.isNotNull(enterWarehouse)) {
                List<EnterWarehouseDetails> enterWarehouseList = enterWarehouseDetailsService.getListEnterWarehouseDetailsByDocNumberAndWarehosue(enterWarehouse.getDocumentNumber(), enterWarehouse.getWarehouse());
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
                jsonResult.put("tableName", "enter_warehouse");
                jsonResult.put("main", entityUtils.jsonField("enterWarehouse", new EnterWarehouse()));
                jsonResult.put("details", entityUtils.jsonField("enterWarehouse", new EnterWarehouseDetails()));
                jsonResult.put("mainValue", enterWarehouse);
                jsonResult.put("detailsValue", enterWarehouseList);
                jsonResult.put("mainUpdate", "/wms/api/v1/enter-warehouse/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/enter-warehouse-details");
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
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "流程引擎-采购入库-发起")
    @PutMapping("/missionStarts")
    public Result missionStarts(@RequestParam Integer id,
                                @RequestParam String processInstanceId) {

        try {
            EnterWarehouse enterWarehouse = enter_warehouseService.getEnterWarehouseById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotNull(enterWarehouse)) {
                UpdateEnterWarehouseDTO updateEnterWarehouseDTO = new UpdateEnterWarehouseDTO();
                updateEnterWarehouseDTO.setId(id);
                updateEnterWarehouseDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateEnterWarehouseDTO.setState(2);
                Result result = enter_warehouseService.updateEnterWarehouse(updateEnterWarehouseDTO);
                if (result.isOk()) {
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("采购入库单异常,无法进入流程引擎");
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
    @ApiOperation(value = "流程引擎-采购入库-完成审批")
    @PutMapping("/missionCompleted")
    public Result missionCompleted(@RequestParam String processInstanceId) {
        try {
            //通过流程Id查询出单据Id
            EnterWarehouse enterWarehouse = enter_warehouseService.getEnterWarehouseByProcessInstanceId(processInstanceId);
            if (ObjectUtil.isNotNull(enterWarehouse)) {
                UpdateEnterWarehouseDTO updateEnterWarehouseDTO = new UpdateEnterWarehouseDTO();
                updateEnterWarehouseDTO.setId(enterWarehouse.getId());
                /**
                 *  单据状态由审批中改为审批生效
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateEnterWarehouseDTO.setState(3);
                return enter_warehouseService.updateEnterWarehouse(updateEnterWarehouseDTO);
            } else {
                return Result.failure("单据异常无法完成");
            }
        } catch (Exception e) {
            log.error("完成审批接口异常", e);
            return Result.failure("系统异常");
        }
    }

}

