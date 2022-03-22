package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddProcurementPlanDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateProcurementPlanDetailsDTO;
import com.huanhong.wms.mapper.ProcurementPlanDetailsMapper;
import com.huanhong.wms.service.IProcurementPlanDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1//procurement-plan-details")
@ApiSort()
@Api(tags = "采购计划明细表")
public class ProcurementPlanDetailsController extends BaseController {

    @Resource
    private IProcurementPlanDetailsService procurementPlanDetailsService;
    @Resource
    private ProcurementPlanDetailsMapper procurementPlanDetailsMapper;

//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "current", value = "当前页码"),
//        @ApiImplicitParam(name = "size", value = "每页行数"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询采购计划明细表", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<ProcurementPlanDetails>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
//                                                     @RequestParam Map<String, Object> search) {
//        QueryWrapper<ProcurementPlanDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                 query.and(qw -> qw.like("title", text).or()
//                    .like("user_name", text)
//             );
//            }
//        }
//            return Result.success(procurementPlanDetailsMapper.selectPage(new Page<>(current, size), query));
//        }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加采购计划明细表", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody List<AddProcurementPlanDetailsDTO> addRequiremetsPlanningDetailsDTOList) {
        return procurementPlanDetailsService.addProcurementPlanDetails(addRequiremetsPlanningDetailsDTOList);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新采购计划明细表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody List<UpdateProcurementPlanDetailsDTO> updateProcurementPlanDetailsDTOList) {
        return procurementPlanDetailsService.updateProcurementPlanDetails(updateProcurementPlanDetailsDTOList);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除采购计划明细表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = procurementPlanDetailsMapper.deleteById(id);
        return render(i > 0);
    }
}

