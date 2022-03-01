package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OnShelf;
import com.huanhong.wms.entity.dto.AddOnShelfDTO;
import com.huanhong.wms.entity.dto.UpdateOnShelfDTO;
import com.huanhong.wms.entity.vo.OnShelfVO;
import com.huanhong.wms.mapper.OnShelfMapper;
import com.huanhong.wms.service.IOnShelfService;
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
@RequestMapping("/v1//on-shelf")
@ApiSort()
@Api(tags = "上架单据管理")
public class OnShelfController extends BaseController {

    @Resource
    private IOnShelfService onShelfService;
    @Resource
    private OnShelfMapper onShelfMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<OnShelf>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      OnShelfVO onShelfVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<OnShelf> pageResult = onShelfService.pageFuzzyQuery(new Page<>(current, size), onShelfVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关上架单信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常" + e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddOnShelfDTO addOnShelfDTO) {
        return onShelfService.addOnShelf(addOnShelfDTO);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateOnShelfDTO updateOnShelfDTO) {
        return onShelfService.updateOnshelf(updateOnShelfDTO);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("deleteById/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = onShelfMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据单据号和仓库编号获取单据信息")
    @GetMapping("getOnshelfByDocNumAndWarehouseId/{docNum}&{warehouseId}")
    public Result getOnshelfByDocNumAndWarehouseId(@PathVariable String docNum,
                                                    @PathVariable String warehouseId) {
        OnShelf onShelf = onShelfService.getOnshelfByDocNumAndWarehouseId(docNum, warehouseId);
        return ObjectUtil.isNotNull(onShelf) ? Result.success(onShelf): Result.failure("未查询到相关数据");
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据ID获取单据信息")
    @GetMapping("getOnshelfById/{id}")
    public Result getOnshelfById(@PathVariable Integer id){
        OnShelf onShelf = onShelfService.getOnshelfById(id);
        return ObjectUtil.isNotNull(onShelf) ? Result.success(onShelf): Result.failure("未查询到相关数据");
    }


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据仓库编号获取未完成的上架单数量")
    @GetMapping("getCountByWarehouseId/{warehouseId}")
    public Result getCountByWarehouseId(@PathVariable String warehouseId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("warehouse",warehouseId);
        queryWrapper.eq("complete",0);
        Integer count =  onShelfMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据") ;
    }
}

