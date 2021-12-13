package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.vo.WarehouseAreaVO;
import com.huanhong.wms.mapper.WarehouseAreaManagementMapper;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.IWarehouseAreaManagementService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 库房区域管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class WarehouseAreaManagementServiceImpl extends SuperServiceImpl<WarehouseAreaManagementMapper, WarehouseAreaManagement> implements IWarehouseAreaManagementService {

    @Resource
    private WarehouseAreaManagementMapper warehouseAreaManagementMapper;

    //根据子库ID获取对应库区
    @Override
    public List<WarehouseAreaManagement> getWarehouseAreaListBySublibraryId(String sublibraryId) {
        QueryWrapper<WarehouseAreaManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("sublibrary_id", sublibraryId);
        List<WarehouseAreaManagement> warehouseAreaList = warehouseAreaManagementMapper.selectList(wrapper);
        return warehouseAreaList;
    }
    //根据库区ID获取对应库区信息
    @Override
    public WarehouseAreaManagement getWarehouseAreaByWarehouseAreaId(String WarehouseAreaId) {
        QueryWrapper<WarehouseAreaManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_area_id", WarehouseAreaId);
        WarehouseAreaManagement warehouseAreaManagement = warehouseAreaManagementMapper.selectOne(wrapper);
        return warehouseAreaManagement;
    }

    //分页组合模糊查询
    @Override
    public Page<WarehouseAreaManagement> pageFuzzyQuery(Page<WarehouseAreaManagement> WarehouseAreaManagementPage, WarehouseAreaVO warehouseAreaVO) {
        //新建QueryWrapper对象
        QueryWrapper<WarehouseAreaManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehouseAreaVO)) {
            return baseMapper.selectPage(WarehouseAreaManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(warehouseAreaVO.getSubLibraryId()), "sublibrary_id", warehouseAreaVO.getSubLibraryId());

        query.like(StringUtils.isNotBlank(warehouseAreaVO.getWarehouseAreaId()), "warehouse_area_id", warehouseAreaVO.getWarehouseAreaId());

        query.like(StringUtils.isNotBlank(warehouseAreaVO.getWarehouseAreaName()), "warehouse_area_name", warehouseAreaVO.getWarehouseAreaName());

        return baseMapper.selectPage(WarehouseAreaManagementPage, query);
    }
}
