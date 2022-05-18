package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.AllocationEnterVO;
import com.huanhong.wms.service.IAllocationEnterDetailsService;
import com.huanhong.wms.service.IAllocationEnterService;
import com.huanhong.wms.service.IAllocationOutDetailsService;
import com.huanhong.wms.service.IAllocationOutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@ApiSort()
@Api(tags = "调拨入库")
@Slf4j
@RestController
@RequestMapping("/v1/allocation-enter")
public class AllocationEnterController extends BaseController {

    @Resource
    private IAllocationEnterService allocationEnterService;

    @Resource
    private IAllocationEnterDetailsService allocationEnterDetailsService;

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
            return allocationEnterService.add(addAllocationEnterAndDetailsDTO);

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
            /**
             * 判断
             */

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
       AllocationEnter allocationEnter = allocationEnterService.getAllocationEnterById(id);
       if (ObjectUtil.isNull(allocationEnter)){
           return Result.failure("单据不存在！");
       }
       Boolean delete = allocationEnterService.removeById(id);
       //主表删除成功,删除明细
       if (delete){
           String docNum = allocationEnter.getAllocationEnterNumber();
           List<AllocationEnterDetails> allocationEnterDetailsList = allocationEnterDetailsService.getAllocationEnterDetailsListByDocNum(docNum);
           for (AllocationEnterDetails allocationEnterDetails:allocationEnterDetailsList
                ) {
                allocationEnterDetailsService.removeById(allocationEnterDetails.getId());
           }
       }
       return Result.success("删除成功");
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


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据调拨出库单生成调拨入库单")
    @PutMapping("allocationOutToAllocationEnter")
    public Result allocationOutToAllocationEnter(@Valid @RequestBody AllocationOut allocationOut){

        LoginUser loginUser = this.getLoginUser();

        AddAllocationEnterAndDetailsDTO addAllocationEnterAndDetailsDTO = new AddAllocationEnterAndDetailsDTO();

        /**
         * 处理主表
         */
        AddAllocationEnterDTO addAllocationEnterDTO = new AddAllocationEnterDTO();

        BeanUtil.copyProperties(allocationOut,addAllocationEnterDTO);

        addAllocationEnterDTO.setLibrarian(loginUser.getLoginName());

        addAllocationEnterDTO.setReceiveCompany(loginUser.getCompanyId().toString());

        addAllocationEnterDTO.setRemark("系统自动生成");

        addAllocationEnterAndDetailsDTO.setAddAllocationEnterDTO(addAllocationEnterDTO);

        /**
         * 处理明细表
         */
        List<AllocationOutDetails> allocationOutDetailsList = allocationOutDetailsService.getAllocationOutDetailsListByDocNum(allocationOut.getAllocationOutNumber());
        List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList = new ArrayList<>();
        for (AllocationOutDetails allocationOutDetails:allocationOutDetailsList
             ) {
            AddAllocationEnterDetailsDTO addAllocationEnterDetailsDTO = new AddAllocationEnterDetailsDTO();
            BeanUtil.copyProperties(allocationOutDetails,addAllocationEnterDetailsDTO);
            //应收数量
            addAllocationEnterDetailsDTO.setCalibrationQuantity(allocationOutDetails.getOutboundQuantity());
            //实收数量
            addAllocationEnterDetailsDTO.setOutboundQuantity((double)0);

            addAllocationEnterDetailsDTO.setRemark("系统自动生成");

            addAllocationEnterDetailsDTOList.add(addAllocationEnterDetailsDTO);
        }
        addAllocationEnterAndDetailsDTO.setAddAllocationEnterDetailsDTOList(addAllocationEnterDetailsDTOList);
        return add(addAllocationEnterAndDetailsDTO);
    }


    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "根据调拨计划单生成调拨入库单")
    @PutMapping("/allocationPlanToAllocationEnter")
    public Result allocationPlanToAllocationEnter(@Valid @RequestBody AllocationPlan allocationPlan){

        return allocationEnterService.allocationPlanToAllocationEnter(allocationPlan);

    }
}

