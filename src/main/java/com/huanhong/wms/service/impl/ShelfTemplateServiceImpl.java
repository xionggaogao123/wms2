package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.ShelfTemplate;
import com.huanhong.wms.entity.vo.ShelfTemplateVO;
import com.huanhong.wms.mapper.ShelfTemplateMapper;
import com.huanhong.wms.service.IShelfTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-06
 */
@Service
public class ShelfTemplateServiceImpl extends SuperServiceImpl<ShelfTemplateMapper, ShelfTemplate> implements IShelfTemplateService {

    @Resource
    private  ShelfTemplateMapper shelfTemplateMapper;

    @Override
    public ShelfTemplate getShelfTemplateByID(Integer id) {
        ShelfTemplate shelfTemplate = shelfTemplateMapper.selectById(id);
        return shelfTemplate;
    }

    @Override
    public Page<ShelfTemplate> pageFuzzyQuery(Page<ShelfTemplate> shelfTemplatePage, ShelfTemplateVO shelfTemplateVO) {
        //新建QueryWrapper对象
        QueryWrapper<ShelfTemplate> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(shelfTemplateVO)) {
            return baseMapper.selectPage(shelfTemplatePage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(shelfTemplateVO.getWarehouseId()), "warehouse_id", shelfTemplateVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(shelfTemplateVO.getShelfTemplateName()), "shelf_template_name", shelfTemplateVO.getShelfTemplateName());

        query.like(ObjectUtil.isNotEmpty(shelfTemplateVO.getShelfType()), "shelf_type", shelfTemplateVO.getShelfType());

        return baseMapper.selectPage(shelfTemplatePage, query);
    }

    @Override
    public List<ShelfTemplate> ListQueryByWarehouseId(String warehouseId) {
        QueryWrapper<ShelfTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id",warehouseId);
        List<ShelfTemplate> shelfTemplateList = shelfTemplateMapper.selectList(queryWrapper);
        return shelfTemplateList;
    }
}
