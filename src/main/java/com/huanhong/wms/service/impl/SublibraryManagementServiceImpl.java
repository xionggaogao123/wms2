package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.SublibraryVO;
import com.huanhong.wms.mapper.SublibraryManagementMapper;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.ISublibraryManagementService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 子库管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class SublibraryManagementServiceImpl extends SuperServiceImpl<SublibraryManagementMapper, SublibraryManagement> implements ISublibraryManagementService {

    @Resource
    private SublibraryManagementMapper sublibraryManagementMapper;

    @Override
    public List<SublibraryManagement> getSublibraryManagementByWarehouseId(String warehouseId) {
        QueryWrapper<SublibraryManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", warehouseId);
        List<SublibraryManagement> sublibraryManagementList = sublibraryManagementMapper.selectList(wrapper);
        return sublibraryManagementList;
    }

    @Override
    public SublibraryManagement getSublibraryBySublibraryId(String sublibraryId) {
        QueryWrapper<SublibraryManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("sublibrary_id", sublibraryId);
        SublibraryManagement sublibraryManagement = sublibraryManagementMapper.selectOne(wrapper);
        return sublibraryManagement;
    }

    @Override
    public Page<SublibraryManagement> pageFuzzyQuery(Page<SublibraryManagement> SublibraryManagementPage, SublibraryVO sublibraryVO) {

        //新建QueryWrapper对象
        QueryWrapper<SublibraryManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(sublibraryVO)) {
            return baseMapper.selectPage(SublibraryManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(sublibraryVO.getWarehouseId()), "warehouse_id", sublibraryVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(sublibraryVO.getSublibraryId()), "sublibrary_id", sublibraryVO.getSublibraryId());

        query.like(StringUtils.isNotBlank(sublibraryVO.getSublibraryName()), "sublibrary_name", sublibraryVO.getSublibraryName());

        return baseMapper.selectPage(SublibraryManagementPage, query);
    }
}
