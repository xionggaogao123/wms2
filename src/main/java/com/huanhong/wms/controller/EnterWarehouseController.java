package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.dto.AddEnterWarehouseAndDetails;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDTO;
import com.huanhong.wms.entity.vo.EnterWarehouseVO;
import com.huanhong.wms.mapper.EnterWarehouseMapper;
import com.huanhong.wms.service.IEnterWarehouseDetailsService;
import com.huanhong.wms.service.IEnterWarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/enter-warehouse")
@ApiSort()
@Api(tags = "采购入库单主表")
public class EnterWarehouseController extends BaseController {

    @Resource
    private IEnterWarehouseService enter_warehouseService;

    @Resource
    private EnterWarehouseMapper enter_warehouseMapper;

    @Resource
    private IEnterWarehouseDetailsService enterWarehouseDetailsService;

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
            Result result = enter_warehouseService.addEnterWarehouse(addEnterWarehouseAndDetails.getAddEnterWarehouseDTO());
            EnterWarehouse enterWarehouse = (EnterWarehouse) result.getData();
            String docNum = enterWarehouse.getDocumentNumber();
            String warehouse = addEnterWarehouseAndDetails.getAddEnterWarehouseDTO().getWarehouse();
            List<AddEnterWarehouseDetailsDTO> addEnterWarehouseDetailsDTOList = addEnterWarehouseAndDetails.getAddEnterWarehouseDetailsDTOList();
            if (ObjectUtil.isNotNull(addEnterWarehouseDetailsDTOList)) {
                for (AddEnterWarehouseDetailsDTO details : addEnterWarehouseDetailsDTOList
                ) {
                    details.setOriginalDocumentNumber(docNum);
                    details.setWarehouse(warehouse);
                }
                enterWarehouseDetailsService.addEnterWarehouseDetails(addEnterWarehouseDetailsDTOList);
            }
            return result;
        } catch (Exception e) {
            log.error("添加入库单出错，异常", e);
            return Result.failure("系统异常：入库单添加失败。");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新采购入库单主表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateEnterWarehouseDTO updateEnterWarehouseDTO) {
        return enter_warehouseService.updateEnterWarehouse(updateEnterWarehouseDTO);
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
            List<EnterWarehouseDetails> enterWarehouseDetailsList = enterWarehouseDetailsService.getListWarehouseDetailsByDocNumberAndWarehosue(documentNumber, wareHouse);
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
                List<EnterWarehouseDetails> enterWarehouseDetailsList = enterWarehouseDetailsService.getListWarehouseDetailsByDocNumberAndWarehosue(enterWarehouse.getDocumentNumber(), enterWarehouse.getWarehouse());
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
}

