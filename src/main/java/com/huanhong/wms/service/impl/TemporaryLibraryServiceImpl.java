package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.TemporaryLibrary;
import com.huanhong.wms.entity.vo.TemporaryLibraryVO;
import com.huanhong.wms.mapper.TemporaryLibraryMapper;
import com.huanhong.wms.service.ITemporaryLibraryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-18
 */
@Service
public class TemporaryLibraryServiceImpl extends SuperServiceImpl<TemporaryLibraryMapper, TemporaryLibrary> implements ITemporaryLibraryService {


    @Resource
    private TemporaryLibraryMapper temporaryLibraryMapper;

    /**
     * 分页查询
     * @param temporaryLibraryPage
     * @param temporaryLibraryVO
     * @return
     */
    @Override
    public Page<TemporaryLibrary> pageFuzzyQuery(Page<TemporaryLibrary> temporaryLibraryPage, TemporaryLibraryVO temporaryLibraryVO) {
        //新建QueryWrapper对象
        QueryWrapper<TemporaryLibrary> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(temporaryLibraryVO)) {
            return temporaryLibraryMapper.selectPage(temporaryLibraryPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getDocumentNumber()), "document_number", temporaryLibraryVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getMaterialCoding()), "material_coding", temporaryLibraryVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getMaterialName()), "material_name", temporaryLibraryVO.getMaterialName());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getBatch()), "batch", temporaryLibraryVO.getBatch());

        query.like(StringUtils.isNotBlank(temporaryLibraryVO.getCargoSpaceId()), "cargo_space_id", temporaryLibraryVO.getCargoSpaceId());

        return baseMapper.selectPage(temporaryLibraryPage, query);
    }

    /**
     * 根据物料编码和批次更新库存信息
     * @param temporaryLibrary
     * @return
     */
    @Override
    public int updateTemporaryLibrary(TemporaryLibrary temporaryLibrary) {
        UpdateWrapper<TemporaryLibrary> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("material_coding", temporaryLibrary.getMaterialCoding());
        updateWrapper.eq("Batch", temporaryLibrary.getBatch());
        updateWrapper.eq("cargo_space_id",temporaryLibrary.getCargoSpaceId());
        int i = temporaryLibraryMapper.update(temporaryLibrary, updateWrapper);
        return i;
    }
}
