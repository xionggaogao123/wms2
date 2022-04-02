package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.mapper.InventoryDocumentDetailsMapper;
import com.huanhong.wms.service.IInventoryDocumentDetailsService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "清点单明细管理")
@RestController
@RequestMapping("/inventory-document-details")
public class InventoryDocumentDetailsController extends BaseController {

    @Resource
    private IInventoryDocumentDetailsService inventoryDocumentDetailsService;

//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "current", value = "当前页码"),
//        @ApiImplicitParam(name = "size", value = "每页行数"),
//        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询清点单", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<InventoryDocumentDetails>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
//                                           @RequestParam Map<String, Object> search) {
//        QueryWrapper<InventoryDocumentDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                 query.and(qw -> qw.like("title", text).or()
//                    .like("user_name", text)
//             );
//            }
//        }
//            return Result.success(inventoryDocumentDetailsService.page(new Page<>(current, size), query));
//        }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加清点单")
    @PostMapping
    public Result add(@Valid @RequestBody List<AddInventoryDocumentDetailsDTO> addInventoryDocumentDetailsDTOList) {
        return inventoryDocumentDetailsService.addInventoryDocumentDetailsLis(addInventoryDocumentDetailsDTOList);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新清点单", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody List<UpdateInventoryDocumentDetailsDTO> updateInventoryDocumentDetailsDTOList) {
        return inventoryDocumentDetailsService.updateInventoryDocumentDetailsList(updateInventoryDocumentDetailsDTOList);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除清点单", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(inventoryDocumentDetailsService.removeById(id));
    }


}

