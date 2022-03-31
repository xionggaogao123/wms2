package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.vo.MaterialVO;
import com.huanhong.wms.entity.vo.PdaMaterialVO;
import com.huanhong.wms.mapper.MaterialMapper;
import com.huanhong.wms.service.IMaterialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 材料 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-22
 */
@Service
public class MaterialServiceImpl extends SuperServiceImpl<MaterialMapper, Material> implements IMaterialService {

    @Resource
    private MaterialMapper materialMapper;

    /**
     * 通过物料编码获取物料信息
     *
     * @param materialCode
     * @return
     */
    @Override
    public Material getMeterialByMeterialCode(String materialCode) {
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding",materialCode);
        Material material = materialMapper.selectOne(queryWrapper);
        return material;
    }

//    /**
//     * 通过物料名称获取物料
//     *
//     * @param meterialName
//     * @return
//     */
//    @Override
//    public Meterial getMeterialByMeterialName(String meterialName) {
//        Meterial meterial = meterialMapper.getMeterialByMeterialCode(meterialName);
//        return meterial;
//    }

    @Override
    public List<Material> listFuzzyQuery(String typeCode) {
        //新建QueryWrapper对象
        QueryWrapper<Material> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");

        query.likeRight("type_code",typeCode);

        return baseMapper.selectList(query);
    }

    /**
     * 查询某物料是否停用  0-使用中  1-停用
     * @param materialCode
     * @return
     */
    @Override
    public int isStopUsing(String materialCode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("material_coding",materialCode);
        queryWrapper.eq("stop_using",1);
        int count = materialMapper.selectCount(queryWrapper);
        return count;
    }

    /**
     * 根据物料编码或物料名称模糊查询物料
     * @return
     */
    @Override
    public List<Material> getMaterialListByKey(PdaMaterialVO pdaMaterialVO) {
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("material_coding",key).or().like("material_name",key);
        queryWrapper.like(StringUtils.isNotBlank(pdaMaterialVO.getMaterialCoding()), "material_coding", pdaMaterialVO.getMaterialCoding());
        queryWrapper.like(StringUtils.isNotBlank(pdaMaterialVO.getMaterialName()), "material_name", pdaMaterialVO.getMaterialName());
        List<Material> materialslist = materialMapper.selectList(queryWrapper);
        return materialslist;
    }


    /**
     * 条件组合模糊分页查询
     * @param meterialPage
     * @param materialVO
     * @return
     */
    @Override
    public Page pageFuzzyQuery(Page meterialPage, MaterialVO materialVO) {

        //新建QueryWrapper对象
        QueryWrapper<Material> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(materialVO)) {
            return baseMapper.selectPage(meterialPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        //物料编码
        query.like(StringUtils.isNotBlank(materialVO.getMaterialCoding()), "material_coding", materialVO.getMaterialCoding());

        //物料名称
        query.like(StringUtils.isNotBlank(materialVO.getMaterialName()), "material_name", materialVO.getMaterialName());

        //俗称
        query.like(StringUtils.isNotBlank(materialVO.getSlang()), "slang", materialVO.getSlang());

        //分类编码
        query.likeRight(StringUtils.isNotBlank(materialVO.getTypeCode()),"type_code",materialVO.getTypeCode());

        //规格型号
        query.like(StringUtils.isNotBlank(materialVO.getSpecificationModel()), "specification_model", materialVO.getSpecificationModel());

        //计量单位
        query.like(StringUtils.isNotBlank(materialVO.getMeasurementUnit()), "measurement_unit", materialVO.getMeasurementUnit());

        //辅助单位
        query.like(StringUtils.isNotBlank(materialVO.getAuxiliaryUnit()), "auxiliary_unit", materialVO.getAuxiliaryUnit());

        //品牌
        query.like(StringUtils.isNotBlank(materialVO.getBrand()), "brand", materialVO.getBrand());

        //物料材质
        query.like(StringUtils.isNotBlank(materialVO.getMaterial()), "material", materialVO.getMaterial());

        //执行标准
        query.like(StringUtils.isNotBlank(materialVO.getExecutiveStandard()), "executive_standard", materialVO.getExecutiveStandard());

        //技术要求
        query.like(StringUtils.isNotBlank(materialVO.getSkillsRequiremen()), "skills_requiremen", materialVO.getSkillsRequiremen());

        //物料图号
        query.like(StringUtils.isNotBlank(materialVO.getDrawingNumber()), "drawing_number", materialVO.getMeasurementUnit());

        //生产厂家
        query.like(StringUtils.isNotBlank(materialVO.getSupplier()), "supplier", materialVO.getSupplier());

        //安全质量标准
        query.like(StringUtils.isNotBlank(materialVO.getSafetyQualityStandards()), "safety_quality_standards", materialVO.getSafetyQualityStandards());

        //停用
        query.eq(StringUtils.isNotBlank(String.valueOf(materialVO.getStopUsing())),"stop_using",materialVO.getStopUsing());

        return baseMapper.selectPage(meterialPage, query);
    }

}
