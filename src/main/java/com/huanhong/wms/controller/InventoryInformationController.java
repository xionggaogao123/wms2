package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.AddMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.dto.MovingInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IMovingInventoryRecordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/v1/inventory-information")
@ApiSort()
@Api(tags = "库存表")
public class InventoryInformationController extends BaseController {

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private IMovingInventoryRecordsService movingInventoryRecordsService;

    /**
     * 分页查询
     *
     * @param current
     * @param size
     * @param inventoryInformationVO
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询库存表")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<InventoryInformation>> page(@RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   InventoryInformationVO inventoryInformationVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<InventoryInformation> pageResult = inventoryInformationService.pageFuzzyQuery(new Page<>(current, size), inventoryInformationVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到库存信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    /**
     * 库存新增--因入库发生变动
     *
     * @param addInventoryInformationDTO
     * @return
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "库存新增")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddInventoryInformationDTO addInventoryInformationDTO) {
        try {
            return inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
        } catch (Exception e) {
            log.error("库存新增错误--（插入数据）失败,异常：", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
        }
    }


    /**
     * 库存更新
     *
     * @param updateInventoryInformationDTO
     * @return
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "库存更新")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateInventoryInformationDTO updateInventoryInformationDTO) {

        /**
         * 这里的库存更新接口用于非正常状态的库存变动，并没有通过线上出库流程。
         * 1.自然或人为原因导致的物料损毁或变质
         * 2.或因税率浮动导致单价变动等因素
         * 3.通过物料编码和批次以及货位操作
         */
        try {
            return inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
        } catch (Exception e) {
            log.error("库存更新失败，异常：", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：更新失败，请稍后再试或联系管理员");
        }

    }

