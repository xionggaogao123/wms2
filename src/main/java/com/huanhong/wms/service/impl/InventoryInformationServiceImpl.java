package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.IInventoryInformationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    private  InventoryInformationMapper inventoryInformationMapper;

    /**
     * 分页查询
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
            return inventoryInformationMapper.selectPage(inventoryInformationPage,query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(inventoryInformationVO.getMaterialCoding()), "material_coding",inventoryInformationVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getMaterialName()), "material_name", inventoryInformationVO.getMaterialName());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getCargoSpaceId()), "cargo_space_id", inventoryInformationVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getBatch()), "batch", inventoryInformationVO.getBatch());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getConsignor()), "consignor", inventoryInformationVO.getConsignor());

        query.like(StringUtils.isNotBlank(inventoryInformationVO.getSupplier()), "supplier", inventoryInformationVO.getSupplier());

        return baseMapper.selectPage(inventoryInformationPage, query);
    }

    /**
     * 根据物料编码和批次更新库存信息
     */
    @Override
    public int updateInventoryInformation(InventoryInformation inventoryInformation) {
        UpdateWrapper<InventoryInformation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("material_coding", inventoryInformation.getMaterialCoding());
        updateWrapper.eq("Batch",inventoryInformation.getBatch());
        int i = inventoryInformationMapper.update(inventoryInformation,updateWrapper);
        return i;
    }

    @Override
    public List<InventoryInformation> getInventoryInformationByCargoSpaceId(String cargoSpaceId) {
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cargo_space_id",cargoSpaceId);
        List<InventoryInformation> inventoryInformationList = inventoryInformationMapper.selectList(queryWrapper);
        return inventoryInformationList;
    }
}
