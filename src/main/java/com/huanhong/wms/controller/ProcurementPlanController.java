package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcurementPlan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.ProcurementPlanMapper;
import com.huanhong.wms.service.IProcurementPlanService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/v1//procurement-plan")
    @ApiSort()
    @Api(tags = "采购计划主表")
    public class ProcurementPlanController extends BaseController {

    @Resource
    private IProcurementPlanService procurement_planService;
    @Resource
    private ProcurementPlanMapper procurement_planMapper;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询采购计划主表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<ProcurementPlan>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam Map<String, Object> search) {
        QueryWrapper<ProcurementPlan> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                 query.and(qw -> qw.like("title", text).or()
                    .like("user_name", text)
             );
            }
        }
            return Result.success(procurement_planMapper.selectPage(new Page<>(current, size), query));
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加采购计划主表", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody ProcurementPlan procurement_plan) {
            int insert = procurement_planMapper.insert(procurement_plan);
            return render(insert > 0);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新采购计划主表", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody ProcurementPlan procurement_plan) {
             int update = procurement_planMapper.updateById(procurement_plan);
              return render(update > 0);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除采购计划主表", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            int i = procurement_planMapper.deleteById(id);
            return render(i > 0);
        }


}

