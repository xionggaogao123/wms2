package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.ShelfManagement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.ShelfManagementMapper;
import com.huanhong.wms.service.IShelfManagementService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/shelf-management")
@ApiSort()
@Api(tags = "货架管理")
public class ShelfManagementController extends BaseController {

    @Resource
    private IShelfManagementService shelf_managementService;

    @Resource
    private ShelfManagementMapper shelf_managementMapper;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(SublibraryManagementController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询货架管理", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<ShelfManagement>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam Map<String, Object> search) {
        QueryWrapper<ShelfManagement> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("title", text).or()
                        .like("user_name", text)
                );
            }
        }
        return Result.success(shelf_managementMapper.selectPage(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加货架管理", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody ShelfManagement shelf_management) {
        int insert = shelf_managementMapper.insert(shelf_management);
        return render(insert > 0);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新货架管理", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody ShelfManagement shelf_management) {
        int update = shelf_managementMapper.updateById(shelf_management);
        return render(update > 0);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除货架管理", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = shelf_managementMapper.deleteById(id);
        return render(i > 0);
    }


}

