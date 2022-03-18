package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.RequiremetsPlanningDetails;
import com.huanhong.wms.entity.dto.AddRequiremetsPlanningDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateRequiremetsPlanningDetailsDTO;
import com.huanhong.wms.mapper.RequiremetsPlanningDetailsMapper;
import com.huanhong.wms.service.IRequiremetsPlanningDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1//requiremets-planning-details")
    @ApiSort()
    @Api(tags = "需求计划明细表")
    public class RequiremetsPlanningDetailsController extends BaseController {

    @Resource
    private IRequiremetsPlanningDetailsService requiremetsPlanningDetailsService;



    @Resource
    private RequiremetsPlanningDetailsMapper requiremetsPlanningDetailsMapper;

//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "current", value = "当前页码"),
//        @ApiImplicitParam(name = "size", value = "每页行数"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询需求计划明细表", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<RequiremetsPlanningDetails>> page(@RequestParam(defaultValue = "1") Integer current,
//                                                         @RequestParam(defaultValue = "10") Integer size
//                                                        ) {
//        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加需求计划明细表", notes = "生成代码")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody List<AddRequiremetsPlanningDetailsDTO> addRequiremetsPlanningDetailsDTOList) {
            return requiremetsPlanningDetailsService.addRequiremetsPlanningDetails(addRequiremetsPlanningDetailsDTOList);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新需求计划明细表", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody List<UpdateRequiremetsPlanningDetailsDTO> updateRequiremetsPlanningDetailsDTOList) {
            return requiremetsPlanningDetailsService.updateRequiremetsPlanningDetails(updateRequiremetsPlanningDetailsDTOList);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除需求计划明细表", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            int i = requiremetsPlanningDetailsMapper.deleteById(id);
            return render(i > 0);
        }

        @ApiOperationSupport(order = 5)
        @ApiOperation(value="通过id获取明细信息")
        @GetMapping("/getRequiremetsPlanningDetailsById")
        public Result getRequiremetsPlanningDetailsById(@RequestParam Integer id){
            RequiremetsPlanningDetails requiremetsPlanningDetails = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsById(id);
            return ObjectUtil.isNotEmpty(requiremetsPlanningDetails) ? Result.success(requiremetsPlanningDetails):Result.failure("未找到对应信息！");
        }

//    @ApiOperationSupport(order = 6)
//    @ApiOperation(value="通过需求计划单编号和仓库编号获取明细list")
//    @GetMapping("/getRequiremetsPlanningDetailsByDocNumAndWarehouseId")
//    public Result getRequiremetsPlanningDetailsByDocNumAndWarehouseId(@RequestParam  String docNum,
//                                                  @RequestParam String warehouseId){
//        List<RequiremetsPlanningDetails> requiremetsPlanningDetailsList = requiremetsPlanningDetailsService.getRequiremetsPlanningDetailsByDocNumAndWarehouseId(docNum,warehouseId)
//        return ObjectUtil.isNotEmpty(requiremetsPlanningDetailsList) ? Result.success(requiremetsPlanningDetailsList):Result.failure("未找到对应信息！");
//    }


}

