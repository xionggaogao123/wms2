package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.TemporaryEnterWarehouseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseMapper;
import com.huanhong.wms.service.ITemporaryEnterWarehouseService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Slf4j
@ApiSort()
@Api(tags = "")
@RestController
@RequestMapping("/v1/temporary-enter-warehouse")
public class TemporaryEnterWarehouseController extends BaseController {

    @Resource
    private ITemporaryEnterWarehouseService temporaryEnterWarehouseService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryEnterWarehouse>> page(@RequestParam(defaultValue = "1") Integer current,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      TemporaryEnterWarehouseVO temporaryEnterWarehouseVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryEnterWarehouse> pageResult = temporaryEnterWarehouseService.pageFuzzyQuery(new Page<>(current, size), temporaryEnterWarehouseVO);
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
        @ApiOperation(value = "添加", notes = "生成代码")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddTemporaryEnterWarehouseDTO addTemporaryEnterWarehouseDTO) {
            try {
                return temporaryEnterWarehouseService.addEnterWarehouse(addTemporaryEnterWarehouseDTO);
            }catch (Exception e){
                return Result.failure("新增临库入库单失败");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateTemporaryEnterWarehouseDTO updateTemporaryEnterWarehouseDTO) {
            try {
                return temporaryEnterWarehouseService.updateTemporaryEnterWarehouse(updateTemporaryEnterWarehouseDTO);
            }catch (Exception e){
                return Result.failure("更新临库入库单失败");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("deleteById/{id}")
        public Result delete(@PathVariable Integer id) {

            TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseService.getTemporaryEnterWarehouseById(id);

            if (ObjectUtil.isNull(temporaryEnterWarehouse)){
                return Result.failure("单据不存在！");
            }
            boolean delete = temporaryEnterWarehouseService.removeById(id);
            return render(delete);
        }

        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据单据编号和仓库ID获取临库入库单信息")
        @GetMapping("/getTemporaryEnterWarehouseByDocNumberAndWarhouseId")
        public Result getTemporaryEnterWarehouseByDocNumberAndWarhouseId(@RequestParam String docNum,
                                                            @RequestParam String wareHouseId
        ) {
            try {
                TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseService.getTemporaryEnterWarehouseByDocNumberAndWarhouseId(docNum,wareHouseId);
                return ObjectUtil.isNotEmpty(temporaryEnterWarehouse)?Result.success(temporaryEnterWarehouse):Result.failure("未查询到单据信息！");
            } catch (Exception e) {
                log.error("查询失败,异常：", e);
                return Result.failure("查询失败，系统异常！");
            }
        }

        @ApiOperationSupport(order = 6)
        @ApiOperation(value = "根据ID获取临库入库单信息")
        @GetMapping("/getEnterWarehouseById")
        public Result getEnterWarehouseById(@RequestParam Integer id) {
            try {
                TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseService.getTemporaryEnterWarehouseById(id);
                return ObjectUtil.isNotEmpty(temporaryEnterWarehouse)?Result.success(temporaryEnterWarehouse):Result.failure("未查询到单据信息！");
            } catch (Exception e) {
                log.error("查询失败,异常：", e);
                return Result.failure("查询失败，系统异常！");
            }
        }

        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "单据Id"),
        })
        @ApiOperationSupport(order = 7)
        @ApiOperation(value = "流程引擎-采购入库-查询")
        @GetMapping("getParameterById/{id}")
        public Result getParameterById(@PathVariable Integer id) {

            EntityUtils entityUtils = new EntityUtils();
            /**
             * 根据主表ID获取主表及明细表数据
             */
            try {
                TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseService.getTemporaryEnterWarehouseById(id);
                if (ObjectUtil.isNotNull(temporaryEnterWarehouse)) {

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
                    jsonResult.put("tableName", "temporary_enter_warehouse");
                    jsonResult.put("main", entityUtils.jsonField("temporaryEnter", new TemporaryEnterWarehouse()));
                    jsonResult.put("details", null);
                    jsonResult.put("mainValue", temporaryEnterWarehouse);
                    jsonResult.put("detailsValue", null);
                    jsonResult.put("mainKey","updateTemporaryEnterWarehouseDTO");
                    jsonResult.put("detailKey",null);
                    jsonResult.put("mainUpdate", "/wms/api/v1/temporary-enter-warehouse/update");
                    jsonResult.put("detailsUpdate", null);
                    jsonResult.put("missionCompleted", "/wms/api/v1/temporary-enter-warehouse/missionCompleted");
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
        @ApiOperation(value = "流程引擎-采购入库-发起")
        @PutMapping("/missionStarts")
        public Result missionStarts(@RequestParam Integer id,
                                    @RequestParam String processInstanceId) {

            try {
                TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseService.getTemporaryEnterWarehouseById(id);
                /**
                 * 正常情况不需要原对单据进行非空验证，
                 * 此处预留其他判断条件的位置
                 */
                if (ObjectUtil.isNotNull(temporaryEnterWarehouse)) {
                    UpdateTemporaryEnterWarehouseDTO updateTemporaryEnterWarehouseDTO = new UpdateTemporaryEnterWarehouseDTO();
                    updateTemporaryEnterWarehouseDTO.setId(id);
                    updateTemporaryEnterWarehouseDTO.setProcessInstanceId(processInstanceId);
                    /**
                     *  单据状态由草拟转为审批中
                     *  审批状态:
                     *  1.草拟
                     *  2.审批中
                     *  3.审批生效
                     *  4.作废
                     */
                    updateTemporaryEnterWarehouseDTO.setState(2);
                    Result result = temporaryEnterWarehouseService.updateTemporaryEnterWarehouse(updateTemporaryEnterWarehouseDTO);
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


//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "processInstanceId", value = "流程Id")
//    })
//    @ApiOperationSupport(order = 9)
//    @ApiOperation(value = "流程引擎-采购入库-完成审批")
//    @PutMapping("/missionCompleted")
//    public Result missionCompleted(@RequestParam String processInstanceId) {
//        try {
//            //通过流程Id查询出单据Id
//            EnterWarehouse enterWarehouse = enter_warehouseService.getEnterWarehouseByProcessInstanceId(processInstanceId);
//            if (ObjectUtil.isNotNull(enterWarehouse)) {
//                UpdateEnterWarehouseDTO updateEnterWarehouseDTO = new UpdateEnterWarehouseDTO();
//                updateEnterWarehouseDTO.setId(enterWarehouse.getId());
//                /**
//                 *  单据状态由审批中改为审批生效
//                 *  审批状态:
//                 *  1.草拟
//                 *  2.审批中
//                 *  3.审批生效
//                 *  4.作废
//                 */
//                updateEnterWarehouseDTO.setState(3);
//                Result result = enter_warehouseService.updateEnterWarehouse(updateEnterWarehouseDTO);
//                if (result.isOk()){
//                    List<EnterWarehouseDetails> enterWarehouseDetailsList = enterWarehouseDetailsService.getListEnterWarehouseDetailsByDocNumberAndWarehosue(enterWarehouse.getDocumentNumber(),enterWarehouse.getWarehouse());
//                    if (ObjectUtil.isNotNull(enterWarehouseDetailsList)){
//                        UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
//                        for (EnterWarehouseDetails enterWarehouseDetails:enterWarehouseDetailsList
//                        ) {
//                            String materialCoding = enterWarehouseDetails.getMaterialCoding();
//                            String batch = enterWarehouseDetails.getBatch();
//                            String warehouseId = enterWarehouseDetails.getWarehouse();
//                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(materialCoding,batch,warehouseId);
//                            for (InventoryInformation inventoryInformation:inventoryInformationList
//                            ) {
//                                //更新库存信息为已入库 入库时间 入库单编号
//                                BeanUtil.copyProperties(inventoryInformation,updateInventoryInformationDTO);
//                                //已入库
//                                updateInventoryInformationDTO.setIsEnter(1);
//                                //入库时间
//                                updateInventoryInformationDTO.setInDate(DateUtil.date());
//                                //入库单单据编号
//                                updateInventoryInformationDTO.setDocumentNumber(enterWarehouse.getDocumentNumber());
//                                inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
//                            }
//                        }
//                    }
//                    return result;
//                }else {
//                    return Result.failure("完成审批失败！");
//                }
//            } else {
//                return Result.failure("单据异常无法完成");
//            }
//        } catch (Exception e) {
//            log.error("完成审批接口异常", e);
//            return Result.failure("系统异常");
//        }
//    }




}

