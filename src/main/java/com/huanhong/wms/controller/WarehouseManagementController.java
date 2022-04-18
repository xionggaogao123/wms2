package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
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
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.WarehouseVo;
import com.huanhong.wms.mapper.CargoSpaceManagementMapper;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/v1/warehouse-management")
@ApiSort()
@Api(tags = "仓库管理")
public class WarehouseManagementController extends BaseController {

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private WarehouseManagementMapper warehouseManagementMapper;

    @Resource
    private ICompanyService companyService;

    @Resource
    private ISublibraryManagementService sublibraryManagementService;

    @Resource
    private IWarehouseAreaManagementService warehouseAreaManagementService;

    @Resource
    private IShelfManagementService shelfManagementService;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Resource
    private CargoSpaceManagementMapper cargoSpaceManagementMapper;


    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(WarehouseManagementController.class);


    /**
     * @param current
     * @param size
     * @param warehouseVo
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询组合仓库")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<WarehouseManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  WarehouseVo warehouseVo //查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<WarehouseManagement> pageResult = warehouseManagementService.pageFuzzyQuery(new Page<>(current, size), warehouseVo);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加仓库")
    @PostMapping("/addWarehouse")
    public Result add(@Valid @RequestBody AddWarehouseDTO addWarehouseDTO) {

        /**
         * 判断是否有必填参数为空
         */
//        try {
//
//            /**
//             * 查看公司是否存在
//             */
//            Company company = companyService.getCompanyById(addWarehouseDTO.getCompanyId());
//            if (ObjectUtil.isEmpty(company)) {
//                return Result.failure(ErrorCode.DATA_IS_NULL, "公司不存在，无法添加库房");
//            }
//
//            /**
//             * 实体类转为json
//             */
//            String warehouseManagementToJoStr = JSONObject.toJSONString(addWarehouseDTO);
//            JSONObject warehouseManagementJo = JSONObject.parseObject(warehouseManagementToJoStr);
//
//            /**
//             * 不能为空的参数list
//             * 配置于judge.properties
//             */
//            List<String> list = judgeConfig.getWarehouseNotNullList();
//
//            /**
//             * 将NotNullList中的值当作key判断value是否为空
//             */
//            for (int i = 0; i < list.size(); i++) {
//                String key = list.get(i);
//                if (ObjectUtil.isEmpty(warehouseManagementJo.getString(key)) || "null".equals(warehouseManagementJo.getString(key))) {
//                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("添加仓库失败--判断参数空值出错,异常：" + e);
//            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
//        }

