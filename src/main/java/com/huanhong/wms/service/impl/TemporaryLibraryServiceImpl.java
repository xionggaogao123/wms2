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
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryDTO;
import com.huanhong.wms.entity.vo.TemporaryLibraryVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseDetailsMapper;
import com.huanhong.wms.mapper.TemporaryLibraryInventoryDetailsMapper;
import com.huanhong.wms.mapper.TemporaryLibraryMapper;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.ITemporaryLibraryService;
import com.huanhong.wms.SuperServiceImpl;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 临库库存表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
@Service
public class TemporaryLibraryServiceImpl extends SuperServiceImpl<TemporaryLibraryMapper, TemporaryLibrary> implements ITemporaryLibraryService {


    @Resource
    private TemporaryLibraryMapper temporaryLibraryMapper;

    @Resource
    private TemporaryLibraryInventoryDetailsMapper temporaryLibraryInventoryDetailsMapper;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Resource
    private TemporaryEnterWarehouseDetailsMapper temporaryEnterWarehouseDetailsMapper;

    @Override
    public Page<TemporaryEnterWarehouseDetails> pageFuzzyQuery(Page<TemporaryEnterWarehouseDetails> temporaryLibraryPage, TemporaryLibraryVO temporaryLibraryVO) {

        //新建QueryWrapper对象
        QueryWrapper<TemporaryEnterWarehouseDetails> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(temporaryLibraryVO)) {
            return temporaryEnterWarehouseDetailsMapper.selectPage(temporaryLibraryPage,query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getEnterNumber()), "enter_number", temporaryLibraryVO.getEnterNumber());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getMaterialCoding()), "material_coding", temporaryLibraryVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getMaterialName()),"material_name",temporaryLibraryVO.getMaterialName());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getWarehouseId()),"warehouse_id",temporaryLibraryVO.getWarehouseId());


        return temporaryEnterWarehouseDetailsMapper.selectPage(temporaryLibraryPage,query);

    }


    @Override
    public Result addTemporaryLibrary(AddTemporaryLibraryDTO addTemporaryLibraryDTO) {
        /**
         * 判断货位是否存在
         */
        CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(addTemporaryLibraryDTO.getCargoSpaceId());

        if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
            return Result.failure(ErrorCode.DATA_IS_NULL, "货位不存在！");
        }

        /**
         * 同一批次的一种物料在某一个货位上只有一条数据
         * 1.若批次 物料 货位已经存在，则调用更新方法增加库存数量
         */

        TemporaryLibrary temporaryLibraryIsExist = getTemporaryLibrary(addTemporaryLibraryDTO.getMaterialCoding(), addTemporaryLibraryDTO.getBatch(), addTemporaryLibraryDTO.getCargoSpaceId());
        if (ObjectUtil.isNotEmpty(temporaryLibraryIsExist)) {
            //已有数量
            Double temporaryCreditOld = temporaryLibraryIsExist.getInventoryCredit();
            //新增数量
            Double temporaryCreditNew = addTemporaryLibraryDTO.getInventoryCredit();
            //存入最终数量
            temporaryLibraryIsExist.setInventoryCredit(NumberUtil.add(temporaryCreditOld, temporaryCreditNew));
            //将查出的ID存入updateDTO
            int i = temporaryLibraryMapper.updateById(temporaryLibraryIsExist);
            if (i > 0) {
                return Result.success("此货位存在同批次的同种物料,已合并数量");
            } else {
                return Result.failure("此货位存在同批次的同种物料,合并数量失败(库存信息或被其他人改动,请重试)");
            }
        }

