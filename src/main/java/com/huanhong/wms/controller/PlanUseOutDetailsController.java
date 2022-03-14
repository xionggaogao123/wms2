package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddPlanUseOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDetailsDTO;
import com.huanhong.wms.mapper.PlanUseOutDetailsMapper;
import com.huanhong.wms.service.IPlanUseOutDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1//plan-use-out-details")
@ApiSort()
@Api(tags = "领料出库明细表")
public class PlanUseOutDetailsController extends BaseController {

    @Resource
    private IPlanUseOutDetailsService planUseOutDetailsService;
    @Resource
    private PlanUseOutDetailsMapper planUseOutDetailsMapper;

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "current", value = "当前页码"),
//            @ApiImplicitParam(name = "size", value = "每页行数"),
//            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询计划领用明细表", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<PlanUseOutDetails>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
//                                                @RequestParam Map<String, Object> search) {
//        QueryWrapper<PlanUseOutDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                query.and(qw -> qw.like("title", text).or()
//                        .like("user_name", text)
//                );
//            }
//        }
//        return Result.success(plan_use_out_detailsMapper.selectPage(new Page<>(current, size), query));
//    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加领料出库明细表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddPlanUseOutDetailsDTO addPlanUseOutDetailsDTO) {
        try {
            PlanUseOutDetails planUseOutDetails = new PlanUseOutDetails();
            BeanUtil.copyProperties(addPlanUseOutDetailsDTO, planUseOutDetails);
            int insert = planUseOutDetailsMapper.insert(planUseOutDetails);
            return render(insert > 0);
        } catch (Exception e) {
            log.error("添加失败，系统异常：", e);
            return Result.failure("添加失败，系统异常：");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新领料出库明细表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody List<UpdatePlanUseOutDetailsDTO> updatePlanUseOutDetailsDTOList) {
        try {
            return planUseOutDetailsService.updatePlanUseOutDetails(updatePlanUseOutDetailsDTOList);
        } catch (Exception e) {
            log.error("更新失败，异常：", e);
            return Result.failure("更新失败，系统异常！");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除领料出库明细表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = planUseOutDetailsMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取领料出库明细详细信息")
    @GetMapping("/getPlanUseOutDetailsById/{id}")
    public PlanUseOutDetails getplanUseOutDetailsById(@PathVariable Integer id) {
        return planUseOutDetailsService.getPlanUseOutDetailsByDetailsId(id);
    }
}

