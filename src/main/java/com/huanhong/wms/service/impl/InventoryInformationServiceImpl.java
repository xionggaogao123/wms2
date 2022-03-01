package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.IInventoryInformationService;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

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

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getBatch()), "batch", inventoryInformationVO.getBatch());

        query.like(ObjectUtil.isNotNull(inventoryInformationVO.getConsignor()), "consignor", inventoryInformationVO.getConsignor());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getSupplier()), "supplier", inventoryInformationVO.getSupplier());

        query.likeRight(StringUtils.isNotBlank(inventoryInformationVO.getParentCode()), "cargo_space_id", inventoryInformationVO.getParentCode());

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
        if (StringUtils.isNotBlank(updateInventoryInformationDTO.getCargoSpaceId())) {
            inventoryInformationOld.setCargoSpaceId(updateInventoryInformationDTO.getCargoSpaceId());
        }
        if (ObjectUtil.isNotNull(updateInventoryInformationDTO.getInventoryCredit())) {
            inventoryInformationOld.setInventoryCredit(updateInventoryInformationDTO.getInventoryCredit());
        }
        if (ObjectUtil.isNotEmpty(updateInventoryInformationDTO.getSafeQuantity())) {
            inventoryInformationOld.setSafeQuantity(updateInventoryInformationDTO.getSafeQuantity());
        }
        if (ObjectUtil.isNotNull(updateInventoryInformationDTO.getConsignor())) {
            inventoryInformationOld.setConsignor(updateInventoryInformationDTO.getConsignor());
        }
        if (ObjectUtil.isNotNull(updateInventoryInformationDTO.getEffectiveDate())) {
            inventoryInformationOld.setEffectiveDate(updateInventoryInformationDTO.getEffectiveDate());
        }
        if (ObjectUtil.isNotNull(updateInventoryInformationDTO.getUnitPrice())) {
            inventoryInformationOld.setUnitPrice(updateInventoryInformationDTO.getUnitPrice());
        }
        if (ObjectUtil.isNotNull(updateInventoryInformationDTO.getManagementFeeRate())) {
            inventoryInformationOld.setManagementFeeRate(updateInventoryInformationDTO.getManagementFeeRate());
        }
        if (ObjectUtil.isNotNull(updateInventoryInformationDTO.getSalesUnitPrice())) {
            inventoryInformationOld.setSalesUnitPrice(updateInventoryInformationDTO.getSalesUnitPrice());
        }
        if (StringUtils.isNotBlank(updateInventoryInformationDTO.getSupplier())) {
            inventoryInformationOld.setSupplier(updateInventoryInformationDTO.getSupplier());
        }
        if (StringUtils.isNotBlank(updateInventoryInformationDTO.getPriorityStorageLocation())) {
            inventoryInformationOld.setPriorityStorageLocation(updateInventoryInformationDTO.getPriorityStorageLocation());
        }
        if (StringUtils.isNotBlank(updateInventoryInformationDTO.getRemark())) {
            inventoryInformationOld.setRemark(updateInventoryInformationDTO.getRemark());
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
        InventoryInformationVO inventoryInformationVO = new InventoryInformationVO();
        BeanUtil.copyProperties(addInventoryInformationDTO, inventoryInformationVO);
        InventoryInformation inventoryInformationIsExist = getInventoryInformation(inventoryInformationVO);
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
        String parentCode = addInventoryInformationDTO.getCargoSpaceId().substring(0, 4);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("priority_storage_location");
        queryWrapper.likeRight("cargo_space_id", parentCode);
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
            updateWrapper.likeRight("cargo_space_id", parentCode);
            InventoryInformation inventoryInformationUpdate = new InventoryInformation();
            String[] strings = listPSL.toArray(new String[listPSL.size()]);
            String resultString = StringUtil.join(strings, ",");
            inventoryInformationUpdate.setPriorityStorageLocation(resultString);
            inventoryInformationMapper.update(inventoryInformationUpdate, updateWrapper);
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


    /**
     * @param inventoryInformationVO
     * @return
     */
    @Override
    public InventoryInformation getInventoryInformation(InventoryInformationVO inventoryInformationVO) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cargo_space_id", inventoryInformationVO.getCargoSpaceId());
        queryWrapper.eq("material_coding", inventoryInformationVO.getMaterialCoding());
        queryWrapper.eq("batch", inventoryInformationVO.getBatch());
        return inventoryInformationMapper.selectOne(queryWrapper);
    }


    @Override
    public InventoryInformation getInventoryById(int id) {
        InventoryInformation inventoryInformationResult = inventoryInformationMapper.selectById(id);
        return inventoryInformationResult;
    }
}
