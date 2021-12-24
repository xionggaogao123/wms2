package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.entity.vo.MeterialVO;
import com.huanhong.wms.mapper.MeterialMapper;
import com.huanhong.wms.service.IMeterialService;
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
public class MeterialServiceImpl extends SuperServiceImpl<MeterialMapper, Meterial> implements IMeterialService {

    @Resource
    private MeterialMapper meterialMapper;

    /**
     * 通过物料编码获取物料信息
     *
     * @param meterialCode
     * @return
     */
    @Override
    public Meterial getMeterialByMeterialCode(String meterialCode) {
        Meterial meterial = meterialMapper.getMeterialByMeterialCode(meterialCode);
        return meterial;
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
    public List<Meterial> listFuzzyQuery(String typeCode) {
        //新建QueryWrapper对象
        QueryWrapper<Meterial> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");

        query.likeRight("type_code",typeCode);

        return baseMapper.selectList(query);
    }


    /**
     * 条件组合模糊分页查询
     * @param meterialPage
     * @param materialVO
     * @return
     */
    @Override
    public Page pageFuzzyQuery(Page meterialPage, MeterialVO materialVO) {

        //新建QueryWrapper对象
        QueryWrapper<Meterial> query = new QueryWrapper<>();
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

        //物料俗称
        query.like(StringUtils.isNotBlank(materialVO.getSlang()), "slang", materialVO.getSlang());

        //规格型号
        query.like(StringUtils.isNotBlank(materialVO.getSpecificationModel()), "specification_model", materialVO.getSpecificationModel());

        //物料材质
        query.like(StringUtils.isNotBlank(materialVO.getMaterial()), "material", materialVO.getMaterial());

        //物料图号
        query.like(StringUtils.isNotBlank(materialVO.getDrawingNumber()), "drawing_number", materialVO.getMeasurementUnit());

        //计量单位
        query.like(StringUtils.isNotBlank(materialVO.getMeasurementUnit()), "measurement_unit", materialVO.getMeasurementUnit());

        //辅助单位
        query.like(StringUtils.isNotBlank(materialVO.getAuxiliaryUnit()), "auxiliary_unit", materialVO.getAuxiliaryUnit());

        //执行标准
        query.like(StringUtils.isNotBlank(materialVO.getExecutiveStandard()), "executive_standard", materialVO.getExecutiveStandard());

        //技术要求
        query.like(StringUtils.isNotBlank(materialVO.getSkillsRequiremen()), "skills_requiremen", materialVO.getSkillsRequiremen());

        //图号
        query.like(StringUtils.isNotBlank(materialVO.getDrawingNumber()), "drawing_number", materialVO.getDrawingNumber());

        //安全质量标准
        query.like(StringUtils.isNotBlank(materialVO.getSafetyQualityStandards()), "safety_quality_standards", materialVO.getSafetyQualityStandards());

        //品牌
        query.like(StringUtils.isNotBlank(materialVO.getBrand()), "brand", materialVO.getBrand());

        return baseMapper.selectPage(meterialPage, query);
    }
}
