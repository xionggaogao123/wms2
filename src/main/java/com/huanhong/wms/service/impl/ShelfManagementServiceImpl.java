package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.vo.ShelfVO;
import com.huanhong.wms.mapper.ShelfManagementMapper;
import com.huanhong.wms.service.IShelfManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 货架管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class ShelfManagementServiceImpl extends SuperServiceImpl<ShelfManagementMapper, ShelfManagement> implements IShelfManagementService {


    @Resource
    private ShelfManagementMapper shelfManagementMapper;


    @Override
    public List<ShelfManagement> getShelfListByWarehouseAreaId(String warehouseAreaId) {
        QueryWrapper<ShelfManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_area_id", warehouseAreaId);
        List<ShelfManagement> ShelfList = shelfManagementMapper.selectList(wrapper);
        return ShelfList;
    }

    @Override
    public ShelfManagement getShelfByShelfId(String shelfId) {
        QueryWrapper<ShelfManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("shelf_id", shelfId);
        ShelfManagement shelfManagement = shelfManagementMapper.selectOne(wrapper);
        return shelfManagement;
    }

    @Override
    public Page<ShelfManagement> pageFuzzyQuery(Page<ShelfManagement> shelfManagementPage, ShelfVO shelfVO) {
        //新建QueryWrapper对象
        QueryWrapper<ShelfManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(shelfVO)) {
            return baseMapper.selectPage(shelfManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(shelfVO.getWarehouseAreaId()), "warehouse_area_id", shelfVO.getWarehouseAreaId());

        query.like(StringUtils.isNotBlank(shelfVO.getShelfId()), "shelf_id", shelfVO.getShelfId());

        return baseMapper.selectPage(shelfManagementPage, query);
    }
}
