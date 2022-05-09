package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddPlanUseOutDetailsDTO;
import com.huanhong.wms.entity.dto.AddTemporaryOutWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryOutWarehouseDTO;
import com.huanhong.wms.entity.vo.PdaMaterialVO;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;
import com.huanhong.wms.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.TemporaryOutWarehouseMapper;
import com.huanhong.wms.service.ITemporaryOutWarehouseService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Validated
@ApiSort()
@Api(tags = "临库出库单")
@RestController
@RequestMapping("/v1/temporary-out-warehouse")
public class TemporaryOutWarehouseController extends BaseController {

    @Resource
    private ITemporaryOutWarehouseService temporaryOutWarehouseService;

    @Resource
    private IUserService userService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryOutWarehouse>> page(@RequestParam(defaultValue = "1") Integer current,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    TemporaryOutWarehouseVO temporaryOutWarehouseVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryOutWarehouse> pageResult = temporaryOutWarehouseService.pageFuzzyQuery(new Page<>(current, size), temporaryOutWarehouseVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到临库出库单据信息");
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
        public Result add(@Valid @RequestBody AddTemporaryOutWarehouseDTO addTemporaryOutWarehouseDTO) {
            try {
                return temporaryOutWarehouseService.addTemporaryOutWarehouse(addTemporaryOutWarehouseDTO);
            } catch (Exception e) {
                log.error("添加临库出库单出错，异常", e);
                return Result.failure("系统异常：临库出库单添加失败。");
            }
        }


        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/upate")
        public Result update(@Valid @RequestBody UpdateTemporaryOutWarehouseDTO updateTemporaryOutWarehouseDTO) {
              try {
                  return temporaryOutWarehouseService.updateTemporaryOutWarehouse(updateTemporaryOutWarehouseDTO);
              }catch (Exception e){
                  log.error("更新临库出库单出错,异常", e);
                  return Result.failure("系统异常：临库出库单更新失败。");
              }
        }


        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("deleteById/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(temporaryOutWarehouseService.removeById(id));
        }


        @ApiImplicitParams({
                @ApiImplicitParam(name = "documentNumber", value = "单据编号"),
                @ApiImplicitParam(name = "wareHouseId", value = "仓库编号")
        })
        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据单据编号和仓库id获取详细信息")
        @GetMapping("/getTemporaryOutWarehouseByDocNumAndWarhouseId")
        public Result getTemporaryOutWarehouseByDocNumAndWarhouseId(@RequestParam String documentNumber,
                                                                    @RequestParam String wareHouseId) {
            try {
                TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseService.getTemporaryOutWarehouseByDocNumAndWarhouseId(documentNumber, wareHouseId);
                return ObjectUtil.isNotEmpty(temporaryOutWarehouse) ? Result.success(temporaryOutWarehouse) : Result.failure("未查询到相关信息！");
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
                TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseService.getTemporaryOutWarehouseById(id);
                return ObjectUtil.isNotEmpty(temporaryOutWarehouse) ? Result.success(temporaryOutWarehouse) : Result.failure("未查询到相关信息！");
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
                TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseService.getTemporaryOutWarehouseById(id);
                if (ObjectUtil.isNotNull(temporaryOutWarehouse)) {
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
                    jsonResult.put("tableName", "temporary_out_warehouse");
                    jsonResult.put("main", entityUtils.jsonField("temporaryOut", new TemporaryOutWarehouse()));
                    jsonResult.put("details", null);
                    jsonResult.put("mainValue", temporaryOutWarehouse);
                    jsonResult.put("detailsValue", null);
                    jsonResult.put("mainKey","updateTemporaryOutWarehouseDTO");
                    jsonResult.put("detailKey",null);
                    jsonResult.put("mainUpdate", "/wms/api/v1/enter-warehouse/update");
                    jsonResult.put("detailsUpdate", null);
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
        @ApiOperationSupport(order = 8)
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
                LoginUser loginUser = this.getLoginUser();

                TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseService.getTemporaryOutWarehouseById(id);

                if (ObjectUtil.isNotNull(temporaryOutWarehouse)) {

                    UpdateTemporaryOutWarehouseDTO updateTemporaryOutWarehouseDTO = new UpdateTemporaryOutWarehouseDTO();
                    updateTemporaryOutWarehouseDTO.setId(id);
                    updateTemporaryOutWarehouseDTO.setProcessInstanceId(processInstanceId);
                    //提交审批时 将更新数量
                    /**
                     *  单据状态由草拟转为审批中
                     *  审批状态:
                     *  1.草拟
                     *  2.审批中
                     *  3.审批生效
                     *  4.作废
                     */
                    updateTemporaryOutWarehouseDTO.setStatus(2);
                    Result result = temporaryOutWarehouseService.updateTemporaryOutWarehouse(updateTemporaryOutWarehouseDTO);

                    if (result.isOk()) {
                        //新增出库记录并减库存
                        Result resultAnother = temporaryOutWarehouseService.addTemporaryRecordUpdateInventory(temporaryOutWarehouse,loginUser);
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
        @ApiOperationSupport(order = 9)
        @ApiOperation(value = "流程引擎-领料出库-完成审批")
        @PutMapping("/missionCompleted")
        public Result missionCompleted(@RequestParam String processInstanceId) {

            try {

                //通过流程Id查询出单据Id
                TemporaryOutWarehouse temporaryOutWarehouse = temporaryOutWarehouseService.getTemporaryOutWarehouseByProcessInstanceId(processInstanceId);

                if (ObjectUtil.isNotNull(temporaryOutWarehouse)) {
                    UpdateTemporaryOutWarehouseDTO updateTemporaryOutWarehouseDTO = new UpdateTemporaryOutWarehouseDTO();
                    updateTemporaryOutWarehouseDTO.setId(temporaryOutWarehouse.getId());
                    /**
                     *  单据状态由审批中改为审批生效
                     *  审批状态:
                     *  1.草拟
                     *  2.审批中
                     *  3.审批生效
                     *  4.作废
                     */
                    updateTemporaryOutWarehouseDTO.setStatus(3);
                    return temporaryOutWarehouseService.updateTemporaryOutWarehouse(updateTemporaryOutWarehouseDTO);
                } else {
                    return Result.failure("单据异常无法完成");
                }
            } catch (Exception e) {
                log.error("完成审批接口异常", e);
                return Result.failure("系统异常");
            }
        }

}

