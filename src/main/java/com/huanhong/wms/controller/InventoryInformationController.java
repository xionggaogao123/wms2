package com.huanhong.wms.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.IInventoryInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/v1/inventory-information")
@ApiSort()
@Api(tags = "库存表")
public class InventoryInformationController extends BaseController {

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Autowired
    private JudgeConfig judgeConfig;

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

                /**
                 * 判断货位是否存在
                 */
                CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(addInventoryInformationDTO.getCargoSpaceId());
                if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
                    return Result.failure(ErrorCode.DATA_IS_NULL, "货位不存在！");
                }
                /**
                 * 批次可以从到货检验单获取-暂定
                 */

                /**
                 * 根据货位编码前四位获取当前子库
                 */
                String parentCode = addInventoryInformationDTO.getCargoSpaceId().substring(0, 4);
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.select("priority_storage_location");
                queryWrapper.likeRight("cargo_space_id", parentCode);
                queryWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());
                //将同一库、同一物料所有的推荐存放位置放入list
                List<Map<String,Object>>  maplist = inventoryInformationMapper.selectMaps(queryWrapper);

                InventoryInformation inventoryInformation = new InventoryInformation();
                BeanUtils.copyProperties(addInventoryInformationDTO, inventoryInformation);
                int insert = inventoryInformationMapper.insert(inventoryInformation);
                List<String> listPSL = new ArrayList<>();
                //将本次的货位ID放入推荐存放位置的list中
                List<String> listTemp = new ArrayList<>();
                listPSL.add(inventoryInformation.getCargoSpaceId());
                if (insert > 0) {
                    //遍历maplist将优先存放位置转换为list<String>
                    for (Map map:maplist) {
                        //若优先存放位置不为空，获取值放入listPsl准备查重
                        if (MapUtil.isNotEmpty(map)){
                        String s = map.get("priorityStorageLocation").toString();
                        listTemp =  Arrays.stream(StringUtils.split(s, ",")).map(s1 -> s1.trim()).collect(Collectors.toList());
                        }
                        listPSL = Stream.of(listPSL,listTemp)
                                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
                    }

                    //更新同一库同一物料的推荐存放位置
                    UpdateWrapper updateWrapper = new UpdateWrapper();
                    updateWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());
                    updateWrapper.likeRight("cargo_space_id", parentCode);
                    InventoryInformation inventoryInformationUpdate = new InventoryInformation();
                    String[] strings = listPSL.toArray(new String[listPSL.size()]);
                    String resultString = StringUtil.join(strings, ",");
                    inventoryInformationUpdate.setPriorityStorageLocation(resultString);
                    inventoryInformationMapper.update(inventoryInformationUpdate,updateWrapper);
                    return Result.success();
                } else {
                    return Result.failure(ErrorCode.SYSTEM_ERROR, "新增库存失败！");
                }
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
            InventoryInformation inventoryInformation = new InventoryInformation();
            BeanUtils.copyProperties(updateInventoryInformationDTO, inventoryInformation);
            int update = inventoryInformationService.updateInventoryInformation(inventoryInformation);
            return render(update > 0);
        }catch (Exception e){
            log.error("库存更新失败，异常：",e);
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
            log.error("物料下架出错--删除失败，异常：",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料下架失败，请稍后再试或联系管理员");
        }
    }
}

