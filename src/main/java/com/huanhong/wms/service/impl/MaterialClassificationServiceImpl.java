package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.TreeUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.MaterialClassification;
import com.huanhong.wms.entity.vo.MaterialClassficationVO;
import com.huanhong.wms.mapper.MaterialClassificationMapper;
import com.huanhong.wms.service.IMaterialClassificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物料分类 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-16
 */
@Service
public class MaterialClassificationServiceImpl extends SuperServiceImpl<MaterialClassificationMapper, MaterialClassification> implements IMaterialClassificationService {

    @Resource
    private MaterialClassificationMapper materialClassificationMapper;

    @Resource
    private TreeUtils treeUtils;

    /**
     * 分页组合查询
     *
     * @param materialClassificationPage
     * @param materialClassificationVO
     * @return
     */
    @Override
    public Page<MaterialClassification> pageFuzzyQuery(Page<MaterialClassification> materialClassificationPage, MaterialClassficationVO materialClassificationVO) {
        //新建QueryWrapper对象
        QueryWrapper<MaterialClassification> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(materialClassificationVO)) {
            return materialClassificationMapper.selectPage(materialClassificationPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(ObjectUtil.isNotEmpty(materialClassificationVO.getLevelType()), "level_type", materialClassificationVO.getLevelType());

        query.like(StringUtils.isNotBlank(materialClassificationVO.getParentCode()), "parent_code", materialClassificationVO.getParentCode());

        query.like(StringUtils.isNotBlank(materialClassificationVO.getTypeName()), "type_name", materialClassificationVO.getTypeName());

        query.like(StringUtils.isNotBlank(materialClassificationVO.getTypeCode()), "type_code", materialClassificationVO.getTypeCode());

        return baseMapper.selectPage(materialClassificationPage, query);
    }

    @Override
    public MaterialClassification getMaterialClassificationByTypeCode(String typeCode) {
        QueryWrapper<MaterialClassification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_code", typeCode);
        return materialClassificationMapper.selectOne(queryWrapper);
    }

    @Override
    public List<MaterialClassification> getMaterialClassificationListByParentCode(String parentCode) {
        QueryWrapper<MaterialClassification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_code", parentCode);
        return materialClassificationMapper.selectList(queryWrapper);
    }

    /**
     * @param materialClassification
     * @return
     */
    @Override
    public Integer deleteMaterialClassification(MaterialClassification materialClassification) {

        //判断用户删除的是那一分类等级
        Integer levelType = materialClassification.getLevelType();
        QueryWrapper<MaterialClassification> wrapper = new QueryWrapper<>();

        int i = 0;
        //删除大类
        if (levelType == 0) {
            /**
             * 1.根据提交的大类的分类编码获取所有中类编码
             * 2.除了删除这些中类还要删除这些中类下的所有小类
             */
            List<Integer> listId = new ArrayList<>();
            //中类列表
            List<MaterialClassification> listMID = getMaterialClassificationListByParentCode(materialClassification.getTypeCode());
            //大类编码的ID
            listId.add(materialClassification.getId());
            //中类编码的ID列表
            for (MaterialClassification classification : listMID) {
                listId.add(classification.getId());
                //小类编码的ID列表
                List<MaterialClassification> listMIN = getMaterialClassificationListByParentCode(classification.getTypeCode());
                for (MaterialClassification value : listMIN) {
                    listId.add(value.getId());
                }
            }
            i = materialClassificationMapper.deleteBatchIds(listId);
            return i;
        }

        //删除中类
        if (levelType == 1) {
            List<Integer> listId = new ArrayList<>();
            //删除中类
            listId.add(materialClassification.getId());
            //删除小类
            //小类编码的ID列表
            List<MaterialClassification> listMIN = getMaterialClassificationListByParentCode(materialClassification.getTypeCode());
            for (MaterialClassification classification : listMIN) {
                listId.add(classification.getId());
            }
            i = materialClassificationMapper.deleteBatchIds(listId);
            return i;
        }

        //删除小类
        if (levelType == 2) {
            //删除小类
            wrapper.eq("type_code", materialClassification.getTypeCode());
            i = materialClassificationMapper.delete(wrapper);
            return i;
        }
        return i;
    }

    @Override
    public List<Map<String, Object>> getTreeNode() {
        QueryWrapper<MaterialClassification> queryWrapper = new QueryWrapper<>();
        List<MaterialClassification> resultList = materialClassificationMapper.selectList(queryWrapper);
        return treeUtils.menuList(resultList);
    }
}

