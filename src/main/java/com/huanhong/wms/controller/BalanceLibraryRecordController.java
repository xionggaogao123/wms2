package com.huanhong.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.BalanceLibraryRecord;
import com.huanhong.wms.service.IBalanceLibraryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "平衡利库记录")
@RestController
@RequestMapping("/v1/balance-library-record")
public class BalanceLibraryRecordController extends BaseController {

    @Resource
    private IBalanceLibraryRecordService balanceLibraryRecordService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询平衡利库记录", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<BalanceLibraryRecord>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam Map<String, Object> search) {
        QueryWrapper<BalanceLibraryRecord> query = new QueryWrapper<>();
        query.orderByDesc("id");
            return Result.success(balanceLibraryRecordService.page(new Page<>(current, size), query));
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加平衡利库记录")
        @PostMapping
        public Result add(@Valid @RequestBody BalanceLibraryRecord balanceLibraryRecord) {
            return balanceLibraryRecordService.add(balanceLibraryRecord);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新平衡利库记录", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody BalanceLibraryRecord balanceLibraryRecord) {
              return render(balanceLibraryRecordService.updateById(balanceLibraryRecord));
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除平衡利库记录", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(balanceLibraryRecordService.removeById(id));
        }


}

