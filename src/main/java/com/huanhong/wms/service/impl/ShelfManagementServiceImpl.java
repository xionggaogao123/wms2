package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
        wrapper.eq("del",0);
        wrapper.eq("warehouse_area_id", warehouseAreaId);
        List<ShelfManagement> ShelfList = shelfManagementMapper.selectList(wrapper);
        return ShelfList;
    }

    @Override
    public ShelfManagement getShelfByShelfId(String shelfId) {
        QueryWrapper<ShelfManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("shelf_id", shelfId);
        wrapper.eq("del",0);
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

    /**
     * 查询某货架是否停用 0- 使用中  1- 停用
     * @param shelfId
     * @return
     */
    @Override
    public int isStopUsing(String shelfId) {
        ShelfManagement shelfManagement = getShelfByShelfId(shelfId);
        return shelfManagement.getStopUsing();
    }

    /**
     *
     * @param parentCode
     * @param enable true = 随父级启用  false = 随父级停用
     * @return
     */
    @Override
    public int stopUsingByParentCode(String parentCode, boolean enable) {
        UpdateWrapper updateWrapper= new UpdateWrapper();
        ShelfManagement shelfManagement = new ShelfManagement();
        if (enable){
            //启用所有子级
            shelfManagement.setStopUsing(0);
        }else {
            //停用所有子级
            shelfManagement.setStopUsing(1);
        }
        //更新所有库区编号模糊匹配的货架
        //从库区开始 有2级及以上父级所以需要likeRight 自己的父编码
        updateWrapper.likeRight("warehouse_area_id", parentCode);
        return shelfManagementMapper.update(shelfManagement, updateWrapper);
    }
}
