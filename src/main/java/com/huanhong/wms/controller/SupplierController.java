package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Supplier;
import com.huanhong.wms.service.ISupplierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "供应商")
@RestController
@RequestMapping("/v1/supplier")
public class SupplierController extends BaseController {

    @Resource
    private ISupplierService supplierService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询供应商", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<Supplier>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam Map<String, Object> search) {
        QueryWrapper<Supplier> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                 query.and(qw -> qw.like("name", text));
            }
        }
            return Result.success(supplierService.page(new Page<>(current, size), query));
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加供应商", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody Supplier supplier) {
            return render(supplierService.save(supplier));
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新供应商", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody Supplier supplier) {
              return render(supplierService.updateById(supplier));
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除供应商", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(supplierService.removeById(id));
        }


}