        /**
         * 在此处查重
         */
        try {
            WarehouseManagement warehouse = warehouseManagementService.getWarehouseByWarehouseId(addWarehouseDTO.getWarehouseId());
            if (ObjectUtil.isNotEmpty(warehouse)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "仓库编号重复");
            }
            try {
                WarehouseManagement warehouseManagement = new WarehouseManagement();
                BeanUtil.copyProperties(addWarehouseDTO, warehouseManagement);
                int insert = warehouseManagementMapper.insert(warehouseManagement);
                //新建库房成功后新建子库
                if (insert>0){
                    String warehousId = warehouseManagement.getWarehouseId();
                    AddSubliraryDTO addSubliraryDTO = new AddSubliraryDTO();
                    addSubliraryDTO.setWarehouseId(warehousId);
                    addSubliraryDTO.setSublibraryId("01");
                    addSubliraryDTO.setSublibraryName("暂存子库");
                    addSubliraryDTO.setSublibraryAcreage("无");
                    addSubliraryDTO.setSublibraryFloor("无");
                    addSubliraryDTO.setSublibraryPrincipal("无");
                    addSubliraryDTO.setSublibraryLength((double)0);
                    addSubliraryDTO.setSublibraryWidth((double)0);
                    addSubliraryDTO.setSublibraryHeight((double)0);
                    addSubliraryDTO.setSublibraryContactNumber("无");
                    addSubliraryDTO.setRemark("系统自动生成");
                    Result resultSublibrary = sublibraryManagementService.addSublibraryManagement(addSubliraryDTO);
                    //新建子库成功后新建暂存库区
                    if (resultSublibrary.isOk()){
                        String subliraryId = ((SublibraryManagement)resultSublibrary.getData()).getSublibraryId();
                        AddWarehouseAreaDTO addWarehouseAreaDTO = new AddWarehouseAreaDTO();
                        addWarehouseAreaDTO.setSublibraryId(subliraryId);
                        addWarehouseAreaDTO.setWarehouseAreaId("A");
                        addWarehouseAreaDTO.setWarehouseAreaName("暂存库区");
                        addWarehouseAreaDTO.setWarehouseAreaHeight((double)0);
                        addWarehouseAreaDTO.setWarehouseAreaWidth((double)0);
                        addWarehouseAreaDTO.setWarehouseAreaLength((double)0);
                        addWarehouseAreaDTO.setWarehouseAreaPrincipal("无");
                        addWarehouseAreaDTO.setWarehouseAreaContactNumber("无");
                        addWarehouseAreaDTO.setRemark("系统自动生成");
                        Result resultWarehouseArea = warehouseAreaManagementService.addWarehouseArea(addWarehouseAreaDTO);
                        //新增库区成功后新建临时货位
                        if (resultWarehouseArea.isOk()){
                            String warehouseAreaId = ((WarehouseAreaManagement)resultWarehouseArea.getData()).getWarehouseAreaId();
                            AddShelfDTO addShelfDTO = new AddShelfDTO();
                            addShelfDTO.setWarehouseAreaId(warehouseAreaId);
                            addShelfDTO.setShelfId("A00");
                            addShelfDTO.setShelfType(0);
                            addShelfDTO.setShelfLoadBearing((double)0);
                            addShelfDTO.setShelfHeight((double)0);
                            addShelfDTO.setShelfBottomLength((double)0);
                            addShelfDTO.setShelfBottomWidth((double)0);
                            addShelfDTO.setCellNumber(1);
                            addShelfDTO.setShelfLayer(1);
                            addShelfDTO.setRemark("系统自动生成");
                            Result resultShelf = shelfManagementService.addShelf(addShelfDTO);
                            if (resultShelf.isOk()){
                                String shelfId = ((ShelfManagement)resultShelf.getData()).getShelfId();
                                CargoSpaceManagement cargoSpaceManagement = new CargoSpaceManagement();
                                AddCargoSpacedDTO addCargoSpacedDTO = new AddCargoSpacedDTO();
                                addCargoSpacedDTO.setShelfId(shelfId);
                                addCargoSpacedDTO.setCargoSpaceId(shelfId+"00");
                                addCargoSpacedDTO.setCargoSpaceFloor(1);
                                addCargoSpacedDTO.setCargoSpaceType(0);
                                addCargoSpacedDTO.setCargoSpaceHeight((double)0);
                                addCargoSpacedDTO.setCargoSpaceWidth((double)0);
                                addCargoSpacedDTO.setCargoSpaceLength((double)0);
                                addShelfDTO.setRemark("系统自动生成");
                                BeanUtil.copyProperties(addCargoSpacedDTO,cargoSpaceManagement);
                                int addCargoSpace = cargoSpaceManagementMapper.insert(cargoSpaceManagement);
                                if (addCargoSpace>0){
                                    return Result.success("新增库房、暂存子库、暂存库区、暂存货架、暂存货位成功！");
                                }else {
                                    return Result.success("新增库房、暂存子库、暂存库区、暂存货架成功,新建暂存货位失败！");
                                }
                            }else{
                                return Result.success("新增库房、暂存子库、暂存库区成功,新建暂存货架失败！");
                            }
                        }else {
                            return Result.success("新增库房、暂存子库成功,新建暂存库区失败！");
                        }
                    }else {
                        return Result.success("新增库房成功,新建暂存子库失败！");
                    }
                }else {
                    return Result.success("新增库房失败！");
                }
            } catch (Exception e) {
                LOGGER.error("添加仓库错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加仓库失败--处理（判断库房编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新仓库")
    @PutMapping("/updateWarehouse")
    public Result update(@Valid @RequestBody UpdateWarehouseDTO updateWarehouseDTO) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        try {

            if (StringUtils.isBlank(updateWarehouseDTO.getWarehouseId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "仓库编号为空");
            }


            WarehouseManagement warehouseManagementIsExist = warehouseManagementService.getWarehouseByWarehouseId(updateWarehouseDTO.getWarehouseId());
            if (ObjectUtil.isEmpty(warehouseManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此仓库编码");
            }
            /**
             * 判断仓库是否停用 若停用是否已经将stopUsing字段改为 0 -使用中
             * 若改为0则允许更新  反之则拒绝
             */
            if (warehouseManagementService.isStopUsing(updateWarehouseDTO.getWarehouseId()) != 0) {
                //若仓库处于停用中 判断用户是和否将停用改为启用
                if (updateWarehouseDTO.getStopUsing() != 0) {
                    return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "仓库已停用,禁止更新！");
                }
            }
            WarehouseManagement warehouseManagement = new WarehouseManagement();
            BeanUtil.copyProperties(updateWarehouseDTO, warehouseManagement);
            updateWrapper.eq("warehouse_id", updateWarehouseDTO.getWarehouseId());
            int update = warehouseManagementMapper.update(warehouseManagement, updateWrapper);
            String parentCode = updateWarehouseDTO.getWarehouseId();
            if (update > 0 && ObjectUtil.isNotNull(warehouseManagement.getStopUsing())) {
                //如果仓库更新成功 判断此次更新仓库是否处于启用状态
                //若是启用状态  则将停用状态为 2-父级停用的子级全部启用
                if (warehouseManagement.getStopUsing() == 0) {
                      sublibraryManagementService.stopUsingByParentCode(parentCode,true);
                      warehouseAreaManagementService.stopUsingByParentCode(parentCode,true);
                      shelfManagementService.stopUsingByParentCode(parentCode,true);
                      cargoSpaceManagementService.stopUsingByParentCode(parentCode,true);
                   }else {
                      //若是停用状态 则将停用状态为 0-启用 的子级全部停用
                      sublibraryManagementService.stopUsingByParentCode(parentCode,false);
                      warehouseAreaManagementService.stopUsingByParentCode(parentCode,false);
                      shelfManagementService.stopUsingByParentCode(parentCode,false);
                      cargoSpaceManagementService.stopUsingByParentCode(parentCode,false);
                 }
            }
                return Result.success("操作成功");
        } catch (Exception e) {
            LOGGER.error("更新库房信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：仓库更新失败，请稍后再试或联系管理员");
        }
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除仓库")
    @DeleteMapping("deleteWarehouseByWarehouseId/{warehouseId}")
    public Result delete(@PathVariable String warehouseId) {
        QueryWrapper<WarehouseManagement> queryWrapper = new QueryWrapper<>();
        try {
            /**
             * 查看是否有下属子库
             */
            List<SublibraryManagement> sublibraryManagementList = sublibraryManagementService.getSublibraryManagementByWarehouseId(warehouseId);
            if (ObjectUtil.isNotEmpty(sublibraryManagementList)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "此仓库下存在子库，请先删除子库");
            }
            queryWrapper.eq("warehouse_id", warehouseId);
            WarehouseManagement warehouseManagement = warehouseManagementMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(warehouseManagement)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "操作失败：仓库编号不存在");
            }

