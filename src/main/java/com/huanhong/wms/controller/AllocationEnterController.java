package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationEnterDetails;
import com.huanhong.wms.entity.AllocationOut;
import com.huanhong.wms.entity.AllocationOutDetails;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.AllocationEnterVO;
import com.huanhong.wms.service.IAllocationEnterDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.AllocationEnter;
import com.huanhong.wms.mapper.AllocationEnterMapper;
import com.huanhong.wms.service.IAllocationEnterService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "调拨入库")
@Slf4j
@RestController
@RequestMapping("/allocation-enter")
public class AllocationEnterController extends BaseController {

    @Resource
    private IAllocationEnterService allocationEnterService;

    @Resource
    private IAllocationEnterDetailsService allocationEnterDetailsService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<AllocationEnter>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              AllocationEnterVO allocationEnterVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<AllocationEnter> pageResult = allocationEnterService.pageFuzzyQuery(new Page<>(current, size), allocationEnterVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到调拨入库单信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddAllocationEnterAndDetailsDTO addAllocationEnterAndDetailsDTO) {
        try {
            AddAllocationEnterDTO addAllocationEnterDTO = addAllocationEnterAndDetailsDTO.getAddAllocationEnterDTO();
            List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList = addAllocationEnterAndDetailsDTO.getAddAllocationEnterDetailsDTOList();
            Result result = allocationEnterService.addAllocationEnterDTO(addAllocationEnterDTO);
            if (!result.isOk()) {
                return Result.failure("新增调拨入库失败！");
            }
            AllocationEnter allocationEnter = (AllocationEnter) result.getData();
            String docNum = allocationEnter.getAllocationEnterNumber();
            for (AddAllocationEnterDetailsDTO addAllocationEnterDetailsDTO : addAllocationEnterDetailsDTOList) {
                addAllocationEnterDetailsDTO.setAllocationEnterNumber(docNum);
            }
            return allocationEnterDetailsService.addAllocationEnterDetails(addAllocationEnterDetailsDTOList);
        } catch (Exception e) {
            log.error("新增调拨出库失败");
            return Result.failure("系统异常，新增调拨入库失败！");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateAllocationEnterAndDetailsDTO updateAllocationEnterAndDetailsDTO) {
        try {
            UpdateAllocationEnterDTO updateAllocationEnterDTO = updateAllocationEnterAndDetailsDTO.getUpdateAllocationEnterDTO();
            List<UpdateAllocationEnterDetailsDTO> updateAllocationEnterDetailsDTOList = updateAllocationEnterAndDetailsDTO.getUpdateAllocationEnterDetailsDTOList();
            Result result = allocationEnterService.update(updateAllocationEnterDTO);
            if (!result.isOk()) {
                return Result.failure("更新调拨入库失败！");
            }
            return allocationEnterDetailsService.updateAllocationEnterDetails(updateAllocationEnterDetailsDTOList);
        } catch (Exception e) {
            log.error("更新调拨入库失败");
            return Result.failure("系统异常：更新调拨入库失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(allocationEnterService.removeById(id));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取调拨入库及其明细")
    @GetMapping("getAllocationEnterAndDetailsById/{id}")
    public Result getAllocationEnterAndDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        AllocationEnter allocationEnter = allocationEnterService.getAllocationEnterById(id);
        if (ObjectUtil.isEmpty(allocationEnter)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationEnterDetails> allocationEnterDetails = allocationEnterDetailsService.getAllocationEnterDetailsListByDocNum(allocationEnter.getAllocationEnterNumber());
        jsonObject.put("doc", allocationEnter);
        jsonObject.put("details", allocationEnterDetails);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号获取调拨入库及其明细")
    @GetMapping("/getAllocationOutAndDetailsByDocNum")
    public Result getAllocationOutAndDetailsByDocNum(@RequestParam String docNum) {
        JSONObject jsonObject = new JSONObject();
        AllocationEnter allocationEnter = allocationEnterService.getAllocationEnterByDocNumber(docNum);
        if (ObjectUtil.isEmpty(allocationEnter)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationEnterDetails> allocationEnterDetails = allocationEnterDetailsService.getAllocationEnterDetailsListByDocNum(allocationEnter.getAllocationEnterNumber());
        jsonObject.put("doc", allocationEnter);
        jsonObject.put("details", allocationEnterDetails);
        return Result.success(jsonObject);
    }

}

