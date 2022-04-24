package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.dto.AddMakeInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDTO;
import com.huanhong.wms.entity.vo.MakeInventoryVO;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.service.IMakeInventoryService;
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
@RequestMapping("/v1//make-inventory")
@ApiSort()
@Api(tags = "盘点单据管理")
public class MakeInventoryController extends BaseController {

    @Resource
    private IMakeInventoryService makeInventoryService;
    @Resource
    private MakeInventoryMapper makeInventoryMapper;
    @Resource
    private IMaterialService materialService;

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
    public Result add(@Valid @RequestBody AddMakeInventoryDTO addMakeInventoryDTO) {



        return makeInventoryService.addMakeInventory(addMakeInventoryDTO);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新清点单", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateMakeInventoryDTO updateMakeInventoryDTO) {
        return makeInventoryService.updateMakeInventory(updateMakeInventoryDTO);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = makeInventoryMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取盘点单数据")
    @GetMapping("/getMakeInventoryById/{id}")
    public Result getMakeInvenrory(@PathVariable Integer id) {
        MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
        return ObjectUtil.isNotNull(makeInventory) ? Result.success(makeInventory) : Result.failure("未查询到相关信息");
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号和子库编号获取盘点单数据")
    @GetMapping("/getMakeInventoryByDocNumAndSublibraryId/{docNum}&{sublibraryId}")
    public Result getMakeInventoryByDocNumAndSublibraryId(@PathVariable String docNum,
                                                          @PathVariable String sublibraryId
    ) {
        String warehouseId = sublibraryId.substring(0, 4);
        MakeInventory makeInventory = makeInventoryService.getMakeInventoryByDocNumAndWarehouse(docNum, warehouseId);
        return ObjectUtil.isNotNull(makeInventory) ? Result.success(makeInventory) : Result.failure("未查询到相关信息");
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
        Material material = materialService.getMeterialByMeterialCode(makeInventory.getMaterialCoding());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doc",makeInventory);
        jsonObject.put("material",material);
        return Result.success(jsonObject);
    }
}

