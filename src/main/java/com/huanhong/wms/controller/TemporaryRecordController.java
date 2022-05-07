package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.TemporaryLibrary;
import com.huanhong.wms.entity.dto.AddTemporaryRecordDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryRecordDTO;
import com.huanhong.wms.entity.vo.TemporaryRecordVO;
import com.huanhong.wms.service.ITemporaryLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.TemporaryRecord;
import com.huanhong.wms.mapper.TemporaryRecordMapper;
import com.huanhong.wms.service.ITemporaryRecordService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@ApiSort()
@Validated
@Slf4j
@Api(tags = "")
@RestController
@RequestMapping("/v1/temporary-record")
public class TemporaryRecordController extends BaseController {

    @Resource
    private ITemporaryRecordService temporaryRecordService;

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryRecord>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              TemporaryRecordVO temporaryRecordVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryRecord> pageResult = temporaryRecordService.pageFuzzyQuery(new Page<>(current, size), temporaryRecordVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加", notes = "生成代码")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddTemporaryRecordDTO addTemporaryRecordDTO) {

            try {
                /**
                 * 判断是否重复添加
                 */
                TemporaryRecord temporaryRecord = temporaryRecordService.getTemporaryRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(addTemporaryRecordDTO.getDocumentNumber(), addTemporaryRecordDTO.getCargoSpaceId(),addTemporaryRecordDTO.getMaterialCoding(),addTemporaryRecordDTO.getBatch());

                if (ObjectUtil.isNotEmpty(temporaryRecord)) {
                    return Result.failure("数据已存在无法继续新增,请使用更新功能！");
                }

                /**
                 * 如果用户手动新增出库记录，先判断库存是否满足
                 */
                String materialCoding = addTemporaryRecordDTO.getMaterialCoding();
                String batch = addTemporaryRecordDTO.getBatch();
                String cargoSpaceId = addTemporaryRecordDTO.getCargoSpaceId();

                TemporaryLibrary temporaryLibrary = temporaryLibraryService.getTemporaryLibrary(materialCoding,batch,cargoSpaceId);

                if (ObjectUtil.isNull(temporaryLibrary)) {
                    return Result.failure("库存不存在");
                }

                BigDecimal inventroyNum = BigDecimal.valueOf(temporaryLibrary.getInventoryCredit());
                BigDecimal recordNum = BigDecimal.valueOf(addTemporaryRecordDTO.getOutQuantity());

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
                    UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO = new UpdateTemporaryLibraryDTO();
                    BeanUtil.copyProperties(temporaryLibrary, updateTemporaryLibraryDTO);
                    updateTemporaryLibraryDTO.setInventoryCredit(newInventory.doubleValue());
                    Result resultUpdateInventory = temporaryLibraryService.updateTemporaryLibrary(updateTemporaryLibraryDTO);
                    if (resultUpdateInventory.isOk()) {
                        //记录类型：1-临时库入库 2-临时库出库
                        addTemporaryRecordDTO.setRecordType(2);
                        Result resultAddTemporaryRecord = temporaryRecordService.addTemporaryRecord(addTemporaryRecordDTO);
                        if (!resultAddTemporaryRecord.isOk()) {
                            return Result.failure("新增出库记录失败！");
                        } else {
                            return Result.success(resultAddTemporaryRecord);
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
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateTemporaryRecordDTO updateTemporaryRecordDTO) {
            try {
                //更新出库记录前，先回滚库存
                String materialCoding = updateTemporaryRecordDTO.getMaterialCoding();
                String batch = updateTemporaryRecordDTO.getBatch();
                String cargoSpaceId = updateTemporaryRecordDTO.getCargoSpaceId();
                TemporaryLibrary temporaryLibrary = temporaryLibraryService.getTemporaryLibrary(materialCoding, batch, cargoSpaceId);
                if (ObjectUtil.isNull(temporaryLibrary )){
                    return Result.failure("未找到库存信息！");
                }

                UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO = new UpdateTemporaryLibraryDTO();
                BeanUtil.copyProperties(temporaryLibrary, updateTemporaryLibraryDTO);

                //获取未更新前得出库数量，与此次更新的对比，更新的出库数量只能小于等于旧出库数量
                TemporaryRecord temporaryRecordOld = temporaryRecordService.getTemporaryRecordById(updateTemporaryLibraryDTO.getId());
                NumberUtil.sub(temporaryRecordOld.getOutQuantity(), updateTemporaryRecordDTO.getOutQuantity());
                /**
                 * 对比此次数量
                 * 0 新旧出库数量一致 啥也不干
                 * -1 新出库数量小于旧出库数量 库存回滚差值  更新出库记录数量为新数量
                 * 1 新出库数量大于旧出库数量  库存数量够则扣库存数量差值 不够则返回提示信息
                 */
                int event = NumberUtil.compare(updateTemporaryRecordDTO.getOutQuantity(), temporaryRecordOld.getOutQuantity());

                if (event == 0) {
                    Result result = temporaryRecordService.updateTemporaryRecord(updateTemporaryRecordDTO);
                    if (!result.isOk()) {
                        return Result.failure("更新出库记录失败！");
                    }else {
                        return result;
                    }
                } else if (event < 0) {
                    //差值
                    Double tempNum = NumberUtil.sub(temporaryRecordOld.getOutQuantity(), updateTemporaryRecordDTO.getOutQuantity());
                    Double newInventoryNum = NumberUtil.add(temporaryLibrary.getInventoryCredit(), tempNum);
                    updateTemporaryLibraryDTO.setInventoryCredit(newInventoryNum);
                    Result result = temporaryLibraryService.updateTemporaryLibrary(updateTemporaryLibraryDTO);
                    if (!result.isOk()) {
                        return Result.failure("回滚库存失败！");
                    }
                    return temporaryRecordService.updateTemporaryRecord(updateTemporaryRecordDTO);
                } else {
                    //更新出库记录前，先回滚库存
                    //差值--新出库数量-旧出库数量
                    Double tempNum = NumberUtil.sub(updateTemporaryRecordDTO.getOutQuantity(), temporaryRecordOld.getOutQuantity());
                    //库存数量若小于要新扣的差值则返回信息
                    if (NumberUtil.compare(temporaryLibrary.getInventoryCredit(), tempNum) < 0) {
                        return Result.failure("库存数量不足！");
                    }
                    //若库存数量大于等于要扣的差值，则更新库存
                    updateTemporaryLibraryDTO.setInventoryCredit(NumberUtil.sub(temporaryLibrary.getInventoryCredit(), tempNum));
                    Result result = temporaryLibraryService.updateTemporaryLibrary(updateTemporaryLibraryDTO);
                    if (!result.isOk()) {
                        return Result.failure("减库存失败！");
                    } else {
                        return temporaryRecordService.updateTemporaryRecord(updateTemporaryRecordDTO);
                    }
                }
            } catch (Exception e) {
                log.error("更新失败！系统异常",e);
                return Result.failure("更新失败！系统异常");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            return render(temporaryRecordService.removeById(id));
        }


}

