package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.DataUtil;
import com.huanhong.common.units.excel.ExportExcel;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.Record;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.param.MaterialProfitParam;
import com.huanhong.wms.entity.vo.InventoryInfoVo;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import com.huanhong.wms.entity.vo.PreExpirationInventoryInfoVo;
import com.huanhong.wms.entity.vo.SafeInventoryInfoVo;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.MaterialPriceService;
import com.huanhong.wms.service.RecordService;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-25
 */
@Service
public class InventoryInformationServiceImpl extends SuperServiceImpl<InventoryInformationMapper, InventoryInformation> implements IInventoryInformationService {

    @Resource
    private MaterialPriceService materialPrice;

    @Resource
    private RecordService recordService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Autowired
    private OssProperties ossProperties;

    @Resource
    private MaterialPriceService materialPriceService;

    /**
     * ????????????
     *
     * @param inventoryInformationPage
     * @param inventoryInformationVO
     * @return
     */
    @Override
    public Page<InventoryInformation> pageFuzzyQuery(Page<InventoryInformation> inventoryInformationPage, InventoryInformationVO inventoryInformationVO) {

        //??????QueryWrapper??????
        QueryWrapper<InventoryInformation> query = new QueryWrapper<>();
        //??????id??????
        query.orderByDesc("id");
        //???????????????????????????Vo?????????????????????????????????
        //????????????selectPage??????
        if (ObjectUtil.isEmpty(inventoryInformationVO)) {
            return inventoryInformationMapper.selectPage(inventoryInformationPage, query);
        }
        //???Vo????????????????????????????????????????????????
        //?????????????????????????????????????????????????????????SQL?????????
        query.like(StringUtils.isNotBlank(inventoryInformationVO.getMaterialCoding()), "material_coding", inventoryInformationVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getMaterialName()), "material_name", inventoryInformationVO.getMaterialName());

//      query.like(StringUtils.isNotBlank(inventoryInformationVO.getCargoSpaceId()), "cargo_space_id", inventoryInformationVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getWarehouseId()), "warehouse_id", inventoryInformationVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getWarehouseAreaId()), "warehouse_area_id", inventoryInformationVO.getWarehouseAreaId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getWarehouseName()), "warehouse_name", inventoryInformationVO.getWarehouseName());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getCargoSpaceId()), "cargo_space_id", inventoryInformationVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getBatch()), "batch", inventoryInformationVO.getBatch());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getConsignor()), "consignor", inventoryInformationVO.getConsignor());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getSupplier()), "supplier", inventoryInformationVO.getSupplier());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getIsVerification()), "is_verification", inventoryInformationVO.getIsVerification());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getIsEnter()), "is_enter", inventoryInformationVO.getIsEnter());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getIsOnshelf()), "is_onshelf", inventoryInformationVO.getIsOnshelf());

        return baseMapper.selectPage(inventoryInformationPage, query);
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    @Override
    public Result updateInventoryInformation(UpdateInventoryInformationDTO updateInventoryInformationDTO) {

        /**
         * ????????????????????????
         */
        if (StringUtils.isNotBlank(updateInventoryInformationDTO.getCargoSpaceId())) {
            CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(updateInventoryInformationDTO.getCargoSpaceId());
            if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "??????????????????");
            }
        }
        InventoryInformation inventoryInformationOld = getInventoryById(updateInventoryInformationDTO.getId());
        /**
         * vesion ??????veision ??????????????????????????????  ?????????????????????
         */
        BeanUtil.copyProperties(updateInventoryInformationDTO, inventoryInformationOld);

        /**
         * ????????????????????????????????????????????????????????? ??????????????????????????????????????????
         */
        int event = NumberUtil.compare(inventoryInformationOld.getInventoryCredit(), updateInventoryInformationDTO.getInventoryCredit());
        if (event != 0) {
            inventoryInformationOld.setLastUpdateInventoryCredit(LocalDateTime.parse(DateUtil.now()));
        }

        int i = inventoryInformationMapper.updateById(inventoryInformationOld);

        if (i > 0) {
            return Result.success("???????????????");
        }
        return Result.failure("????????????,?????????????????????????????????,?????????");
    }

    /**
     * ????????????
     *
     * @param addInventoryInformationDTO
     * @return
     */
    @Override
    public Result addInventoryInformation(AddInventoryInformationDTO addInventoryInformationDTO) {
        /**
         * ????????????????????????
         */
        CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(addInventoryInformationDTO.getCargoSpaceId());
        if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
            return Result.failure(ErrorCode.DATA_IS_NULL, "??????????????????");
        }

        /**
         * ??????????????????????????????????????????????????????????????????
         * 1.????????? ?????? ????????????????????????????????????????????????????????????
         */
