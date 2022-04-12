package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.dto.AddMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationPlanDetailDTO;
import com.huanhong.wms.entity.dto.UpdateMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.vo.MovingInventoryRecordsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.MovingInventoryRecords;
import com.huanhong.wms.mapper.MovingInventoryRecordsMapper;
import com.huanhong.wms.service.IMovingInventoryRecordsService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Slf4j
@Api(tags = "移动库存记录表")
@RestController
@RequestMapping("/moving-inventory-records")
public class MovingInventoryRecordsController extends BaseController {

    @Resource
    private IMovingInventoryRecordsService movingInventoryRecordsService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询移动库存记录表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<MovingInventoryRecords>> page(@RequestParam(defaultValue = "1") Integer current,
                                                     @RequestParam(defaultValue = "10") Integer size,
                                           MovingInventoryRecordsVO movingInventoryRecordsVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<MovingInventoryRecords> pageResult = movingInventoryRecordsService.pageFuzzyQuery(new Page<>(current, size), movingInventoryRecordsVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到出库单据信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加移动库存记录表", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody List<AddMovingInventoryRecordsDTO> movingInventoryRecordsDTOList) {
            return movingInventoryRecordsService.addMovingInventoryRecordsList(movingInventoryRecordsDTOList);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新移动库存记录表", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody List<UpdateMovingInventoryRecordsDTO> updateMovingInventoryRecordsDTOList) {
              return movingInventoryRecordsService.updateMovingInventoryRecords(updateMovingInventoryRecordsDTOList);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除移动库存记录表", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(movingInventoryRecordsService.removeById(id));
        }

}

