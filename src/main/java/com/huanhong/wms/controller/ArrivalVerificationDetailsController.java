package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddArrivalVerificationDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateArrivalVerificationDetailsDTO;
import com.huanhong.wms.service.IArrivalVerificationDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@ApiSort()
@Api(tags = "到货检验明细表")
@RestController
@RequestMapping("/v1//arrival-verification-details")
public class ArrivalVerificationDetailsController extends BaseController {

    @Resource
    private IArrivalVerificationDetailsService arrivalVerificationDetailsService;

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "current", value = "当前页码"),
//            @ApiImplicitParam(name = "size", value = "每页行数")
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询到货检验明细表", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<ArrivalVerificationDetails>> page(@RequestParam(defaultValue = "1") Integer current,
//                                                         @RequestParam(defaultValue = "10") Integer size
//    ) {
//        QueryWrapper<ArrivalVerificationDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                query.and(qw -> qw.like("title", text).or()
//                        .like("user_name", text)
//                );
//            }
//        }
//        return Result.success(arrivalVerificationDetailsService.page(new Page<>(current, size), query));
//    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加到货检验明细表")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody List<AddArrivalVerificationDetailsDTO> arrivalVerificationDetailsDTOList) {
        return arrivalVerificationDetailsService.addArrivalVerificationDetails(arrivalVerificationDetailsDTOList);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新到货检验明细表", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody List<UpdateArrivalVerificationDetailsDTO> updateArrivalVerificationDetailsDTOList) {
        return arrivalVerificationDetailsService.updateArrivalVerificationDetails(updateArrivalVerificationDetailsDTOList);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除到货检验明细表", notes = "生成代码")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(arrivalVerificationDetailsService.removeById(id));
    }

}

