package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationOutDetails;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.AllocationOutVO;
import com.huanhong.wms.service.IAllocationOutDetailsService;
import com.huanhong.wms.service.IAllocationPlanDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.AllocationOut;
import com.huanhong.wms.mapper.AllocationOutMapper;
import com.huanhong.wms.service.IAllocationOutService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "调拨出库主表")
@Slf4j
@RestController
@RequestMapping("/allocation-out")
public class AllocationOutController extends BaseController {

    @Resource
    private IAllocationOutService allocationOutService;

    @Resource
    private IAllocationOutDetailsService allocationOutDetailsService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<AllocationOut>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            AllocationOutVO allocationOutVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<AllocationOut> pageResult = allocationOutService.pageFuzzyQuery(new Page<>(current, size), allocationOutVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到调拨出库单信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddAllocationOutDTOAndDetails addAllocationOutDTOAndDetails) {
            try {
                AddAllocationOutDTO addAllocationOutDTO = addAllocationOutDTOAndDetails.getAddAllocationOutDTO();
                List<AddAllocationOutDetailsDTO> addAllocationOutDetailsDTOList = addAllocationOutDTOAndDetails.getAddAllocationOutDetailsDTOS();
                Result result = allocationOutService.addAllocationOutDTO(addAllocationOutDTO);
                if (!result.isOk()) {
                    return Result.failure("新增调拨出库失败！");
                }
                AllocationOut allocationOut = (AllocationOut) result.getData();
                String docNum = allocationOut.getAllocationOutNumber();
                for (AddAllocationOutDetailsDTO addAllocationOutDetailsDTO : addAllocationOutDetailsDTOList) {
                    addAllocationOutDetailsDTO.setAllocationOutNumber(allocationOut.getAllocationOutNumber());
                }
                return allocationOutDetailsService.addAllocationOutDetails(addAllocationOutDetailsDTOList);
            } catch (Exception e) {
                log.error("新增调拨出库失败");
                return Result.failure("系统异常，新增调拨出库失败！");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateAllocationOutAndDetailsDTO updateAllocationOutAndDetailsDTO) {
            try {
                UpdateAllocationOutDTO updateAllocationOutDTO = updateAllocationOutAndDetailsDTO.getUpdateAllocationOutDTO();
                List<UpdateAllocationOutDetailsDTO> updateAllocationOutDetailsDTOS = updateAllocationOutAndDetailsDTO.getUpdateAllocationOutDetailsDTOS();
                Result result = allocationOutService.update(updateAllocationOutDTO);
                if (!result.isOk()) {
                    return Result.failure("更新调拨出库失败！");
                }
                return allocationOutDetailsService.updateAllocationOutDetails(updateAllocationOutDetailsDTOS);
            } catch (Exception e) {
                log.error("更新调拨出库失败");
                return Result.failure("系统异常：更新调拨出库失败!");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(allocationOutService.removeById(id));
        }


        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据ID获取调拨出库及其明细")
        @GetMapping("getAllocationOutByAndDetailsById/{id}")
        public Result getAllocationOutByAndDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        AllocationOut allocationOut = allocationOutService.getAllocationOutById(id);
        if (ObjectUtil.isEmpty(allocationOut)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationOutDetails> allocationOutDetailsList = allocationOutDetailsService.getAllocationOutDetailsListByDocNum(allocationOut.getAllocationOutNumber());
        jsonObject.put("doc", allocationOut);
        jsonObject.put("details", allocationOutDetailsList);
        return Result.success(jsonObject);
        }


        @ApiOperationSupport(order = 6)
        @ApiOperation(value = "根据单据编号获取到货检验单及其明细")
        @GetMapping("/getAllocationOutAndDetailsByDocNum")
        public Result getAllocationOutAndDetailsByDocNum(@RequestParam String docNum) {
        JSONObject jsonObject = new JSONObject();
        AllocationOut allocationOut = allocationOutService.getAllocationOutByDocNumber(docNum);
        if (ObjectUtil.isEmpty(allocationOut)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationOutDetails> allocationOutDetailsList = allocationOutDetailsService.getAllocationOutDetailsListByDocNum(allocationOut.getAllocationOutNumber());
        jsonObject.put("doc", allocationOut);
        jsonObject.put("details", allocationOutDetailsList);
        return Result.success(jsonObject);
        }

}

