package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.BalanceLibraryDetail;
import com.huanhong.wms.mapper.BalanceLibraryDetailMapper;
import com.huanhong.wms.service.IBalanceLibraryDetailService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "平衡利库明细")
@RestController
@RequestMapping("/v1/balance-library-detail")
public class BalanceLibraryDetailController extends BaseController {

    @Resource
    private IBalanceLibraryDetailService balanceLibraryDetailService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询平衡利库明细", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<BalanceLibraryDetail>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam Map<String, Object> search) {
        QueryWrapper<BalanceLibraryDetail> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                 query.and(qw -> qw.like("title", text).or()
                    .like("user_name", text)
             );
            }
        }
            return Result.success(balanceLibraryDetailService.page(new Page<>(current, size), query));
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加平衡利库明细", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody BalanceLibraryDetail balanceLibraryDetail) {
            return render(balanceLibraryDetailService.save(balanceLibraryDetail));
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新平衡利库明细", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody BalanceLibraryDetail balanceLibraryDetail) {
              return render(balanceLibraryDetailService.updateById(balanceLibraryDetail));
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除平衡利库明细", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(balanceLibraryDetailService.removeById(id));
        }

        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "生成采购计划")
        @PostMapping("/createProcurementPlan")
        public Result createProcurementPlan(@Valid @RequestBody BalanceLibraryDetail balanceLibraryDetail) {
            return balanceLibraryDetailService.createProcurementPlan(balanceLibraryDetail);
        }
}

