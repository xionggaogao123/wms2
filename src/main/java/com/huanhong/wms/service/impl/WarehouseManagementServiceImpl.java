package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.WarehouseVo;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.IWarehouseManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 仓库管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class WarehouseManagementServiceImpl extends SuperServiceImpl<WarehouseManagementMapper, WarehouseManagement> implements IWarehouseManagementService {

    @Resource
    private WarehouseManagementMapper warehouseManagementMapper;


    @Override
    public List<WarehouseManagement> getWarehouseByCompanyId(Integer CompanyId) {
        QueryWrapper<WarehouseManagement>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id", CompanyId);
        return warehouseManagementMapper.selectList(queryWrapper);
    }

    @Override
    public WarehouseManagement getWarehouseByWarehouseId(String WarehouseId) {
        QueryWrapper<WarehouseManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", WarehouseId);
        WarehouseManagement warehouseManagement = warehouseManagementMapper.selectOne(wrapper);
        return warehouseManagement;
    }

//    @Override
//    public List<String> fuzzyQuerySelectList(String field, String value) {
//        List<String> resultList = warehouseManagementMapper.fuzzyQuerySelectList(field, value);
//        return resultList;
//    }

    @Override
    public Page<WarehouseManagement> pageFuzzyQuery(Page<WarehouseManagement> warehouseManagementPage, WarehouseVo warehouseVo) {

        //新建QueryWrapper对象
        QueryWrapper<WarehouseManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehouseVo)) {
            return baseMapper.selectPage(warehouseManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(ObjectUtil.isNotEmpty(warehouseVo.getCompanyId()), "company_id", warehouseVo.getCompanyId());

        query.like(StringUtils.isNotBlank(warehouseVo.getWarehouseId()), "warehouse_id", warehouseVo.getWarehouseId());

        query.like(StringUtils.isNotBlank(warehouseVo.getWarehouseName()), "warehouse_name", warehouseVo.getWarehouseName());

        return baseMapper.selectPage(warehouseManagementPage, query);
    }

    /**
     * 查询仓库是否停用 0-使用中 1-停用
     * @param warehouseId
     * @return
     */
    @Override
    public int isStopUsing(String warehouseId) {
        WarehouseManagement warehouseManagement = getWarehouseByWarehouseId(warehouseId);
        return warehouseManagement.getStopUsing();
    }

}
