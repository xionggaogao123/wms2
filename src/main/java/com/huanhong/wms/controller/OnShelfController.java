package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPatch;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.AddOnShelfDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateOnShelfDTO;
import com.huanhong.wms.entity.vo.OnShelfVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.OnShelfMapper;
import com.huanhong.wms.service.*;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.Order;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1//on-shelf")
@ApiSort()
@Api(tags = "上架单据管理")
public class OnShelfController extends BaseController {

    @Resource
    private IOnShelfService onShelfService;

    @Resource
    private OnShelfMapper onShelfMapper;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private IMaterialService materialService;

    @Resource
    private ICargoSpaceManagementService iCargoSpaceManagementService;

    @Resource
    private IShelfManagementService shelfManagementService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<OnShelf>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      OnShelfVO onShelfVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<OnShelf> pageResult = onShelfService.pageFuzzyQuery(new Page<>(current, size), onShelfVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关上架单信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常" + e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddOnShelfDTO addOnShelfDTO) {
        return onShelfService.addOnShelf(addOnShelfDTO);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateOnShelfDTO updateOnShelfDTO) {

        OnShelf onShelfOld = onShelfService.getOnshelfById(updateOnShelfDTO.getId());

        //判断此单据是否已经完成，若完成，则无法继续更新
        if (onShelfOld.getComplete()==1){
            return Result.failure("单据已完成,无法更新！");
        }

        //判断此次更新是否将单据更新为完成，若不是只更新单据
        //若此次更新为完成,更新完成后变动库存信息：临时货位->正式货位
        int doNum = BigDecimal.valueOf(updateOnShelfDTO.getWaitingQuantity()).compareTo(BigDecimal.valueOf(onShelfOld.getWaitingQuantity()));
        if (doNum > 0){
            return Result.failure("新待上数量不能大于旧待上数量！");
        }
        Result result = onShelfService.updateOnshelf(updateOnShelfDTO);
        if (result.isOk()){
            try {

                //获取临时货位的存货数量=可移动数量
                //货位ID= 仓库ID+临时货位
                String cargoSpaceId = updateOnShelfDTO.getWarehouse()+"01AA0000";
                String materialCoding = updateOnShelfDTO.getMaterialCoding();
                String batch = updateOnShelfDTO.getBatch();
                InventoryInformation inventoryInformationOld =  inventoryInformationService.getInventoryInformation(materialCoding,batch,cargoSpaceId);

                if (ObjectUtil.isEmpty(inventoryInformationOld)){
                    UpdateOnShelfDTO updateOnShelfDTORoll = new UpdateOnShelfDTO();
                    updateOnShelfDTORoll.setId(updateOnShelfDTO.getId());
                    updateOnShelfDTORoll.setComplete(0);
                    onShelfService.updateOnshelf(updateOnShelfDTORoll);
                    return Result.failure("未从临时货位找到库存信息！");
                }

                //可移动数量为临时库存的库存数量
                Double PreInventoryCredit = inventoryInformationOld.getInventoryCredit();
                //移动数量为待上数量旧-待上数量新
                Double HindInventoryCredit = NumberUtil.sub(onShelfOld.getWaitingQuantity(),updateOnShelfDTO.getWaitingQuantity());

                /**
                 * 判断可移数量和移动数量
                 * 1、可移数量=移动数量 直接更新此条数据货位编码
                 * 2、可移数量>移动数量 原数据更新库存数量为 原数量-移动数量  新增库存数据移动数量+新货位
                 * 3、 可移数量<移动数量 返回错误
                 */
                //可移动数量
                BigDecimal preNum = BigDecimal.valueOf(PreInventoryCredit);
                //移动数量
                BigDecimal hindNum = BigDecimal.valueOf(HindInventoryCredit);
                int event = preNum.compareTo(hindNum);
                switch (event) {
                    case -1:
                        //移动数量大于可移数量
                        UpdateOnShelfDTO updateOnShelfDTORoll = new UpdateOnShelfDTO();
                        updateOnShelfDTORoll.setId(updateOnShelfDTO.getId());
                        updateOnShelfDTORoll.setComplete(0);
                        onShelfService.updateOnshelf(updateOnShelfDTORoll);
                        return Result.failure("移动数量不能超出可移数量！");
                    case 0:
                        //移动数量等于可移数量,将元库存的数量更新为0，新库存数量更新到新货位
                        UpdateInventoryInformationDTO updateInventoryInformationDTO = new UpdateInventoryInformationDTO();
                        updateInventoryInformationDTO.setId(inventoryInformationOld.getId());
                        updateInventoryInformationDTO.setInventoryCredit(Double.valueOf(0));
                        Result resultZero = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTO);
                        if (resultZero.isOk()){
                            //新增数据
                            AddInventoryInformationDTO addInventoryInformationDTO = new AddInventoryInformationDTO();
                            InventoryInformation inventoryInformation = inventoryInformationService.getInventoryById(inventoryInformationOld.getId());
                            BeanUtil.copyProperties(inventoryInformation, addInventoryInformationDTO);
                            //新货位
                            addInventoryInformationDTO.setCargoSpaceId(updateOnShelfDTO.getCargoSpaceId());
                            //移动数量
                            addInventoryInformationDTO.setInventoryCredit(hindNum.doubleValue());
                            Result resultAdd = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                            if (resultAdd.isOk()) {
                                return Result.success("移动库存成功！");
                            } else {
                                return resultAdd;
                            }
                        }else {
                            return Result.failure("原库存更新失败！");
                        }
                    case 1:
                        //移动数量<可移数量
                        //更新元数据
                        BigDecimal finalNum = preNum.subtract(hindNum);
                        UpdateInventoryInformationDTO updateInventoryInformationDTOAnother = new UpdateInventoryInformationDTO();
                        updateInventoryInformationDTOAnother.setId(inventoryInformationOld.getId());
                        updateInventoryInformationDTOAnother.setInventoryCredit(finalNum.doubleValue());
                        Result resultUpdate = inventoryInformationService.updateInventoryInformation(updateInventoryInformationDTOAnother);
                        //临时货位的原库存数量减去此次移动到正式货位的数量，更新成功后，往新货位上新增库存数据（移动数量）
                        if (resultUpdate.isOk()) {
                            //新增数据
                            AddInventoryInformationDTO addInventoryInformationDTO = new AddInventoryInformationDTO();
                            InventoryInformation inventoryInformation = inventoryInformationService.getInventoryById(inventoryInformationOld.getId());
                            BeanUtil.copyProperties(inventoryInformation, addInventoryInformationDTO);
                            //新货位
                            addInventoryInformationDTO.setCargoSpaceId(updateOnShelfDTO.getCargoSpaceId());
                            //移动数量
                            addInventoryInformationDTO.setInventoryCredit(hindNum.doubleValue());
                            Result resultAdd = inventoryInformationService.addInventoryInformation(addInventoryInformationDTO);
                            if (resultAdd.isOk()) {
                                return Result.success("移动库存成功！");
                            } else {
                                return resultAdd;
                            }
                        } else {
                            return resultUpdate;
                        }
                    default:
                        log.error("系统异常：库存移动数量比较出错");
                }
            } catch (Exception e) {
                log.error("库存更新失败，异常：", e);
                return Result.failure("操作失败----系统异常,请联系管理员。");
            }
        }else {
            return Result.failure("更新上架单失败！");
        }
        return Result.failure("系统异常，未知错误！");
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("deleteById/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = onShelfMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据单据号和仓库编号获取单据信息")
    @GetMapping("getOnshelfByDocNumAndWarehouseId/{docNum}&{warehouseId}")
    public Result getOnshelfByDocNumAndWarehouseId(@PathVariable String docNum,
                                                    @PathVariable String warehouseId) {
        OnShelf onShelf = onShelfService.getOnshelfByDocNumAndWarehouseId(docNum, warehouseId);
        return ObjectUtil.isNotNull(onShelf) ? Result.success(onShelf): Result.failure("未查询到相关数据");
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据ID获取单据信息")
    @GetMapping("getOnshelfById/{id}")
    public Result getOnshelfById(@PathVariable Integer id){
        OnShelf onShelf = onShelfService.getOnshelfById(id);
        return ObjectUtil.isNotNull(onShelf) ? Result.success(onShelf): Result.failure("未查询到相关数据");
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "PDA根据ID获取单据信息及物料详情")
    @GetMapping("getOnshelfIncludeMaterialById/{id}")
    public Result getOnshelfIncludeMaterialById(@PathVariable Integer id){
        JSONObject jsonObject = new JSONObject();
        OnShelf onShelf = onShelfService.getOnshelfById(id);
        if (ObjectUtil.isEmpty(onShelf)) {
            return Result.failure("未找到对应信息！");
        }
        Material material = materialService.getMeterialByMeterialCode(onShelf.getMaterialCoding());
        jsonObject.put("doc", onShelf);
        jsonObject.put("material", material);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据仓库编号获取未完成的上架单数量")
    @GetMapping("getCountByWarehouseId/{warehouseId}")
    public Result getCountByWarehouseId(@PathVariable String warehouseId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("warehouse",warehouseId);
        queryWrapper.eq("complete",0);
        Integer count =  onShelfMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据") ;
    }

    /**
    * 上架单新增 提供一个通过物料编码 返回历史货位 没有此编码的推荐货位 从小类开始推目前使用的 一个货架的范围
    */
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "根据物料编码返回推荐或邻近货位")
    @GetMapping("/getCargoSpaceByMaterialCoding")
    public Result getCargoSpaceByMaterialCoding(@RequestParam String materialCoding,
                                                @RequestParam String warehouseId
                                                ){
        //根据物料编码查询货位
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("priority_storage_location");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.likeRight("cargo_space_id",warehouseId);
        queryWrapper.last("limit 1");
        InventoryInformation inventoryInformation = inventoryInformationMapper.selectOne(queryWrapper);
        JSONObject jsonObjectResult = new JSONObject();
        //判断历史货位是否为空
        if (ObjectUtil.isAllEmpty(inventoryInformation)){
            //判断历史货位是否为空，推荐物料分类小类的同货架的邻近货位
            String materialType = materialCoding.substring(0,6);
            QueryWrapper<InventoryInformation> wrapper = new QueryWrapper<>();
            queryWrapper.select("priority_storage_location");
            queryWrapper.likeRight("material_coding", materialType);
            queryWrapper.likeRight("cargo_space_id",warehouseId);
            List<InventoryInformation> inventoryInformationList = inventoryInformationMapper.selectList(queryWrapper);
            List<String> cargoSpaceIdList = new ArrayList<>();
            //模糊查询的库存list
            for (InventoryInformation inventoryInformationAnother:inventoryInformationList
            ) {
                List<String> cargoSpaceIdListPre = Arrays.asList(StringUtils.commaDelimitedListToStringArray(inventoryInformationAnother.getPriorityStorageLocation()));
                //同类物料的推荐货位
                for (String cargoSpaceId : cargoSpaceIdListPre
                     ) {
                   cargoSpaceIdList.add(cargoSpaceId);
                }
            }
            //去重
            cargoSpaceIdList = cargoSpaceIdList.stream()
                    .distinct()
                    .collect(Collectors.toList());

            JSONArray jsonArrayNearCargoSpaceId = new JSONArray();
            //将去重后的货位进行遍历查询
            for (String cargoSpaceId:cargoSpaceIdList
                 ) {
                String shelfId = cargoSpaceId.substring(0,10);
                List<CargoSpaceManagement> cargoSpaceManagementList = iCargoSpaceManagementService.getCargoSpaceListByShelfId(shelfId);
                for (CargoSpaceManagement cargoSpaceManagement:cargoSpaceManagementList
                     ) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cargoSpaceId",cargoSpaceManagement.getCargoSpaceId());
                    jsonObject.put("full",cargoSpaceManagement.getFull());
                    jsonArrayNearCargoSpaceId.add(jsonObject);
                }
            }
            jsonObjectResult.put("NearCargoSpaceId",jsonArrayNearCargoSpaceId);
            return Result.success(jsonObjectResult);
        }


        String priorityStorageLocation = inventoryInformation.getPriorityStorageLocation();
        //历史货位不为空，返回未满的货位ID list
        List<String> cargoSpaceIdList = Arrays.asList(StringUtils.commaDelimitedListToStringArray(priorityStorageLocation));
        JSONArray jsonArrayHistoryCargoSpace = new JSONArray();
        for (String cargospaceId:cargoSpaceIdList
        ) {
            JSONObject jsonObject = new JSONObject();
            CargoSpaceManagement cargoSpaceManagement = iCargoSpaceManagementService.getCargoSpaceByCargoSpaceId(cargospaceId);
            //货位不存在且不为满
            if (ObjectUtil.isNotEmpty(cargoSpaceManagement)&&cargoSpaceManagement.getFull()!=2){
                jsonObject.put("cargoSpaceId",cargoSpaceManagement.getCargoSpaceId());
                jsonObject.put("full",cargoSpaceManagement.getFull());
                jsonArrayHistoryCargoSpace.add(jsonObject);
            }
        }
        jsonObjectResult.put("HistoryCargoSpaceId",jsonArrayHistoryCargoSpace);
        return Result.success(jsonObjectResult);
    }
}

