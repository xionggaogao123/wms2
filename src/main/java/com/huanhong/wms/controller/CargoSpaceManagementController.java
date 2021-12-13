package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.CargoSpaceManagement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.CargoSpaceManagementMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/cargo-space-management")
@ApiSort()
@Api(tags = "货位管理")
public class CargoSpaceManagementController extends BaseController {

    @Resource
    private ICargoSpaceManagementService cargo_space_managementService;
    @Resource
    private CargoSpaceManagementMapper cargo_space_managementMapper;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询货位管理", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<CargoSpaceManagement>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam Map<String, Object> search) {
        QueryWrapper<CargoSpaceManagement> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                 query.and(qw -> qw.like("title", text).or()
                    .like("user_name", text)
             );
            }
        }
            return Result.success(cargo_space_managementMapper.selectPage(new Page<>(current, size), query));
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加货位管理", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody CargoSpaceManagement cargo_space_management) {
            int insert = cargo_space_managementMapper.insert(cargo_space_management);
            return render(insert > 0);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新货位管理", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody CargoSpaceManagement cargo_space_management) {
             int update = cargo_space_managementMapper.updateById(cargo_space_management);
              return render(update > 0);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除货位管理", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            int i = cargo_space_managementMapper.deleteById(id);
            return render(i > 0);
        }


}

