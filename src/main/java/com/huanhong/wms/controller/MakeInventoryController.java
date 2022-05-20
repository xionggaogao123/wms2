package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.MakeInventoryVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.beans.beancontext.BeanContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1//make-inventory")
@ApiSort()
@Api(tags = "盘点单据管理")
public class MakeInventoryController extends BaseController {

    @Resource
    private IMakeInventoryService makeInventoryService;

    @Resource
    private IMakeInventoryDetailsService makeInventoryDetailsService;

    @Resource
    private MakeInventoryMapper makeInventoryMapper;

    @Resource
    private IMaterialService materialService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;


    @Resource
    private IMakeInventoryReportService makeInventoryReportService;

    @Resource
    private IMakeInventoryReportDetailsService makeInventoryReportDetailsService;


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<MakeInventory>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            MakeInventoryVO makeInventoryVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<MakeInventory> pageResult = makeInventoryService.pageFuzzyQuery(new Page<>(current, size), makeInventoryVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到盘点单据信息");
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
    public Result add(@Valid @RequestBody AddMakeInventoryAndDetailsDTO addMakeInventoryAndDetailsDTO) {
        try {

            List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList = new ArrayList<>();
            /**
             * 先新增主表
             */
            Result result = makeInventoryService.addMakeInventory(addMakeInventoryAndDetailsDTO.getAddMakeInventoryDTO());

            if (!result.isOk()) {
                return Result.failure("新增入库单失败");
            }

            MakeInventory makeInventory = (MakeInventory) result.getData();

            String docNum = makeInventory.getDocumentNumber();

            String warehouseId = makeInventory.getWarehouseId();

            Integer consignor = makeInventory.getConsignor();

            /**
             * 判断是否为全盘
             */
            //非全盘的明细使用用户提交的明细
            if (makeInventory.getAllMake()==0) {

                /**
                 * 选择非全盘时，根据货主进行不同的明细生成
                 * 货主 0-泰丰盛和  1-矿上自有  2-全部
                 */
                /**
                 * 泰丰盛和
                 */
                if (makeInventory.getConsignor() == 0) {
                    //    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存 3-全部 ")
                    /**
                     * 暂存
                     */
                    if (makeInventory.getInventoryType() == 0) {
                        //物料类型: 0-全部物料、1-指定物料、2-随机物料
                        //全部物料
                        if (makeInventory.getMaterialType() == 0) {
                            //正式库存list
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 0, consignor);

                            for (InventoryInformation inventoryInformation : inventoryInformationList
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                            //指定物料
                        } else if (makeInventory.getMaterialType() == 1) {
                            if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                                for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                                ) {
                                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                }
                                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                                return result;
                            }
                        } else {
                            /**
                             * 先查询对应库存类型的全部库存
                             * 在从中抽出百分之三十返回给用户
                             */
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 0, consignor);

                            // 总记录数
                            int count = inventoryInformationList.size();

                            // 随机数起始位置
                            int randomCount = (int) (Math.random() * count);

                            int num = inventoryInformationList.size() / 3;

                            // 保证能展示10个数据
                            if (randomCount > count - num) {
                                randomCount = count - num;
                            }

                            QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();

                            queryWrapper.orderByDesc("id");
                            queryWrapper.ne("inventory_credit", 0);
                            queryWrapper.eq("warehouse_id", warehouseId);
                            queryWrapper.likeRight("cargo_space_id", warehouseId);
                            queryWrapper.and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
                            queryWrapper.eq("consignor", consignor);
                            queryWrapper.last("limit " + String.valueOf(randomCount) + ", 10");
                            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(queryWrapper);
                            for (InventoryInformation inventoryInformation : inventoryInformations
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                        }
                    }
                    /**
                     * 正式库存
                     */
                    else if (makeInventory.getInventoryType() == 1) {
                        //物料类型: 0-全部物料、1-指定物料、2-随机物料
                        //全部物料
                        if (makeInventory.getMaterialType() == 0) {
                            //正式库存list
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 1, consignor);

                            for (InventoryInformation inventoryInformation : inventoryInformationList
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                            //指定物料
                        } else if (makeInventory.getMaterialType() == 1) {
                            if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                                for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                                ) {
                                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                }
                                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                                return result;
                            }
                        }
                        //随机物料
                        else {
                            /**
                             * 先查询对应库存类型的全部库存
                             * 在从中抽出百分之三十返回给用户
                             */
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 1, consignor);

                            // 总记录数
                            int count = inventoryInformationList.size();

                            // 随机数起始位置
                            int randomCount = (int) (Math.random() * count);

                            int num = inventoryInformationList.size() / 3;

                            // 保证能展示10个数据
                            if (randomCount > count - num) {
                                randomCount = count - num;
                            }

                            QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();

                            queryWrapper.orderByDesc("id");
                            queryWrapper.ne("inventory_credit", 0);
                            queryWrapper.eq("warehouse_id", warehouseId);
                            queryWrapper.likeRight("cargo_space_id", warehouseId);
                            queryWrapper.eq("is_verification",1);
                            queryWrapper.eq("is_enter",1);
                            queryWrapper.eq("consignor", consignor);
                            queryWrapper.last("limit " + String.valueOf(randomCount) + ", 10");
                            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(queryWrapper);
                            for (InventoryInformation inventoryInformation : inventoryInformations
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                        }

                    }
                    /**
                     * 临时库存
                     */
                    else if (makeInventory.getInventoryType()==2){
                    List<TemporaryLibrary> temporaryLibraryList = temporaryLibraryService.getTemporaryLibraryListByWarehouseId(warehouseId);
                        /**
                         * 临时库
                         */
                        for (TemporaryLibrary temporaryLibrary : temporaryLibraryList
                        ) {
                            AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                            Material material = materialService.getMeterialByMeterialCode(temporaryLibrary.getMaterialCoding());
                            BeanUtil.copyProperties(temporaryLibrary, addMakeInventoryDetailsDTO);
                            BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                            addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                            addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                            addMakeInventoryDetailsDTO.setSublibraryId(temporaryLibrary.getCargoSpaceId().substring(0, 6));
                            addMakeInventoryDetailsDTO.setWarehouseAreaId(temporaryLibrary.getCargoSpaceId().substring(0, 7));
                            addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                        }

                        makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                        return result;
                    }
                }

