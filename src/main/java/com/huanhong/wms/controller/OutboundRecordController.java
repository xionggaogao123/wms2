package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.vo.OutboundRecordVO;
import com.huanhong.wms.mapper.OutboundRecordMapper;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IMaterialService;
import com.huanhong.wms.service.IOutboundRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
@Slf4j
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

    @Resource
    private IInventoryInformationService inventoryInformationService;

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
    public Result add(@Valid @RequestBody AddOutboundRecordDTO addOutboundRecordDTO) {

        try {

            /**
             * 判断是否重复添加
             */
            OutboundRecord outboundRecord = outboundRecordService.getOutboundRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(addOutboundRecordDTO.getDocumentNumber(), addOutboundRecordDTO.getCargoSpaceId(), addOutboundRecordDTO.getMaterialCoding(), addOutboundRecordDTO.getBatch());

            if (ObjectUtil.isNotNull(outboundRecord)) {
                return Result.failure("数据已存在无法继续新增,请使用更新功能！");
            }

            /**
             * 如果用户手动新增出库记录，先判断库存是否满足
             */
            String materialCoding = addOutboundRecordDTO.getMaterialCoding();
            String batch = addOutboundRecordDTO.getBatch();
            String cargoSpaceId = addOutboundRecordDTO.getCargoSpaceId();
            InventoryInformation inventoryInformation = inventoryInformationService.getInventoryInformation(materialCoding, batch, cargoSpaceId);
            if (ObjectUtil.isNull(inventoryInformation)) {
                return Result.failure("库存不存在");
            }
            BigDecimal inventroyNum = BigDecimal.valueOf(inventoryInformation.getInventoryCredit());
            BigDecimal recordNum = BigDecimal.valueOf(addOutboundRecordDTO.getOutQuantity());
            int event = inventroyNum.compareTo(recordNum);
            //如果库存量小于出库数量则不能出库
            if (event < 0) {
                return Result.failure("库存量不足！无法出库！");
                //如果库存量等于出库数量或大于出库数量
            } else {
                /**
                 * 先扣库存
                 */
                BigDecimal newInventory = inventroyNum.subtract(recordNum);
                UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);
                updateInventoryInformationDTO.setInventoryCredit(newInventory.doubleValue());
                Result resultUpdateInventory = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                if (resultUpdateInventory.isOk()) {
                    //更新库存成功,新增库存记录
                    //出库类型：1-领料出库 2-调拨出库
                    addOutboundRecordDTO.setOutType(1);
                    Result resultAddOutRecord = outboundRecordService.addOutboundRecord(addOutboundRecordDTO);
                    if (!resultAddOutRecord.isOk()) {
                        return Result.failure("新增出库记录失败！");
                    } else {
                        return Result.success(resultAddOutRecord);
                    }
                } else {
                    return Result.failure("更新库存失败！");
                }
            }
        } catch (Exception e) {
            log.error("新增出库记录异常", e);
            return Result.failure("系统异常：新增出库记录失败！");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新出库记录", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateOutboundRecordDTO updateOutboundRecordDTO) {

        try {
            //更新出库记录前，先回滚库存
            String materialCoding = updateOutboundRecordDTO.getMaterialCoding();
            String batch = updateOutboundRecordDTO.getBatch();
            String cargoSpaceId = updateOutboundRecordDTO.getCargoSpaceId();
            InventoryInformation inventoryInformation = inventoryInformationService.getInventoryInformation(materialCoding, batch, cargoSpaceId);
            if (ObjectUtil.isNull(inventoryInformation)){
                return Result.failure("未找到库存信息！");
            }

            UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
            BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);

            //获取未更新前得出库数量，与此次更新的对比，更新的出库数量只能小于等于旧出库数量
            OutboundRecord outboundRecordOld = outboundRecordService.getOutboundRecordById(updateOutboundRecordDTO.getId());
            NumberUtil.sub(outboundRecordOld.getOutQuantity(), updateOutboundRecordDTO.getOutQuantity());
            /**
             * 对比此次数量
             * 0 新旧出库数量一致 啥也不干
             * -1 新出库数量小于旧出库数量 库存回滚差值  更新出库记录数量为新数量
             * 1 新出库数量大于旧出库数量  库存数量够则扣库存数量差值 不够则返回提示信息
             */
            int event = NumberUtil.compare(updateOutboundRecordDTO.getOutQuantity(), outboundRecordOld.getOutQuantity());
            if (event == 0) {
                Result result = outboundRecordService.updateOutboundRecord(updateOutboundRecordDTO);
                if (!result.isOk()) {
                    return Result.failure("更新出库记录失败！");
                }
            } else if (event < 0) {
                //差值
                Double tempNum = NumberUtil.sub(outboundRecordOld.getOutQuantity(), updateOutboundRecordDTO.getOutQuantity());
                Double newInventoryNum = NumberUtil.add(inventoryInformation.getInventoryCredit(), tempNum);
                updateInventoryInformationDTO.setInventoryCredit(newInventoryNum);
                Result result = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                if (!result.isOk()) {
                    return Result.failure("回滚库存失败！");
                }
                return outboundRecordService.updateOutboundRecord(updateOutboundRecordDTO);
            } else if (event > 0) {
                //更新出库记录前，先回滚库存
                //差值--新出库数量-旧出库数量
                Double tempNum = NumberUtil.sub(updateOutboundRecordDTO.getOutQuantity(), outboundRecordOld.getOutQuantity());
                //库存数量若小于要新扣的差值则返回信息
                if (NumberUtil.compare(inventoryInformation.getInventoryCredit(), tempNum) < 0) {
                    return Result.failure("库存数量不足！");
                }
                //若库存数量大于等于要扣的差值，则更新库存
                updateInventoryInformationDTO.setInventoryCredit(NumberUtil.sub(inventoryInformation.getInventoryCredit(), tempNum));
                Result result = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                if (!result.isOk()) {
                    return Result.failure("减库存失败！");
                } else {
                    return outboundRecordService.updateOutboundRecord(updateOutboundRecordDTO);
                }
            }
            return Result.failure("未知错误！");
        } catch (Exception e) {
         log.error("更新失败！系统异常",e);
         return Result.failure("更新失败！系统异常");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除出库记录", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //删除出库记录前,回滚库存
        OutboundRecord outboundRecord = outboundRecordService.getOutboundRecordById(id);
        if (ObjectUtil.isNull(outboundRecord)) {
            return Result.failure("出库记录不存在！");
        }
        String materialCoding = outboundRecord.getMaterialCoding();
        String batch = outboundRecord.getBatch();
        String cargoSpace = outboundRecord.getCargoSpaceId();
        InventoryInformation inventoryInformation = inventoryInformationService.getInventoryInformation(materialCoding, batch, cargoSpace);
        if (ObjectUtil.isNull(inventoryInformation)) {
            return Result.failure("对应库存信息不存在！");
        }
        UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
        BeanUtil.copyProperties(inventoryInformation, updateInventoryInformationDTO);
        //新库存数量=库存数量+被删除的出库数量
        Double newInventory = NumberUtil.add(inventoryInformation.getInventoryCredit(), outboundRecord.getOutQuantity());
        updateInventoryInformationDTO.setInventoryCredit(newInventory);
        Result result = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
        if (!result.isOk()) {
            return Result.failure("回滚库存失败!");
        }
        int i = outboundRecordMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "通过ID获取记录详情")
    @GetMapping("/getOutboundRecordById")
    public Result getOutboundRecordById(@RequestParam Integer id) {
        //     Double newInventory = NumberUtil.add(inventoryInformation.getInventoryCredit(), updateOutboundRecordDTO.getOutQuantity());
        OutboundRecord outboundRecord = outboundRecordService.getOutboundRecordById(id);
        return ObjectUtil.isNotNull(outboundRecord) ? Result.success(outboundRecord) : Result.failure("未查询到相关信息");

    }
}