//        /**
//         * 根据货位编码前四位获取当前仓库-库位编号前四位
//         */
//        String warehouseId = addTemporaryLibraryDTO.getWarehouseId();
//        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.select("priority_storage_location");
//        queryWrapper.like("warehouse_id", warehouseId);
//        queryWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());

        /**
         * 插入本次新增的库存
         */
        TemporaryLibrary temporaryLibrary= new TemporaryLibrary();
        BeanUtil.copyProperties(addTemporaryLibraryDTO, temporaryLibrary);
        int insert = temporaryLibraryMapper.insert(temporaryLibrary);
        if (insert>0){
//
//        //将同一库、同一物料所有的推荐存放位置放入list
//        List<Map<String, Object>> maplist = inventoryInformationMapper.selectMaps(queryWrapper);
//
//        List<String> listPSL = new ArrayList<>();
//        //将本次的货位ID放入推荐存放位置的list中
//        List<String> listTemp = new ArrayList<>();
//        listPSL.add(inventoryInformation.getCargoSpaceId());
//        if (insert > 0) {
//            //遍历maplist将优先存放位置转换为list<String>
//            for (Map map : maplist) {
//                //若优先存放位置不为空，获取值放入listPsl准备查重
//                if (MapUtil.isNotEmpty(map)) {
//                    String s = map.get("priorityStorageLocation").toString();
//                    listTemp = Arrays.stream(StringUtils.split(s, ",")).map(s1 -> s1.trim()).collect(Collectors.toList());
//                }
//                listPSL = Stream.of(listPSL, listTemp)
//                        .flatMap(Collection::stream).distinct().collect(Collectors.toList());
//            }

//            //更新同一库同一物料的推荐存放位置
//            UpdateWrapper updateWrapper = new UpdateWrapper();
//            updateWrapper.eq("material_coding", addInventoryInformationDTO.getMaterialCoding());
//            updateWrapper.eq("warehouse_id", warehouseId);
//            InventoryInformation inventoryInformationUpdate = new InventoryInformation();
//            String[] strings = listPSL.toArray(new String[listPSL.size()]);
//            String resultString = StringUtil.join(strings, ",");
//            inventoryInformationUpdate.setPriorityStorageLocation(resultString);
//            int result = inventoryInformationMapper.update(inventoryInformationUpdate, updateWrapper);
            return Result.success("新增库存成功");
        } else {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "新增库存失败！");
        }
    }

    @Override
    public Result updateTemporaryLibrary(UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO) {
        /**
         * 判断货位是否存在
         */
        if (StringUtils.isNotBlank(updateTemporaryLibraryDTO.getCargoSpaceId())) {
            CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(updateTemporaryLibraryDTO.getCargoSpaceId());
            if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "货位不存在！");
            }
        }
        TemporaryLibrary temporaryLibraryOld = getTemporaryLibraryById(updateTemporaryLibraryDTO.getId());
        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */
        BeanUtil.copyProperties(updateTemporaryLibraryDTO,temporaryLibraryOld);

//        /**
//         * 比对前后库存数量是否相同，若不同则更新 最近一次库存数量更新时间字段
//         */
//        int event = NumberUtil.compare(temporaryLibraryOld.getInventoryCredit(),updateTemporaryLibraryDTO.getInventoryCredit());
//        if (event!=0){
//            temporaryLibraryOld.setLastUpdateInventoryCredit(LocalDateTime.parse(DateUtil.now()));
//        }

        int i = temporaryLibraryMapper.updateById(temporaryLibraryOld);
        if (i > 0) {
            return Result.success("更新成功！");
        }
        return Result.failure("更新失败,库存信息或被其他人改动,请重试");
    }

    @Override
    public TemporaryLibrary getTemporaryLibraryById(int id) {
        return temporaryLibraryMapper.selectById(id);
    }

    @Override
    public TemporaryLibrary getTemporaryLibrary(String materialCoding, String batch, String cargoSpaceId) {
        QueryWrapper<TemporaryLibrary> queryWrapper = new QueryWrapper();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("batch", batch);
        queryWrapper.eq("cargo_space_id", cargoSpaceId);
        return temporaryLibraryMapper.selectOne(queryWrapper);
    }

    @Override
    public List<TemporaryLibrary> getTemporaryLibraryListByMaterialCodingAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<TemporaryLibrary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        return temporaryLibraryMapper.selectList(queryWrapper);
    }

    @Override
    public List<TemporaryLibrary> getTemporaryLibraryListByWarehouseId(String warehouseId) {
        QueryWrapper<TemporaryLibrary> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        return temporaryLibraryMapper.selectList(queryWrapper);
    }

    @Override
    public Double getNumByMaterialCodingAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<TemporaryLibrary> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(SUM(Inventory_credit),0) AS num");
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        Map map = this.getMap(queryWrapper);
        return (Double) map.get("num");
    }

    @Override
    public Double getNumByMaterialCodingAndBatchAndWarehouseId(String materialCoding, String batch, String warehouseId) {
        QueryWrapper<TemporaryLibrary> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(SUM(Inventory_credit),0) AS num");
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("batch", batch);
        Map map = this.getMap(queryWrapper);
        return (Double) map.get("num");
    }

    @Override
    public List<TemporaryLibrary> getInventoryInformationListByMaterialCodingAndWarehouseId(String materialCoding, String warehouseId) {
        QueryWrapper<TemporaryLibrary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.ne("inventory_credit",0);
        queryWrapper.likeRight("cargo_space_id", warehouseId);
        return temporaryLibraryMapper.selectList(queryWrapper);
    }
}