//        InventoryInformationVO inventoryInformationVO = new InventoryInformationVO();
//        BeanUtil.copyProperties(addInventoryInformationDTO, inventoryInformationVO);
        InventoryInformation inventoryInformationIsExist = getInventoryInformation(addInventoryInformationDTO.getMaterialCoding(), addInventoryInformationDTO.getBatch(), addInventoryInformationDTO.getCargoSpaceId());
        if (ObjectUtil.isNotEmpty(inventoryInformationIsExist)) {
            //????????????
            Double inventoryCreditOld = inventoryInformationIsExist.getInventoryCredit();
            //????????????
            Double inventoryCreditNew = addInventoryInformationDTO.getInventoryCredit();
            //??????????????????
            inventoryInformationIsExist.setInventoryCredit(NumberUtil.add(inventoryCreditOld, inventoryCreditNew));
//            inventoryInformationIsExist.setVersion(12);
            //????????????ID??????updateDTO
            int i = inventoryInformationMapper.updateById(inventoryInformationIsExist);

            if (i > 0) {
                return Result.success("???????????????????????????????????????,???????????????");
            } else {
                return Result.failure("???????????????????????????????????????,??????????????????(?????????????????????????????????,?????????)");
            }
        }

        /**
         * ?????????????????????????????????????????????-?????????????????????
         */
        String warehouseId = addInventoryInformationDTO.getWarehouseId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("priority_storage_location");
        queryWrapper.like("warehouse_id", warehouseId);
        queryWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());

        /**
         * ???????????????????????????
         */
        InventoryInformation inventoryInformation = new InventoryInformation();
        BeanUtil.copyProperties(addInventoryInformationDTO, inventoryInformation);
        int insert = inventoryInformationMapper.insert(inventoryInformation);
        //????????????????????????????????????????????????????????????list
        List<Map<String, Object>> maplist = inventoryInformationMapper.selectMaps(queryWrapper);

        List<String> listPSL = new ArrayList<>();
        //??????????????????ID???????????????????????????list???
        List<String> listTemp = new ArrayList<>();
        listPSL.add(inventoryInformation.getCargoSpaceId());
        if (insert > 0) {
            //??????maplist??????????????????????????????list<String>
            for (Map map : maplist) {
                //????????????????????????????????????????????????listPsl????????????
                if (MapUtil.isNotEmpty(map)) {
                    String s = map.get("priorityStorageLocation").toString();
                    listTemp = Arrays.stream(StringUtils.split(s, ",")).map(s1 -> s1.trim()).collect(Collectors.toList());
                }
                listPSL = Stream.of(listPSL, listTemp)
                        .flatMap(Collection::stream).distinct().collect(Collectors.toList());
            }

            //????????????????????????????????????????????????
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());
            updateWrapper.eq("warehouse_id", warehouseId);
            InventoryInformation inventoryInformationUpdate = new InventoryInformation();
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            inventoryInformationUpdate.setPriorityStorageLocation(resultString);
            int result = inventoryInformationMapper.update(inventoryInformationUpdate, updateWrapper);
            //??????????????????????????????????????? ???????????? 0 ??????
            QueryWrapper<InventoryInformation> inventoryInformationQueryWrapper = new QueryWrapper<>();
            inventoryInformationQueryWrapper.eq("warehouse_id", warehouseId);
            inventoryInformationQueryWrapper.eq("material_coding",addInventoryInformationDTO.getMaterialCoding());
            inventoryInformationQueryWrapper.eq("inventory_credit",0);
            inventoryInformationQueryWrapper.likeLeft("cargo_space_id","0000");
            inventoryInformationQueryWrapper.eq("batch",addInventoryInformationDTO.getBatch());
            InventoryInformation i = inventoryInformationMapper.selectOne(inventoryInformationQueryWrapper);
            inventoryInformationMapper.deleteById(i.getId());
            return Result.success("??????????????????");
        } else {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "?????????????????????");
        }

    }

    @Override
    public List<InventoryInformation> getInventoryInformationByCargoSpaceId(String cargoSpaceId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cargo_space_id", cargoSpaceId);
        List<InventoryInformation> inventoryInformationList = inventoryInformationMapper.selectList(queryWrapper);
        return inventoryInformationList;
    }


    @Override
    public InventoryInformation getInventoryById(int id) {
        InventoryInformation inventoryInformationResult = inventoryInformationMapper.selectById(id);
        return inventoryInformationResult;
    }

    @Override
    public InventoryInformation getInventoryInformation(String materialCoding, String batch, String cargoSpaceId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("batch", batch);
        queryWrapper.eq("cargo_space_id", cargoSpaceId);
        return inventoryInformationMapper.selectOne(queryWrapper);
    }

    @Override
    public Double getNumByMaterialCodingAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(SUM(Inventory_credit),0) AS num");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        Map map = this.getMap(queryWrapper);
        return (Double) map.get("num");
    }


    @Override
    public Double getNumByMaterialCodingAndWarehouseIdOutTypeZero(String materialCoding, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(SUM(Inventory_credit),0) AS num");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.likeRight("cargo_space_id", warehouseId)
                .and(wrapper -> wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        Map map = this.getMap(queryWrapper);
        Double num = (Double) map.get("num");
        return num;
    }

    @Override
    public Double getNumByMaterialCodingAndWarehouseIdOutTypeOne(String materialCoding, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(SUM(Inventory_credit),0) AS num");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        queryWrapper.eq("is_verification", 1);
        queryWrapper.eq("is_enter", 1);
        Map map = this.getMap(queryWrapper);
        Double num = (Double) map.get("num");
        return num;
    }

    @Override
    public Double getNumByMaterialCodingAndBatchAndWarehouseId(String materialCoding, String batch, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(SUM(Inventory_credit),0) AS num");
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("batch", batch);
        Map map = this.getMap(queryWrapper);
        Double num = (Double) map.get("num");
        return num;
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByMaterialCodingAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByMaterialCodingAndWarehouseIdOutTypeZero(String materialCoding, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.likeRight("cargo_space_id", warehouseId)
                .and(wrapper -> wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByMaterialCodingAndWarehouseIdOutTypeOne(String materialCoding, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        queryWrapper.eq("is_verification", 1);
        queryWrapper.eq("is_enter", 1);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseId(String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.eq("warehouse_id", warehouseId);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseIdAndInventoryType(String warehouseId, Integer InventoryType) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        if (InventoryType == 0) {
            queryWrapper.and(wrapper -> wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        }
        queryWrapper.eq("is_verification", 1);
        queryWrapper.eq("is_enter", 1);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseIdAndConsignor(String warehouseId, Integer consignor) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        if (consignor == 0) {
            queryWrapper.eq("consignor", consignor);
        } else {
            queryWrapper.ne("consignor", 0);
        }
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(String warehouseId, Integer InventoryType, Integer consignor) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit", 0);
        queryWrapper.eq("warehouse_id", warehouseId);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        if (InventoryType == 0) {
            queryWrapper.and(wrapper -> wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        } else {
            queryWrapper.eq("is_verification", 1);
            queryWrapper.eq("is_enter", 1);
        }
        if (consignor == 0) {
            queryWrapper.eq("consignor", consignor);
        } else {
            queryWrapper.ne("consignor", 0);
        }
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(String materialCoding, String batch, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("batch", batch);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public HashMap getMaterialPrice(String materialCoding) {

        HashMap hashMap = new HashMap();

        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("unit_price", "sales_unit_price");
        queryWrapper.eq("material_coding", materialCoding);

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime previousDate = nowDate.minus(6, ChronoUnit.MONTHS);
        /**
         * ????????????????????????????????????????????????
         */
        String createDateStart = dtf1.format(previousDate);
        String createDateEnd = dtf1.format(nowDate);
        queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");
        List<InventoryInformation> inventoryInformationList = inventoryInformationMapper.selectList(queryWrapper);
        if (ObjectUtil.isAllEmpty(inventoryInformationList)) {
            hashMap.put("unit_price", BigDecimal.valueOf(0));
            hashMap.put("sales_unit_price", BigDecimal.valueOf(0));
            return hashMap;
        }

        BigDecimal uniPriceSum = BigDecimal.valueOf(0);
        BigDecimal salesPriceSum = BigDecimal.valueOf(0);
        for (InventoryInformation inventoryInformation : inventoryInformationList
        ) {
            //??????(????????????)
            uniPriceSum = NumberUtil.add(uniPriceSum, inventoryInformation.getUnitPrice());
            //??????(????????????)
            salesPriceSum = NumberUtil.add(salesPriceSum, inventoryInformation.getSalesUnitPrice());
        }
        hashMap.put("unit_price", NumberUtil.div(uniPriceSum, inventoryInformationList.size()));
        hashMap.put("sales_unit_price", NumberUtil.div(salesPriceSum, inventoryInformationList.size()));
        return hashMap;
    }

    @Override
    public Result<Page<InventoryInfoVo>> inventoryBill(InventoryInfoPage page) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.inventoryBill(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if (null != ii.getInDate()) {
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void inventoryBillExport(InventoryInfoPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.inventoryBill(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if (null != ii.getInDate()) {
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        String templatePath = ossProperties.getPath() + "templates/inventoryBill.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "?????????.xlsx", params, request, response);
    }

    @Override
    public Result<Page<InventoryInfoVo>> deadGoods(InventoryInfoPage page) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.deadGoods(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if (null != ii.getInDate()) {
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void deadGoodsExport(InventoryInfoPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.deadGoods(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if (null != ii.getInDate()) {
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        String templatePath = ossProperties.getPath() + "templates/deadGoods.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "?????????????????????????????????.xlsx", params, request, response);

    }

    @Override
    public Result<Page<InventoryInfoVo>> deadGoodsSettle(InventoryInfoPage page) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.deadGoodsSettle(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if (null != ii.getInDate()) {
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void deadGoodsSettleExport(InventoryInfoPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.deadGoodsSettle(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if (null != ii.getInDate()) {
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        params.put("consignorStr", DataUtil.getConsignor(page.getConsignor()));
        String templatePath = ossProperties.getPath() + "templates/deadGoodsSettle.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "?????????????????????????????????.xlsx", params, request, response);

    }

    @Override
    public Result<Object> getMaterialProfit(MaterialProfitParam param) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> material = inventoryInformationMapper.getMaterialProfitList(param);
        map.put("material", material);
        return Result.success(map);
    }

    @Override
    public Result<Object> getBelowSafetyStockMaterialWarning(String warehouseId) {
        Map<String, Object> map = new HashMap<>();
        List<SafeInventoryInfoVo> vos = inventoryInformationMapper.getBelowSafetyStockMaterialWarningByParam(warehouseId);
        map.put("material", vos);
        return Result.success(map);
    }

    @Override
    public Result<Object> getPreExpirationWarning(String warehouseId, Integer days) {
        Map<String, Object> map = new HashMap<>();
        if (days == null || days == 0) {
            days = 30;
        }
        List<PreExpirationInventoryInfoVo> vos = inventoryInformationMapper.getPreExpirationWarningByParam(warehouseId, days);
        map.put("material", vos);
        return Result.success(map);
    }


}
