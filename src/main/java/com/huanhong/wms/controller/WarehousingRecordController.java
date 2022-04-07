package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddWarehousingRecordDTO;
import com.huanhong.wms.entity.dto.UpdateWarehousingRecordDTO;
import com.huanhong.wms.entity.vo.WarehousingRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.WarehousingRecord;
import com.huanhong.wms.mapper.WarehousingRecordMapper;
import com.huanhong.wms.service.IWarehousingRecordService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "")
@Slf4j
@RestController
@RequestMapping("/warehousing-record")
public class WarehousingRecordController extends BaseController {

    @Resource
    private IWarehousingRecordService warehousingRecordService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<WarehousingRecord>> page(@RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                WarehousingRecordVO warehousingRecordVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<WarehousingRecord> pageResult = warehousingRecordService.pageFuzzyQuery(new Page<>(current, size), warehousingRecordVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到入库记录信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加", notes = "生成代码")
        @PostMapping
        public Result add(@Valid @RequestBody AddWarehousingRecordDTO addWarehousingRecordDTO) {
            return warehousingRecordService.addWarehousingRecord(addWarehousingRecordDTO);
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping
        public Result update(@Valid @RequestBody UpdateWarehousingRecordDTO updateWarehousingRecordDTO) {
              return warehousingRecordService.updateWarehousingRecord(updateWarehousingRecordDTO);
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(warehousingRecordService.removeById(id));
        }


        @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "单据Id"),
        })
        @ApiOperationSupport(order = 6)
        @ApiOperation(value = "根据ID获取单据信息")
        @GetMapping("getPlanUseOutById/{id}")
        public Result getPlanUseOutById(@PathVariable Integer id) {
            WarehousingRecord warehousingRecord = warehousingRecordService.getWarehousingRecordById(id);
            return ObjectUtil.isNotNull(warehousingRecord) ? Result.success(warehousingRecord) :  Result.failure("未查询到相关信息");

    }
}

