package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ShelfTemplate;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.dto.AddShelfTemplateDTO;
import com.huanhong.wms.entity.dto.UpdateShelfTemplateDTO;
import com.huanhong.wms.entity.vo.ShelfTemplateVO;
import com.huanhong.wms.mapper.ShelfTemplateMapper;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.IShelfTemplateService;
import com.huanhong.wms.service.IWarehouseManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1//shelf-template")
@ApiSort()
@Api(tags = "货架模板管理")
public class ShelfTemplateController extends BaseController {

    @Resource
    private IShelfTemplateService shelf_templateService;

    @Resource
    private ShelfTemplateMapper shelf_templateMapper;

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private WarehouseManagementMapper warehouseManagementMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<ShelfTemplate>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            ShelfTemplateVO shelfTemplateVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ShelfTemplate> pageResult = shelf_templateService.pageFuzzyQuery(new Page<>(current, size), shelfTemplateVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关模板信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加模板")
    @PostMapping
    public Result add(@Valid @RequestBody AddShelfTemplateDTO addShelfTemplateDTO) {
        /**
         *检测仓库是否存在
         */
        try {
            WarehouseManagement warehouseManagement = warehouseManagementService.getWarehouseByWarehouseId(addShelfTemplateDTO.getWarehouseId());
            if (ObjectUtil.isEmpty(warehouseManagement)){
                return Result.success("未查询到相关仓库信息");
            }
            ShelfTemplate shelfTemplate = new ShelfTemplate();
            BeanUtil.copyProperties(addShelfTemplateDTO,shelfTemplate);
            int insert = shelf_templateMapper.insert(shelfTemplate);
            return render(insert > 0);
        }catch (Exception e){
            return Result.failure("添加失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据模板ID更新")
    @PutMapping
    public Result updateById(@Valid @RequestBody UpdateShelfTemplateDTO updateShelfTemplateDTO) {
        try {
            ShelfTemplate shelfTemplate = shelf_templateService.getShelfTemplateByID(updateShelfTemplateDTO.getId());
            if (ObjectUtil.isEmpty(shelfTemplate)){
                return Result.failure("更新失败--货架模板不存在");
            }
            ShelfTemplate shelfTemplateUpdate = new ShelfTemplate();
            BeanUtil.copyProperties(updateShelfTemplateDTO,shelfTemplateUpdate);
            int update = shelf_templateMapper.updateById(shelfTemplateUpdate);
            return render(update > 0);
        }catch (Exception e){
            return Result.failure("更新失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "根据模板Id删除")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            ShelfTemplate shelfTemplate = shelf_templateService.getShelfTemplateByID(id);
            if (ObjectUtil.isEmpty(shelfTemplate)){
                return Result.failure("删除失败--货架模板不存在");
            }
            int i = shelf_templateMapper.deleteById(id);
            return render(i > 0);
        }catch (Exception e){
            return Result.failure("更新失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("根据模板Id获取模板信息")
    @GetMapping("/{id}")
    public Result getShelfTemplateById(@PathVariable Integer id){
        try {
            ShelfTemplate shelfTemplate = shelf_templateService.getShelfTemplateByID(id);
            if (ObjectUtil.isEmpty(shelfTemplate)){
                return Result.success(shelfTemplate, "未查询到相关模板信息");
            }
            return Result.success(shelfTemplate);
        }catch (Exception e){
            return Result.failure("查询失败--异常：" + e);
        }
    }
}