            /**
             * 判断仓库是否停用 若停用是否已经将stopUsing字段改为 0 -使用中
             * 若改为0则允许更新  反之则拒绝
             */
            if (warehouseManagementService.isStopUsing(warehouseId) == 1) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "仓库已停用,无法删除!");
            }
            int i = warehouseManagementMapper.delete(queryWrapper);
            LOGGER.info("库房:  " + warehouseId + "删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除库房信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：仓库删除失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取仓库详细信息")
    @GetMapping("/getWarehouseByWarehouseId/{warehouseId}")
    public Result getWarehouse(@PathVariable String warehouseId) {
        try {
            WarehouseManagement warehouseManagement = warehouseManagementService.getWarehouseByWarehouseId(warehouseId);
            return Result.success(warehouseManagement);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取仓库信息失败");
        }
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "获取对应仓库的所有子库")
    @GetMapping("/getAllSublibraryByWarehouseId/{warehouseId}")
    public Result getAllSublibrary(@PathVariable String warehouseId) {
        try {
            List<SublibraryManagement> sublibraryManagementList = sublibraryManagementService.getSublibraryManagementByWarehouseId(warehouseId);
            return Result.success(sublibraryManagementList);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取子库信息失败");
        }
    }


    /** 模糊查询-暂时废弃
     * @param key
     * @param value
     * @return
     */
//    @ApiOperationSupport(order = 5)
//    @ApiOperation(value = "模糊查询")
//    @GetMapping("/getWareHouseByFuzzyQuery/{key}&{value}")
//    public Result getMeterialByFuzzy(@PathVariable("key") String key,
//                                     @PathVariable("value") String value) {
//        try {
//            String field = fuzzyQuery(key);
//            // List<String> warehouselList = warehouseManagementMapper.fuzzyQuerySelectList(field, value);
//            //QueryWrapper<WarehouseManagement> queryWrapper = new QueryWrapper<>();
//            //queryWrapper.select(field).like(field, value);
//            List<WarehouseManagement> warehouselList = warehouseManagementMapper.selectList(queryWrapper);
//            if (warehouselList != null) {
//                return Result.success(warehouselList, "查询成功");
//            }
//            return Result.noDataError();
//        } catch (Exception e) {
//            LOGGER.error("模糊查询出错--异常：" + e);
//            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：模糊查询错误，请稍后再试或联系管理员");
//        }
//    }

//    public String fuzzyQuery(String key) {
//        String field;
//        switch (key) {
//            //所属公司ID
//            case "companyId":
//                field = "company_id";
//                break;
//            //库房编号
//            case "warehouseId":
//                field = "warehouse_id";
//                break;
//            //库房名称
//            case "warehouseName":
//                field = "warehouse_name";
//                break;
//            //库房面积
//            case "warehouseAcreage":
//                field = "warehouse_acreage";
//                break;
//            //库房层数
//            case "warehouseLayers":
//                field = "warehouse_layers";
//                break;
//            //库房地址
//            case "warehouseAdress":
//                field = "warehouse_adress";
//                break;
//            //库房负责人
//            case "warehousePrincipal":
//                field = "warehouse_principal";
//                break;
//            //库房联系电话
//            case "warehouseContactNumber":
//                field = "warehouse_contact_number";
//                break;
//            case UNKNOWN:
//                throw new IllegalArgumentException("未知字段");
//            default:
//                throw new IllegalStateException("Unexpected value: " + key);
//        }
//        return field;
//    }

}