    /**
     * 根据物料编码和批次下架全部物料
     *
     * @param materialCode
     * @param batch
     * @return
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "物料下架")
    @DeleteMapping("/deleteByMaterialCodeAndBatch/{materialCode}&{batch}")
    public Result delete(@PathVariable("materialCode") String materialCode, @PathVariable("batch") String batch) {
        try {
            QueryWrapper<InventoryInformation> wrapper = new QueryWrapper<>();
            wrapper.eq("material_coding", materialCode);
            wrapper.eq("batch", batch);
            int i = inventoryInformationMapper.delete(wrapper);
            return render(i > 0);
        } catch (Exception e) {
            log.error("物料下架出错--删除失败，异常：", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料下架失败，请稍后再试或联系管理员");
        }
    }
    
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "移动库存")
    @PutMapping("/movingInventory")
    public Result movingInventory(@Valid @RequestBody List<MovingInventoryDTO> movingInventoryDTOList) {
        try {
            List<AddMovingInventoryRecordsDTO> addMovingInventoryRecordsDTOArrayList = new ArrayList<>();
        for (MovingInventoryDTO movingInventoryDTO:movingInventoryDTOList
             ) {
                /**
                 * 判断可移数量和移动数量
                 * 1、可移数量=移动数量 直接更新此条数据货位编码
                 * 2、可移数量>移动数量 原数据更新库存数量为 原数量-移动数量  新增库存数据移动数量+新货位
                 * 3、 可移数量<移动数量 返回错误
                 */
                InventoryInformation preInventory = inventoryInformationService.getInventoryById(movingInventoryDTO.getId());
                BigDecimal preNum = BigDecimal.valueOf(preInventory.getInventoryCredit());
                BigDecimal hindNum = BigDecimal.valueOf(movingInventoryDTO.getHindInventoryCredit());
                int event = preNum.compareTo(hindNum);
                switch (event) {
                    case -1:
                        //移动数量大于可移数量
                        return Result.failure("移动数量不能超出可移数量！");
                    case 0:
                        //移动数量等于可移数量
                        UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                        updateInventoryInformationDTO.setId(movingInventoryDTO.getId());
                        updateInventoryInformationDTO.setCargoSpaceId(movingInventoryDTO.getHindCargoSpaceId());
                        Result resultUpdate = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                        if (!resultUpdate.isOk()){
                            return Result.failure("移库失败：更新原库存失败！");
                        }else {
                            addMovingInventoryRecordsDTOArrayList.add(addMovingInventoryRecords(preInventory,movingInventoryDTO));
                        }
                    case 1:
                        //移动数量<可移数量
                        //更新元数据
                        BigDecimal finalNum = preNum.subtract(hindNum);
                        UpdateInventoryInformationDTO updateInventoryInformationDTOAnother = new UpdateInventoryInformationDTO();
                        updateInventoryInformationDTOAnother.setId(movingInventoryDTO.getId());
                        updateInventoryInformationDTOAnother.setInventoryCredit(finalNum.doubleValue());
                        Result resultUpdateAnother = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTOAnother);
                        if (resultUpdateAnother.isOk()) {
                            //新增数据
                            AddInventoryInformationDTO addInventoryInformationDTO = new AddInventoryInformationDTO();
                            InventoryInformation inventoryInformation = inventoryInformationService.getInventoryById(movingInventoryDTO.getId());
                            BeanUtil.copyProperties(inventoryInformation, addInventoryInformationDTO);
                            //新货位
                            addInventoryInformationDTO.setCargoSpaceId(movingInventoryDTO.getHindCargoSpaceId());
                            //移动数量
                            addInventoryInformationDTO.setInventoryCredit(hindNum.doubleValue());
                            Result resultAdd = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                            if (!resultAdd.isOk()) {
                                return Result.failure("移库失败：新增库存失败！");
                            }else {
                                addMovingInventoryRecordsDTOArrayList.add(addMovingInventoryRecords(preInventory,movingInventoryDTO));
                            }
                        } else {
                            return Result.failure("移库失败：更新原库存失败！");
                        }
                    default:
                        log.error("系统异常：库存移动数量比较出错");
                }
            }
        return movingInventoryRecordsService.addMovingInventoryRecordsList(addMovingInventoryRecordsDTOArrayList);
        }catch (Exception e) {
            log.error("库存更新失败，异常：", e);
            return Result.failure("操作失败----系统异常,请联系管理员。");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据ID获取库存信息")
    @GetMapping("getgetInventoryById/{id}")
    public Result getPlanUseOutById(@PathVariable Integer id) {
        try {
            InventoryInformation inventoryInformation = inventoryInformationService.getInventoryById(id);
            return ObjectUtil.isNotNull(inventoryInformation)? Result.success(inventoryInformation):Result.failure("未查询到相关信息！");
        } catch (Exception e) {
            log.error("查询失败,异常：", e);
            return Result.failure("查询失败，系统异常！");
        }
    }

    private AddMovingInventoryRecordsDTO addMovingInventoryRecords(InventoryInformation preInventory,MovingInventoryDTO movingInventoryDTO){

        /**
         * 新增移库记录
         */
        AddMovingInventoryRecordsDTO addMovingInventoryRecordsDTO = new AddMovingInventoryRecordsDTO();
        //物料编码
        addMovingInventoryRecordsDTO.setMaterialCoding(preInventory.getMaterialCoding());
        //仓库编号
        addMovingInventoryRecordsDTO.setWarehouseId(movingInventoryDTO.getHindCargoSpaceId().substring(0,4));
        //批次
        addMovingInventoryRecordsDTO.setBatch(preInventory.getBatch());
        //原货位
        addMovingInventoryRecordsDTO.setPreCargoSpaceId(preInventory.getCargoSpaceId());
        //新货位
        addMovingInventoryRecordsDTO.setNewCargoSpaceId(movingInventoryDTO.getHindCargoSpaceId());
        //原库存
        addMovingInventoryRecordsDTO.setInventoryCredit(preInventory.getInventoryCredit());
        //新库存
        addMovingInventoryRecordsDTO.setMoveQuantity(movingInventoryDTO.getHindInventoryCredit());

        return addMovingInventoryRecordsDTO;
    }


}