                /**
                 * 矿上自有
                 */
                else if (makeInventory.getConsignor()==1){
                    //    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存 3-全部 ")
                    /**
                     * 暂存
                     */
                    if (makeInventory.getInventoryType() == 0) {
                        //物料类型: 0-全部物料、1-指定物料、2-随机物料
                        //全部物料
                        if (makeInventory.getMaterialType() == 0) {
                            //正式库存list
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 0, consignor);

                            for (InventoryInformation inventoryInformation : inventoryInformationList
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                            //指定物料
                        } else if (makeInventory.getMaterialType() == 1) {
                            if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                                for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                                ) {
                                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                }
                                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                                return result;
                            }
                        } else {
                            /**
                             * 先查询对应库存类型的全部库存
                             * 在从中抽出百分之三十返回给用户
                             */
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 0, consignor);

                            // 总记录数
                            int count = inventoryInformationList.size();

                            // 随机数起始位置
                            int randomCount = (int) (Math.random() * count);

                            int num = inventoryInformationList.size() / 3;

                            // 保证能展示10个数据
                            if (randomCount > count - num) {
                                randomCount = count - num;
                            }

                            QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();

                            queryWrapper.orderByDesc("id");
                            queryWrapper.ne("inventory_credit", 0);
                            queryWrapper.eq("warehouse_id", warehouseId);
                            queryWrapper.likeRight("cargo_space_id", warehouseId);
                            queryWrapper.and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
                            queryWrapper.ne("consignor", 0);
                            queryWrapper.last("limit " + String.valueOf(randomCount) + ", 10");
                            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(queryWrapper);
                            for (InventoryInformation inventoryInformation : inventoryInformations
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                        }
                    }
                    /**
                     * 正式库存
                     */
                    else if (makeInventory.getInventoryType() == 1) {
                        //物料类型: 0-全部物料、1-指定物料、2-随机物料
                        //全部物料
                        if (makeInventory.getMaterialType() == 0) {
                            //正式库存list
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 1, consignor);

                            for (InventoryInformation inventoryInformation : inventoryInformationList
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                            //指定物料
                        } else if (makeInventory.getMaterialType() == 1) {
                            if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                                for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                                ) {
                                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                }
                                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                                return result;
                            }
                        }
                        //随机物料
                        else {
                            /**
                             * 先查询对应库存类型的全部库存
                             * 在从中抽出百分之三十返回给用户
                             */
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(warehouseId, 1, consignor);

                            // 总记录数
                            int count = inventoryInformationList.size();

                            // 随机数起始位置
                            int randomCount = (int) (Math.random() * count);

                            int num = inventoryInformationList.size() / 3;

                            // 保证能展示10个数据
                            if (randomCount > count - num) {
                                randomCount = count - num;
                            }

                            QueryWrapper<InventoryInformation> wrapper = new QueryWrapper<>();

                            wrapper.orderByDesc("id");
                            wrapper.ne("inventory_credit", 0);
                            wrapper.eq("warehouse_id", warehouseId);
                            wrapper.likeRight("cargo_space_id", warehouseId);
                            wrapper.eq("is_verification", 1);
                            wrapper.eq("is_enter", 1);
                            wrapper.ne("consignor", 0);
                            wrapper.last("limit " + String.valueOf(randomCount) + ", 10");
                            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(wrapper);
                            for (InventoryInformation inventoryInformation : inventoryInformations
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                        }

                    }
                    /**
                     * 临时库存
                     */
                    else if (makeInventory.getInventoryType()==2){
                        List<TemporaryLibrary> temporaryLibraryList = temporaryLibraryService.getTemporaryLibraryListByWarehouseId(warehouseId);
                        /**
                         * 临时库
                         */
                        for (TemporaryLibrary temporaryLibrary : temporaryLibraryList
                        ) {
                            AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                            Material material = materialService.getMeterialByMeterialCode(temporaryLibrary.getMaterialCoding());
                            BeanUtil.copyProperties(temporaryLibrary, addMakeInventoryDetailsDTO);
                            BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                            addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                            addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                            addMakeInventoryDetailsDTO.setSublibraryId(temporaryLibrary.getCargoSpaceId().substring(0, 6));
                            addMakeInventoryDetailsDTO.setWarehouseAreaId(temporaryLibrary.getCargoSpaceId().substring(0, 7));
                            addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                        }

                        makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                        return result;
                    }
                }

                /**
                 * 全部
                 */
                else {
                    //    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存 3-全部 ")
                    /**
                     * 暂存
                     */
                    if (makeInventory.getInventoryType() == 0) {
                        //物料类型: 0-全部物料、1-指定物料、2-随机物料
                        //全部物料
                        if (makeInventory.getMaterialType() == 0) {

                            //临时库存list
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryType(warehouseId,0);

                            for (InventoryInformation inventoryInformation : inventoryInformationList
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                            //指定物料
                        } else if (makeInventory.getMaterialType() == 1) {
                            if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                                for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                                ) {
                                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                }
                                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                                return result;
                            }
                        } else {
                            /**
                             * 先查询对应库存类型的全部库存
                             * 在从中抽出百分之三十返回给用户
                             */
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryType(warehouseId,0);
                            // 总记录数
                            int count = inventoryInformationList.size();

                            // 随机数起始位置
                            int randomCount = (int) (Math.random() * count);

                            int num = inventoryInformationList.size() / 3;

                            // 保证能展示10个数据
                            if (randomCount > count - num) {
                                randomCount = count - num;
                            }

                            QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();

                            queryWrapper.orderByDesc("id");
                            queryWrapper.ne("inventory_credit", 0);
                            queryWrapper.eq("warehouse_id", warehouseId);
                            queryWrapper.likeRight("cargo_space_id", warehouseId);
                            queryWrapper.and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
                            queryWrapper.last("limit " + String.valueOf(randomCount) + ", 10");
                            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(queryWrapper);
                            for (InventoryInformation inventoryInformation : inventoryInformations
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                        }
                    }
                    /**
                     * 正式库存
                     */
                    else if (makeInventory.getInventoryType() == 1) {
                        //物料类型: 0-全部物料、1-指定物料、2-随机物料
                        //全部物料
                        if (makeInventory.getMaterialType() == 0) {
                            //正式库存list
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryType(warehouseId,1);

                            for (InventoryInformation inventoryInformation : inventoryInformationList
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                            //指定物料
                        } else if (makeInventory.getMaterialType() == 1) {
                            if (ObjectUtil.isNotNull(addMakeInventoryDetailsDTOList)) {
                                for (AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO : addMakeInventoryDetailsDTOList
                                ) {
                                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                }
                                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                                return result;
                            }
                        }
                        //随机物料
                        else {
                            /**
                             * 先查询对应库存类型的全部库存
                             * 在从中抽出百分之三十返回给用户
                             */
                            List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByWarehouseIdAndInventoryType(warehouseId,1);

                            // 总记录数
                            int count = inventoryInformationList.size();

                            // 随机数起始位置
                            int randomCount = (int) (Math.random() * count);

                            int num = inventoryInformationList.size() / 3;

                            // 保证能展示10个数据
                            if (randomCount > count - num) {
                                randomCount = count - num;
                            }

                            QueryWrapper<InventoryInformation> wrapper = new QueryWrapper<>();

                            wrapper.orderByDesc("id");
                            wrapper.ne("inventory_credit", 0);
                            wrapper.eq("warehouse_id", warehouseId);
                            wrapper.likeRight("cargo_space_id", warehouseId);
                            wrapper.eq("is_verification", 1);
                            wrapper.eq("is_enter", 1);
                            wrapper.ne("consignor", 0);
                            wrapper.last("limit " + String.valueOf(randomCount) + ", 10");
                            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(wrapper);
                            for (InventoryInformation inventoryInformation : inventoryInformations
                            ) {
                                AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                                Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                                BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                                BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                                addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                                addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                                addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                                addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                                addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                            }
                            makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                            return result;
                        }

                    }
                    /**
                     * 临时库存
                     */
                    else if (makeInventory.getInventoryType()==2){
                        List<TemporaryLibrary> temporaryLibraryList = temporaryLibraryService.getTemporaryLibraryListByWarehouseId(warehouseId);
                        /**
                         * 临时库
                         */
                        for (TemporaryLibrary temporaryLibrary : temporaryLibraryList
                        ) {
                            AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                            Material material = materialService.getMeterialByMeterialCode(temporaryLibrary.getMaterialCoding());
                            BeanUtil.copyProperties(temporaryLibrary, addMakeInventoryDetailsDTO);
                            BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                            addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                            addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                            addMakeInventoryDetailsDTO.setSublibraryId(temporaryLibrary.getCargoSpaceId().substring(0, 6));
                            addMakeInventoryDetailsDTO.setWarehouseAreaId(temporaryLibrary.getCargoSpaceId().substring(0, 7));
                            addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                        }

                        makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                        return result;
                    }
                }
            }


                List<InventoryInformation> inventoryInformationListAll = inventoryInformationService.getInventoryInformationListByWarehouseId(warehouseId);

                List<TemporaryLibrary> temporaryLibraryList = temporaryLibraryService.getTemporaryLibraryListByWarehouseId(warehouseId);
                /**
                 * 普通库（暂存∪正式）
                 */
                for (InventoryInformation inventoryInformation : inventoryInformationListAll
                ) {
                    AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                    Material material = materialService.getMeterialByMeterialCode(inventoryInformation.getMaterialCoding());
                    BeanUtil.copyProperties(inventoryInformation, addMakeInventoryDetailsDTO);
                    BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                    addMakeInventoryDetailsDTO.setSublibraryId(inventoryInformation.getCargoSpaceId().substring(0, 6));
                    addMakeInventoryDetailsDTO.setWarehouseAreaId(inventoryInformation.getCargoSpaceId().substring(0, 7));
                    addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                }

                /**
                 * 临时库
                 */
                for (TemporaryLibrary temporaryLibrary : temporaryLibraryList
                ) {
                    AddMakeInventoryDetailsDTO addMakeInventoryDetailsDTO = new AddMakeInventoryDetailsDTO();
                    Material material = materialService.getMeterialByMeterialCode(temporaryLibrary.getMaterialCoding());
                    BeanUtil.copyProperties(temporaryLibrary, addMakeInventoryDetailsDTO);
                    BeanUtil.copyProperties(material, addMakeInventoryDetailsDTO);
                    addMakeInventoryDetailsDTO.setDocumentNumber(docNum);
                    addMakeInventoryDetailsDTO.setWarehouseId(warehouseId);
                    addMakeInventoryDetailsDTO.setSublibraryId(temporaryLibrary.getCargoSpaceId().substring(0, 6));
                    addMakeInventoryDetailsDTO.setWarehouseAreaId(temporaryLibrary.getCargoSpaceId().substring(0, 7));
                    addMakeInventoryDetailsDTOList.add(addMakeInventoryDetailsDTO);
                }

                makeInventoryDetailsService.addMakeInventoryDetails(addMakeInventoryDetailsDTOList);
                return result;

        } catch (Exception e) {
            log.error("添加入库单出错，异常", e);
            return Result.failure("系统异常：入库单添加失败。");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新盘点单", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateMakeInventoryAndDetailsDTO updateMakeInventoryAndDetailsDTO) {
        try {

            /**
             * 如果当前时间晚于盘点开始时间则不能更新
             */

            //如果想比较日期则写成"yyyy-MM-dd"就可以了
            //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //将字符串形式的时间转化为Date类型的时间
            Date planTime = DateUtil.parseDate(updateMakeInventoryAndDetailsDTO.getUpdateMakeInventoryDTO().getStartTime().toString());

            Date nowTime = DateUtil.parseDate(DateUtil.now());

            if(nowTime.getTime()-planTime.getTime()>0){
                return Result.failure("盘点已开始,无法更新单据！");
            }

            UpdateMakeInventoryDTO updateMakeInventoryDTO = updateMakeInventoryAndDetailsDTO.getUpdateMakeInventoryDTO();

            List<UpdateMakeInventoryDetailsDTO> updateMakeInventoryDetailsDTOList = updateMakeInventoryAndDetailsDTO.getUpdateMakeInventoryDetailsDTOList();

            Result result = makeInventoryService.updateMakeInventory(updateMakeInventoryDTO);

            if (!result.isOk()) {
                return Result.failure("更新盘点单失败！");
            }

            //更新明细
            Result resultDetails = makeInventoryDetailsService.updateMakeInventoryDetails(updateMakeInventoryDetailsDTOList);

            log.info("更新盘点明细{}",resultDetails);


            //明细更新完成后,查询所有明细是否已完成
            MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(updateMakeInventoryDTO.getId());

            int count = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseIdNotComplete(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

            //如果未完成的明细数量为零，则更新主表状态为已完成
            if (count==0){
                updateMakeInventoryDTO.setConsignor(1);
                result = makeInventoryService.updateMakeInventory(updateMakeInventoryDTO);
            }

            /**
             * 如果盘点报告单已生成则同步更新盘点报告
             */
            if (ObjectUtil.isNotNull(makeInventoryReportService.getMakeInventoryReportByDocNumAndWarehouse(makeInventory.getDocumentNumber(), makeInventory.getWarehouseId()))){
                Result resultUpdateReport = updateMakeInventoryReport(resultDetails);
                log.info("同步更新报告单{}",resultUpdateReport);
            }

            return result;
        } catch (Exception e) {
            log.error("更新盘点单失败!");
            return Result.failure("系统异常：更新盘点单失败!");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {

        MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);

        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }
        boolean delete = makeInventoryService.removeById(id);

        //主表删除成功,删除明细
        if (delete){
            String docNum = makeInventory.getDocumentNumber();

            List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

            for (MakeInventoryDetails makeInventoryDetails:makeInventoryDetailsList
            ) {
                makeInventoryDetailsService.removeById(makeInventoryDetails.getId());
            }
        }
        return Result.success("删除成功");
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取盘点单数据")
    @GetMapping("/getMakeInventoryById/{id}")
    public Result getMakeInvenrory(@PathVariable Integer id) {

        MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }

        JSONObject jsonObject = new JSONObject();
        List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

        jsonObject.put("doc",makeInventory);
        jsonObject.put("details",makeInventoryDetailsList);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据单据编号和子库编号获取盘点单及明细数据")
    @GetMapping("/getMakeInventoryByDocNumAndSublibraryId/{docNum}&{sublibraryId}")
    public Result getMakeInventoryByDocNumAndSublibraryId(@PathVariable String docNum,
                                                          @PathVariable String sublibraryId
    ) {
        String warehouseId = sublibraryId.substring(0, 4);

        MakeInventory makeInventory = makeInventoryService.getMakeInventoryByDocNumAndWarehouse(docNum, warehouseId);

        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }

        JSONObject jsonObject = new JSONObject();
        List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());
        jsonObject.put("doc",makeInventory);
        jsonObject.put("details",makeInventoryDetailsList);
        return Result.success(jsonObject);
    }


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据子库ID获取当前仓库的待盘点任务数")
    @GetMapping("/getCountBySublibraryId/{sublibraryId}")
    public Result getCountBySublibraryId(@PathVariable String sublibraryId) {
        String warehouseId = sublibraryId.substring(0, 4);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.likeRight("sublibrary_id", warehouseId);
        queryWrapper.eq("check_status", 0);
        Integer count = makeInventoryMapper.selectCount(queryWrapper);
        return ObjectUtil.isNotNull(count) ? Result.success(count) : Result.failure("未查询到相关数据");
    }


    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "PDA-根据单据编号和子库编号获取盘点单数据(附带物料详情)")
    @GetMapping("/getMakeInventoryForPda")
    public Result getMakeInventoryByDocNumAndSublibraryIdForPDA(@RequestParam String docNum,
                                                                @RequestParam String sublibraryId
    ) {
        String warehouseId = sublibraryId.substring(0, 4);
        MakeInventory makeInventory = makeInventoryService.getMakeInventoryByDocNumAndWarehouse(docNum, warehouseId);
        if (ObjectUtil.isEmpty(makeInventory)) {
            return Result.success("未查到相关数据！");
        }
        List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(docNum,warehouseId);
        JSONArray jsonArray = new JSONArray();
        for (MakeInventoryDetails makeInventoryDetails:makeInventoryDetailsList
             ) {
            JSONObject jsonObject = new JSONObject();
            Material material = materialService.getMeterialByMeterialCode(makeInventoryDetails.getMaterialCoding());
            jsonObject.put("details",makeInventoryDetails);
            jsonObject.put("material",material);
            jsonArray.add(jsonObject);
        }

        JSONObject jsonObjectResult = new JSONObject();
        jsonObjectResult.put("doc",makeInventory);
        jsonObjectResult.put("details",jsonArray);
        return Result.success(jsonObjectResult);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
    })
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "流程引擎-采购入库-查询")
    @GetMapping("getParameterById/{id}")
    public Result getParameterById(@PathVariable Integer id) {

        EntityUtils entityUtils = new EntityUtils();
        /**
         * 根据主表ID获取主表及明细表数据
         */
        try {
            MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
            if (ObjectUtil.isNotNull(makeInventory)) {
                List<MakeInventoryDetails> makeInventoryDetailsList = makeInventoryDetailsService.getMakeInventoryDetailsByDocNumAndWarehouseId(makeInventory.getDocumentNumber(),makeInventory.getWarehouseId());

                /**
                 * 当查询到主表事进行数据封装
                 * 1.表头--主表表明--用于判断应该进入那个流程-tableName
                 * 2.主表字段名对照-main
                 * 3.明细表字段名对照-details
                 * 4.主表数据-mainValue
                 * 5.明细表数据-detailsValue
                 * 6.主表更新接口-mainUpdate
                 * 7.明细表更新接口-detailsUpdate
                 */
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("tableName", "make_inventory");
                jsonResult.put("main", entityUtils.jsonField("makeInventory", new MakeInventory()));
                jsonResult.put("details", entityUtils.jsonField("makeInventory", new MakeInventoryDetails()));
                jsonResult.put("mainValue", makeInventory);
                jsonResult.put("detailsValue", makeInventoryDetailsList);
                jsonResult.put("mainKey","updateMakeInventoryDTO");
                jsonResult.put("detailKey","updateMakeInventoryDetailsDTO");
                jsonResult.put("mainUpdate", "/wms/api/v1/make-inventory/update");
                jsonResult.put("detailsUpdate", "/wms/api/v1/make-inventory-details/update");
                jsonResult.put("missionCompleted", "/wms/api/v1/enter-warehouse/missionCompleted");
                return Result.success(jsonResult);
            } else {
                return Result.failure("未查询到相关信息");
            }
        } catch (Exception e) {
            log.error("查询失败,异常：", e);
            return Result.failure("查询失败，系统异常！");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "单据Id"),
            @ApiImplicitParam(name = "processInstanceId", value = "流程Id")
    })
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "流程引擎-采购入库-发起")
    @PutMapping("/missionStarts")
    public Result missionStarts(@RequestParam Integer id,
                                @RequestParam String processInstanceId) {

        try {
            MakeInventory makeInventory = makeInventoryService.getMakeInventoryById(id);
            /**
             * 正常情况不需要原对单据进行非空验证，
             * 此处预留其他判断条件的位置
             */
            if (ObjectUtil.isNotNull(makeInventory)) {
                UpdateMakeInventoryDTO updateMakeInventoryDTO = new UpdateMakeInventoryDTO();
                updateMakeInventoryDTO.setId(id);
                updateMakeInventoryDTO.setProcessInstanceId(processInstanceId);
                /**
                 *  单据状态由草拟转为审批中
                 *  审批状态:
                 *  1.草拟
                 *  2.审批中
                 *  3.审批生效
                 *  4.作废
                 */
                updateMakeInventoryDTO.setPlanStatus(2);
                Result result = makeInventoryService.updateMakeInventory(updateMakeInventoryDTO);
                if (result.isOk()) {
                    return Result.success("进入流程");
                } else {
                    return Result.failure("未进入流程");
                }
            } else {
                return Result.failure("未找到此单据,无法进入流程引擎");
            }
        } catch (Exception e) {
            log.error("流程启动接口异常", e);
            return Result.failure("系统异常");
        }
    }

    /**
     * 盘点报告跟随盘点单同步更新
     * @param resultDetails
     * @return
     */
    public Result updateMakeInventoryReport(Result resultDetails){

        /**
         * 盘点单更新明细后，更新盘点报告中的对应明细
         */
        //物料编码 批次 货位编码
        try {
            JSONObject jsonObject = (JSONObject) resultDetails.getData();

            List<UpdateMakeInventoryDetailsDTO> listSuccess = (List<UpdateMakeInventoryDetailsDTO>) jsonObject.get("success");

            List<UpdateMakeInventoryReportDetailsDTO> updateMakeInventoryReportDetailsDTOList = new ArrayList<>();

            MakeInventoryReportDetails makeInventoryReportDetails = new MakeInventoryReportDetails();

            for (UpdateMakeInventoryDetailsDTO updateMakeInventoryDetailsDTO:listSuccess
            ) {
                UpdateMakeInventoryReportDetailsDTO updateMakeInventoryReportDetailsDTO = new UpdateMakeInventoryReportDetailsDTO();
                String materialCoding = updateMakeInventoryDetailsDTO.getMaterialCoding();
                String batch = updateMakeInventoryDetailsDTO.getBatch();
                String cargoSpaceId = updateMakeInventoryDetailsDTO.getCargoSpaceId();
                makeInventoryReportDetails  = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByMaterialCodingAndBatchAndCargoSpaceId(materialCoding,batch,cargoSpaceId);
                if (ObjectUtil.isNotEmpty(makeInventoryReportDetails)){
                    BeanUtil.copyProperties(makeInventoryReportDetails,updateMakeInventoryReportDetailsDTO);
                    //实盘数量
                    updateMakeInventoryReportDetailsDTO.setCheckCredit(updateMakeInventoryDetailsDTO.getCheckCredit());
                    //盈亏数量
                    updateMakeInventoryReportDetailsDTO.setFinalCredit(NumberUtil.sub(updateMakeInventoryDetailsDTO.getInventoryCredit(),updateMakeInventoryDetailsDTO.getCheckCredit()));
                    //盈亏金额
                    if (makeInventoryReportDetails.getConsignor()==0){
                        updateMakeInventoryReportDetailsDTO.setFinalAmounts(NumberUtil.mul(updateMakeInventoryDetailsDTO.getUnitPrice(),updateMakeInventoryReportDetailsDTO.getFinalAmounts()));
                    }else {
                        updateMakeInventoryReportDetailsDTO.setFinalAmounts(NumberUtil.mul(updateMakeInventoryDetailsDTO.getSalesUnitPrice(),updateMakeInventoryReportDetailsDTO.getFinalAmounts()));
                    }
                    //差异原因
                    updateMakeInventoryReportDetailsDTO.setReason(updateMakeInventoryDetailsDTO.getReason());
                    //盘点状态
                    /**
                     * 盈亏数量大于0则亏，等于0一致，小于零则盈
                     */
                    int event = updateMakeInventoryReportDetailsDTO.getFinalAmounts().compareTo(BigDecimal.valueOf(0));

                    //"盘点状态: 0-待盘点，1-一致 ，2-盘盈 ，3-盘亏"
                    if (event==0){
                        updateMakeInventoryReportDetailsDTO.setCheckStatusDetails(1);
                    }else if (event>0){
                        updateMakeInventoryReportDetailsDTO.setCheckStatusDetails(3);
                    }else {
                        updateMakeInventoryReportDetailsDTO.setCheckStatusDetails(2);
                    }

                    updateMakeInventoryReportDetailsDTOList.add(updateMakeInventoryReportDetailsDTO);
                }
            }

            //更新盘点报告list
            Result resultUpdateReport = makeInventoryReportDetailsService.updateMakeInventoryReportDetailsList(updateMakeInventoryReportDetailsDTOList);

            log.info("更新盘点报告明细{}",resultUpdateReport);

            int countReport = makeInventoryReportDetailsService.getMakeInventoryReportDetailsByDocNumAndWarehouseIdNotComplete(makeInventoryReportDetails.getReportNumber(),makeInventoryReportDetails.getWarehouseId());

            /**
             *
             */
            if (countReport==0){
                UpdateMakeInventoryReportDTO updateMakeInventoryReportDTO = new UpdateMakeInventoryReportDTO();
                MakeInventoryReport makeInventoryReport = makeInventoryReportService.getMakeInventoryReportByReportNumAndWarehouse(makeInventoryReportDetails.getReportNumber(),makeInventoryReportDetails.getWarehouseId());
                updateMakeInventoryReportDTO.setId(makeInventoryReport.getId());
                updateMakeInventoryReportDTO.setCheckStatus(1);
                Result resultReport = makeInventoryReportService.updateMakeInventoryReport(updateMakeInventoryReportDTO);
                log.info("更新盘点报告主表{}",resultReport);
                return resultReport;
            }
            return resultUpdateReport;
        }catch (Exception e){
            log.error("同步更新盘点报告失败,",e);
            return Result.failure("同步更新盘点报告失败！");
        }
    }

}

