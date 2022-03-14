package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.vo.OutboundRecordVO;
import com.huanhong.wms.mapper.OutboundRecordMapper;
import com.huanhong.wms.service.IMaterialService;
import com.huanhong.wms.service.IOutboundRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/v1//outbound-record")
@ApiSort()
@Api(tags = "出库记录")
public class OutboundRecordController extends BaseController {

    @Resource
    private IOutboundRecordService outboundRecordService;

    @Resource
    private OutboundRecordMapper outboundRecordMapper;

    @Resource
    private IMaterialService materialService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询出库记录", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<OutboundRecord>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             OutboundRecordVO outboundRecordVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<OutboundRecord> pageResult = outboundRecordService.pageFuzzyQuery(new Page<>(current, size), outboundRecordVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加出库记录", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody List<AddOutboundRecordDTO> addOutboundRecordDTOList) {
        return outboundRecordService.addOutboundRecordList(addOutboundRecordDTOList);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新出库记录", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateOutboundRecordDTO updateOutboundRecordDTO) {
        return outboundRecordService.updateOutboundRecord(updateOutboundRecordDTO);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除出库记录", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = outboundRecordMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "通过ID获取记录详情")
    @GetMapping("/getOutboundRecordById")
    public Result getOutboundRecordById(@RequestParam Integer id){
        OutboundRecord outboundRecord = outboundRecordService.getOutboundRecordById(id);
        return ObjectUtil.isNotNull(outboundRecord) ? Result.success(outboundRecord) :  Result.failure("未查询到相关信息");

    }
}

