package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddMakeInventoryDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDetailsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.MakeInventoryDetails;
import com.huanhong.wms.mapper.MakeInventoryDetailsMapper;
import com.huanhong.wms.service.IMakeInventoryDetailsService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "盘点单明细")
@RestController
@RequestMapping("/v1/make-inventory-details")
public class MakeInventoryDetailsController extends BaseController {

    @Resource
    private IMakeInventoryDetailsService makeInventoryDetailsService;

//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "current", value = "当前页码"),
//        @ApiImplicitParam(name = "size", value = "每页行数"),
//        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<MakeInventoryDetails>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
//                                           @RequestParam Map<String, Object> search) {
//        QueryWrapper<MakeInventoryDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                 query.and(qw -> qw.like("title", text).or()
//                    .like("user_name", text)
//             );
//            }
//        }
//            return Result.success(makeInventoryDetailsService.page(new Page<>(current, size), query));
//        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加", notes = "生成代码")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList) {
            return makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody List<UpdateMakeInventoryDetailsDTO> updateMakeInventoryDetailsDTOList) {
              return makeInventoryDetailsService.updateMakeInventoryDetails(updateMakeInventoryDetailsDTOList);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("delete/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(makeInventoryDetailsService.removeById(id));
        }

}

