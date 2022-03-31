package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddAllocationEnterDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationEnterDetailsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.AllocationEnterDetails;
import com.huanhong.wms.mapper.AllocationEnterDetailsMapper;
import com.huanhong.wms.service.IAllocationEnterDetailsService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "调拨入库明细")
@RestController
@RequestMapping("/allocation-enter-details")
public class AllocationEnterDetailsController extends BaseController {

    @Resource
    private IAllocationEnterDetailsService allocationEnterDetailsService;

//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "current", value = "当前页码"),
//        @ApiImplicitParam(name = "size", value = "每页行数"),
//        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<AllocationEnterDetails>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
//                                           @RequestParam Map<String, Object> search) {
//        QueryWrapper<AllocationEnterDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                 query.and(qw -> qw.like("title", text).or()
//                    .like("user_name", text)
//             );
//            }
//        }
//            return Result.success(allocationEnterDetailsService.page(new Page<>(current, size), query));
//        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList) {
            return allocationEnterDetailsService.addAllocationEnterDetails(addAllocationEnterDetailsDTOList);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody List<UpdateAllocationEnterDetailsDTO> updateAllocationEnterDetailsDTOList) {
              return allocationEnterDetailsService.updateAllocationEnterDetails(updateAllocationEnterDetailsDTOList);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("delete/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(allocationEnterDetailsService.removeById(id));
        }


}

