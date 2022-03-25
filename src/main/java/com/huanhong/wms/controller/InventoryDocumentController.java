package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDTO;
import com.huanhong.wms.entity.vo.InventoryDocumentVO;
import com.huanhong.wms.mapper.InventoryDocumentMapper;
import com.huanhong.wms.service.IInventoryDocumentService;
import com.huanhong.wms.service.IMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/v1//inventory-document")
@ApiSort()
@Api(tags = "清点单管理")
public class InventoryDocumentController extends BaseController {

    @Resource
    private IInventoryDocumentService inventoryDocumentService;
    @Resource
    private InventoryDocumentMapper inventoryDocumentMapper;
    @Resource
    private IMaterialService materialService;

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
    public Result add(@Valid @RequestBody AddInventoryDocumentDTO addInventoryDocumentDTO) {
        try {
            return inventoryDocumentService.addInventoryDocument(addInventoryDocumentDTO);
        } catch (Exception e) {
            log.error("新增清点单失败", e);
            return Result.failure("新增失败--系统异常,请联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateInventoryDocumentDTO updateInventoryDocumentDTO) {
        try {
            return inventoryDocumentService.updateInventoryDocument(updateInventoryDocumentDTO);
        }catch (Exception e){
            log.error("更新清点单失败",e);
            return Result.failure("更新失败--系统异常,请联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = inventoryDocumentMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "通过ID查询清点单")
    @GetMapping("getInventoryDocumentById/{id}")
    public InventoryDocument getInventoryDocumentById(@PathVariable Integer id){
        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentById(id);
        return ObjectUtil.isNotNull(inventoryDocument) ? inventoryDocument : null;
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "通过单据号和仓库id查询清点单")
    @GetMapping("getInventoryDocumentByDocNumAndWarhouseId/{docNum}&{warehouseId}")
    public InventoryDocument getInventoryDocumentByDocNumAndWarhouseId(@PathVariable String docNum, @PathVariable String warehouseId){
        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentByDocumentNumberAndWarehouseId(docNum, warehouseId);
        return ObjectUtil.isNotNull(inventoryDocument) ? inventoryDocument : null;
    }




    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "PDA-通过单据号和仓库id查询清点单")
    @GetMapping("/getInventoryDocumentForPda")
    public Result getInventoryDocumentForPda(@RequestParam String docNum,
                                             @RequestParam String warehouseId
    ){
        InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentByDocumentNumberAndWarehouseId(docNum, warehouseId);
        if (ObjectUtil.isEmpty(inventoryDocument)) {
            return Result.success("未查到相关数据！");
        }
        Material material = materialService.getMeterialByMeterialCode(inventoryDocument.getMaterialCoding());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doc",inventoryDocument );
        jsonObject.put("material",material);
        return Result.success(jsonObject);
    }
}

