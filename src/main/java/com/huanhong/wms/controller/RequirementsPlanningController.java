package com.huanhong.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.RequirementsPlanningMapper;
import com.huanhong.wms.service.IRequirementsPlanningService;

@RestController
@RequestMapping("/v1//requirements-planning")
    @ApiSort()
    @Api(tags = "需求计划表")
    public class RequirementsPlanningController extends BaseController {

    @Resource
    private IRequirementsPlanningService requirements_planningService;
    @Resource
    private RequirementsPlanningMapper requirements_planningMapper;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
        @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询需求计划表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<RequirementsPlanning>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam Map<String, Object> search) {
        QueryWrapper<RequirementsPlanning> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                 query.and(qw -> qw.like("title", text).or()
                    .like("user_name", text)
             );
            }
        }
            return Result.success(requirements_planningMapper.selectPage(new Page<>(current, size), query));
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加需求计划表", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody RequirementsPlanning requirements_planning) {
            int insert = requirements_planningMapper.insert(requirements_planning);
            return render(insert > 0);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新需求计划表", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody RequirementsPlanning requirements_planning) {
             int update = requirements_planningMapper.updateById(requirements_planning);
              return render(update > 0);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除需求计划表", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            int i = requirements_planningMapper.deleteById(id);
            return render(i > 0);
        }


}

