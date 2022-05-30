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
 * 库存表 服务实现类
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
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Autowired
    private OssProperties ossProperties;

    /**
     * 分页查询
     *
     * @param inventoryInformationPage
     * @param inventoryInformationVO
     * @return
     */
    @Override
    public Page<InventoryInformation> pageFuzzyQuery(Page<InventoryInformation> inventoryInformationPage, InventoryInformationVO inventoryInformationVO) {

        //新建QueryWrapper对象
        QueryWrapper<InventoryInformation> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(inventoryInformationVO)) {
            return inventoryInformationMapper.selectPage(inventoryInformationPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(inventoryInformationVO.getMaterialCoding()), "material_coding", inventoryInformationVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getMaterialName()), "material_name", inventoryInformationVO.getMaterialName());

//      query.like(StringUtils.isNotBlank(inventoryInformationVO.getCargoSpaceId()), "cargo_space_id", inventoryInformationVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getWarehouseId()),"warehouse_id",inventoryInformationVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getWarehouseAreaId()),"warehouse_area_id",inventoryInformationVO.getWarehouseAreaId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getWarehouseName()),"warehouse_name",inventoryInformationVO.getWarehouseName());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getCargoSpaceId()), "cargo_space_id", inventoryInformationVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getBatch()), "batch", inventoryInformationVO.getBatch());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getConsignor()), "consignor", inventoryInformationVO.getConsignor());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getSupplier()), "supplier", inventoryInformationVO.getSupplier());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getIsVerification()),"is_verification",inventoryInformationVO.getIsVerification());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getIsEnter()),"is_enter",inventoryInformationVO.getIsEnter());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getIsOnshelf()),"is_onshelf",inventoryInformationVO.getIsOnshelf());

        return baseMapper.selectPage(inventoryInformationPage, query);
    }

    /**
     * 根据物料编码和批次、货位更新库存信息
     */
    @Override
    public Result updateInventoryInformation(UpdateInventoryInformationDTO updateInventoryInformationDTO) {

        /**
         * 判断货位是否存在
         */
        if (StringUtils.isNotBlank(updateInventoryInformationDTO.getCargoSpaceId())) {
            CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(updateInventoryInformationDTO.getCargoSpaceId());
            if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "货位不存在！");
            }
        }
        InventoryInformation inventoryInformationOld = getInventoryById(updateInventoryInformationDTO.getId());
        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */
        BeanUtil.copyProperties(updateInventoryInformationDTO,inventoryInformationOld);

        /**
         * 比对前后库存数量是否相同，若不同则更新 最近一次库存数量更新时间字段
         */
        int event = NumberUtil.compare(inventoryInformationOld.getInventoryCredit(),updateInventoryInformationDTO.getInventoryCredit());
        if (event!=0){
            inventoryInformationOld.setLastUpdateInventoryCredit(LocalDateTime.parse(DateUtil.now()));
        }

        int i = inventoryInformationMapper.updateById(inventoryInformationOld);
        if (i > 0) {
            return Result.success("更新成功！");
        }
        return Result.failure("更新失败,库存信息或被其他人改动,请重试");
    }

    /**
     * 库存新增
     *
     * @param addInventoryInformationDTO
     * @return
     */
    @Override
    public Result addInventoryInformation(AddInventoryInformationDTO addInventoryInformationDTO) {
        /**
         * 判断货位是否存在
         */
        CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(addInventoryInformationDTO.getCargoSpaceId());
        if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
            return Result.failure(ErrorCode.DATA_IS_NULL, "货位不存在！");
        }

        /**
         * 同一批次的一种物料在某一个货位上只有一条数据
         * 1.若批次 物料 货位已经存在，则调用更新方法增加库存数量
         */
