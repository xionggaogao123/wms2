package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.vo.ShelfVO;
import com.huanhong.wms.mapper.ShelfManagementMapper;
import com.huanhong.wms.service.IShelfManagementService;
import com.huanhong.wms.SuperServiceImpl;
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
    public ShelfManagement getShelfByWarehouseAreaId(String shelfId) {
        return null;
    }

    @Override
    public Page<ShelfManagement> pageFuzzyQuery(Page<ShelfManagement> shelfManagementPage, ShelfVO shelfVO) {
        return null;
    }
}
