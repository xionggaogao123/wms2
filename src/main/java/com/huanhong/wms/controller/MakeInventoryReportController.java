package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.MakeInventoryReportVO;
import com.huanhong.wms.service.IMakeInventoryDetailsService;
import com.huanhong.wms.service.IMakeInventoryReportDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.IMakeInventoryReportService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiSort()
@Slf4j
@Validated
@Api(tags = "盘点报告管理")
@RestController
@RequestMapping("/v1/make-inventory-report")
public class MakeInventoryReportController extends BaseController {

    @Resource
    private IMakeInventoryReportService makeInventoryReportService;

    @Resource
    private IMakeInventoryReportDetailsService makeInventoryReportDetailsService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<MakeInventoryReport>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  MakeInventoryReportVO makeInventoryReportVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<MakeInventoryReport> pageResult = makeInventoryReportService.pageFuzzyQuery(new Page<>(current, size), makeInventoryReportVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到盘点报告信息");
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
        public Result add(@Valid @RequestBody AddMakeInventoryReportAndDetailsDTO addMakeInventoryReportAndDetailsDTO) {
            try {

                Result result = makeInventoryReportService.addMakeInventoryReport(addMakeInventoryReportAndDetailsDTO.getAddMakeInventoryReportDTO());

                if (!result.isOk()) {
                    return Result.failure("新增盘点报告失败");
                }

                MakeInventoryReport makeInventoryReport = (MakeInventoryReport)result.getData();

                String docNum = makeInventoryReport.getReportNumber();
                String warehouseId = makeInventoryReport.getWarehouseId();
                List<AddMakeInventoryReportDetailsDTO> addMakeInventoryReportDetailsDTOList = addMakeInventoryReportAndDetailsDTO.getAddMakeInventoryReportDetailsDTOList();
                if (ObjectUtil.isNotNull(addMakeInventoryReportDetailsDTOList)) {
                    for (AddMakeInventoryReportDetailsDTO details : addMakeInventoryReportDetailsDTOList
                    ) {
                        details.setReportNumber(docNum);
                        details.setWarehouseId(warehouseId);
                    }
                    makeInventoryReportDetailsService.addMakeInventoryReportDetailsList(addMakeInventoryReportDetailsDTOList);
                }
                return result;
            } catch (Exception e) {
                log.error("添加盘点报告出错，异常", e);
                return Result.failure("系统异常：盘点报告添加失败。");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateMakeInventoryReportAndDetailsDTO updateMakeInventoryReportAndDetailsDTO) {
            try {
                Result resultUpdateMakeInventory = makeInventoryReportService.updateMakeInventoryReport(updateMakeInventoryReportAndDetailsDTO.getUpdateMakeInventoryReportDTO());
                if (resultUpdateMakeInventory.isOk()) {
                    return makeInventoryReportDetailsService.updateMakeInventoryReportDetailsList(updateMakeInventoryReportAndDetailsDTO.getUpdateMakeInventoryReportDetailsDTOList());
                } else {
                    return Result.failure("更新失败");
                }
            } catch (Exception e) {
                log.error("系统异常：更新盘点报告主表及明细失败！",e);
                return Result.failure("系统异常，更新失败");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("delete/{id}")
        public Result delete(@PathVariable Integer id) {

            MakeInventoryReport makeInventoryReport = makeInventoryReportService.getMakeInventoryReportById(id);

            if (ObjectUtil.isNull(makeInventoryReport)){
                return Result.failure("单据不存在！");
            }

            boolean delete = makeInventoryReportService.removeById(id);

            //主表删除成功,删除明细
            if (delete){
                String docNum = makeInventoryReport.getDocumentNumber();

                List<MakeInventoryReportDetails> makeInventoryReportDetailsList = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByDocNumberAndWarehosueId(docNum,makeInventoryReport.getWarehouseId());

                for (MakeInventoryReportDetails makeInventoryReportDetails : makeInventoryReportDetailsList
                ) {
                    makeInventoryReportDetailsService.removeById(makeInventoryReportDetails.getId());
                }
            }
            return Result.success("删除成功");
        }


        @ApiImplicitParams({
                @ApiImplicitParam(name = "documentNumber", value = "单据编号"),
                @ApiImplicitParam(name = "wareHouseId", value = "仓库编号")
        })
        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据单据编号和仓库id获取详细信息")
        @GetMapping("/getMakeInventoryReportByDocNumAndWarhouseId/{documentNumber}&{wareHouseId}")
        public Result getMakeInventoryReportByDocNumAndWarhouseId(@PathVariable String documentNumber,
                                                                    @PathVariable String wareHouseId) {
            try {
                Map map = new HashMap();
                MakeInventoryReport makeInventoryReport = makeInventoryReportService.getMakeInventoryReportByDocNumAndWarehouse(documentNumber,wareHouseId);
                if (ObjectUtil.isNotEmpty(makeInventoryReport)) {
                    List<MakeInventoryReportDetails> makeInventoryReportDetailsList = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByDocNumberAndWarehosueId(documentNumber,wareHouseId);
                    map.put("doc", makeInventoryReport);
                    map.put("details", makeInventoryReportDetailsList);
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
                MakeInventoryReport makeInventoryReport = makeInventoryReportService.getMakeInventoryReportById(id);
                if (ObjectUtil.isNotEmpty(makeInventoryReport)) {
                    List<MakeInventoryReportDetails> makeInventoryReportDetailsList = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByDocNumberAndWarehosueId(makeInventoryReport.getReportNumber(), makeInventoryReport.getWarehouseId());
                    map.put("doc", makeInventoryReport);
                    map.put("details", makeInventoryReportDetailsList);
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
        @ApiOperationSupport(order = 9)
        @ApiOperation(value = "流程引擎-采购入库-查询")
        @GetMapping("getParameterById/{id}")
        public Result getParameterById(@PathVariable Integer id) {

            EntityUtils entityUtils = new EntityUtils();
            /**
             * 根据主表ID获取主表及明细表数据
             */
            try {
                MakeInventoryReport makeInventoryReport = makeInventoryReportService.getMakeInventoryReportById(id);

                if (ObjectUtil.isNotNull(makeInventoryReport)) {

                    List<MakeInventoryReportDetails> makeInventoryReportDetailsList = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByDocNumberAndWarehosueId(makeInventoryReport.getDocumentNumber(),makeInventoryReport.getWarehouseId());

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
                    jsonResult.put("tableName", "make_inventory_report");
                    jsonResult.put("main", entityUtils.jsonField("makeInventoryReport", new MakeInventoryReport()));
                    jsonResult.put("details", entityUtils.jsonField("makeInventoryReport", new MakeInventoryReportDetails()));
                    jsonResult.put("mainValue", makeInventoryReport);
                    jsonResult.put("detailsValue", makeInventoryReportDetailsList);
                    jsonResult.put("mainKey","updateMakeInventoryReportDTO");
                    jsonResult.put("detailKey","updateMakeInventoryReportDetailsDTO");
                    jsonResult.put("mainUpdate", "/wms/api/v1/make-inventory-report/update");
                    jsonResult.put("detailsUpdate", "/wms/api/v1/make-inventory-report-details/update");
                    jsonResult.put("missionCompleted", "/wms/api/v1/make-inventory-report/missionCompleted");
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

                MakeInventoryReport makeInventoryReport = makeInventoryReportService.getMakeInventoryReportById(id);
                if (makeInventoryReport.getCheckStatus()!=1){
                    return Result.failure("发起审批失败，盘点计划未完成！");
                }
                /**
                 * 正常情况不需要原对单据进行非空验证，
                 * 此处预留其他判断条件的位置
                 */
                if (ObjectUtil.isNotNull(makeInventoryReport)) {

                    UpdateMakeInventoryReportDTO updateMakeInventoryReportDTO = new UpdateMakeInventoryReportDTO();
                    updateMakeInventoryReportDTO.setId(id);
                    updateMakeInventoryReportDTO.setProcessInstanceId(processInstanceId);

                    /**
                     *  单据状态由草拟转为审批中
                     *  审批状态:
                     *  1.草拟
                     *  2.审批中
                     *  3.审批生效
                     *  4.作废
                     */
                    updateMakeInventoryReportDTO.setPlanStatus(2);
                    Result result = makeInventoryReportService.updateMakeInventoryReport(updateMakeInventoryReportDTO);
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