//        InventoryInformationVO inventoryInformationVO = new InventoryInformationVO();
//        BeanUtil.copyProperties(addInventoryInformationDTO, inventoryInformationVO);
        InventoryInformation inventoryInformationIsExist = getInventoryInformation(addInventoryInformationDTO.getMaterialCoding(), addInventoryInformationDTO.getBatch(), addInventoryInformationDTO.getCargoSpaceId());
        if (ObjectUtil.isNotEmpty(inventoryInformationIsExist)) {
            //已有数量
            Double inventoryCreditOld = inventoryInformationIsExist.getInventoryCredit();
            //新增数量
            Double inventoryCreditNew = addInventoryInformationDTO.getInventoryCredit();
            //存入最终数量
            inventoryInformationIsExist.setInventoryCredit(NumberUtil.add(inventoryCreditOld, inventoryCreditNew));
//            inventoryInformationIsExist.setVersion(12);
            //将查出的ID存入updateDTO
            int i = inventoryInformationMapper.updateById(inventoryInformationIsExist);
            if (i > 0) {
                return Result.success("此货位存在同批次的同种物料,已合并数量");
            } else {
                return Result.failure("此货位存在同批次的同种物料,合并数量失败(库存信息或被其他人改动,请重试)");
            }
        }

        /**
         * 根据货位编码前四位获取当前仓库-库位编号前四位
         */
        String warehouseId = addInventoryInformationDTO.getWarehouseId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("priority_storage_location");
        queryWrapper.like("warehouse_id", warehouseId);
        queryWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());

        /**
         * 插入本次新增的库存
         */
        InventoryInformation inventoryInformation = new InventoryInformation();
        BeanUtil.copyProperties(addInventoryInformationDTO, inventoryInformation);
        int insert = inventoryInformationMapper.insert(inventoryInformation);

        //将同一库、同一物料所有的推荐存放位置放入list
        List<Map<String, Object>> maplist = inventoryInformationMapper.selectMaps(queryWrapper);

        List<String> listPSL = new ArrayList<>();
        //将本次的货位ID放入推荐存放位置的list中
        List<String> listTemp = new ArrayList<>();
        listPSL.add(inventoryInformation.getCargoSpaceId());
        if (insert > 0) {
            //遍历maplist将优先存放位置转换为list<String>
            for (Map map : maplist) {
                //若优先存放位置不为空，获取值放入listPsl准备查重
                if (MapUtil.isNotEmpty(map)) {
                    String s = map.get("priorityStorageLocation").toString();
                    listTemp = Arrays.stream(StringUtils.split(s, ",")).map(s1 -> s1.trim()).collect(Collectors.toList());
                }
                listPSL = Stream.of(listPSL, listTemp)
                        .flatMap(Collection::stream).distinct().collect(Collectors.toList());
            }

            //更新同一库同一物料的推荐存放位置
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());
            updateWrapper.eq("warehouse_id", warehouseId);
            InventoryInformation inventoryInformationUpdate = new InventoryInformation();
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            inventoryInformationUpdate.setPriorityStorageLocation(resultString);
            int result = inventoryInformationMapper.update(inventoryInformationUpdate, updateWrapper);
            materialPrice.addMaterialPrice(inventoryInformationUpdate.getMaterialCoding(),inventoryInformationUpdate.getMaterialName());
            return Result.success("新增库存成功");
        } else {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "新增库存失败！");
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
                .and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
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
        queryWrapper.eq("is_verification",1);
        queryWrapper.eq("is_enter",1);
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
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByMaterialCodingAndWarehouseIdOutTypeZero(String materialCoding, String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.likeRight("cargo_space_id", warehouseId)
                .and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByMaterialCodingAndWarehouseIdOutTypeOne(String materialCoding, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        queryWrapper.eq("is_verification",1);
        queryWrapper.eq("is_enter",1);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseId(String warehouseId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.eq("warehouse_id",warehouseId);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseIdAndInventoryType(String warehouseId, Integer InventoryType) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.eq("warehouse_id",warehouseId);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        if (InventoryType==0){
                    queryWrapper.and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        }
        queryWrapper.eq("is_verification",1);
        queryWrapper.eq("is_enter",1);
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseIdAndConsignor(String warehouseId, Integer consignor) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.eq("warehouse_id",warehouseId);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        if (consignor==0){
            queryWrapper.eq("consignor",consignor);
        }else {
            queryWrapper.ne("consignor",0);
        }
        return inventoryInformationMapper.selectList(queryWrapper);
    }

    @Override
    public List<InventoryInformation> getInventoryInformationListByWarehouseIdAndInventoryTypeAndConsignor(String warehouseId, Integer InventoryType, Integer consignor) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.eq("warehouse_id",warehouseId);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        if (InventoryType==0){
            queryWrapper.and(wrapper->wrapper.eq("is_verification", 0).or().eq("is_enter", 0));
        }else {
            queryWrapper.eq("is_verification",1);
            queryWrapper.eq("is_enter",1);
        }
        if (consignor==0){
            queryWrapper.eq("consignor",consignor);
        }else {
            queryWrapper.ne("consignor",0);
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
        queryWrapper.select("unit_price","sales_unit_price");
        queryWrapper.eq("material_coding", materialCoding);

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime previousDate = nowDate.minus(6, ChronoUnit.MONTHS);
        /**
         * 查询半年内此物料的单价（内外部）
         */
        String createDateStart = dtf1.format(previousDate);
        String createDateEnd = dtf1.format(nowDate);
        queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");
        List<InventoryInformation> inventoryInformationList = inventoryInformationMapper.selectList(queryWrapper);
        if (ObjectUtil.isAllEmpty(inventoryInformationList)){
            hashMap.put("unit_price",BigDecimal.valueOf(0));
            hashMap.put("sales_unit_price",BigDecimal.valueOf(0));
            return hashMap;
        }

        BigDecimal uniPriceSum = BigDecimal.valueOf(0);
        BigDecimal salesPriceSum = BigDecimal.valueOf(0);
        for (InventoryInformation inventoryInformation:inventoryInformationList
             ) {
            //单价(泰丰盛和)
            uniPriceSum = NumberUtil.add(uniPriceSum,inventoryInformation.getUnitPrice());
            //单价(使用单位)
            salesPriceSum = NumberUtil.add(salesPriceSum,inventoryInformation.getSalesUnitPrice());
        }
        hashMap.put("unit_price",NumberUtil.div(uniPriceSum,inventoryInformationList.size()));
        hashMap.put("sales_unit_price",NumberUtil.div(salesPriceSum,inventoryInformationList.size()));
        return hashMap;
    }

    @Override
    public Result<Page<InventoryInfoVo>> inventoryBill(InventoryInfoPage page) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.inventoryBill(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if(null != ii.getInDate()){
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
            if(null != ii.getInDate()){
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        String templatePath = ossProperties.getPath() + "templates/inventoryBill.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "库存账.xlsx", params, request, response);
    }

    @Override
    public Result<Page<InventoryInfoVo>> deadGoods(InventoryInfoPage page) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.deadGoods(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if(null != ii.getInDate()){
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
            if(null != ii.getInDate()){
                ii.setInDay(DateUtil.betweenDay(ii.getInDate(), new Date(), true));
            }
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        String templatePath = ossProperties.getPath() + "templates/deadGoods.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "呆滞货物明细表（查询）.xlsx", params, request, response);

    }

    @Override
    public Result<Page<InventoryInfoVo>> deadGoodsSettle(InventoryInfoPage page) {
        Page<InventoryInfoVo> pageData = inventoryInformationMapper.deadGoodsSettle(page);
        int i = 1;
        for (InventoryInfoVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            if(null != ii.getInDate()){
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
            if(null != ii.getInDate()){
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
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "呆滞货物明细表（查询）.xlsx", params, request, response);

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
        if (days == null || days ==0 ){
            days = 30;
        }
        List<PreExpirationInventoryInfoVo> vos = inventoryInformationMapper.getPreExpirationWarningByParam(warehouseId,days);
        map.put("material", vos);
        return Result.success(map);
    }


}
