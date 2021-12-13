package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.entity.vo.MeterialVO;
import com.huanhong.wms.mapper.MeterialMapper;
import com.huanhong.wms.service.IMeterialService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    /**
     * 通过物料名称获取物料
     *
     * @param meterialName
     * @return
     */
    @Override
    public Meterial getMeterialByMeterialName(String meterialName) {
        Meterial meterial = meterialMapper.getMeterialByMeterialCode(meterialName);
        return meterial;
    }

    /**
     * 条件组合模糊分页查询
     *
     * @param meterialPage
     * @param meterialVO
     * @return
     */
    @Override
    public Page pageFuzzyQuery(Page meterialPage, MeterialVO meterialVO) {

        //新建QueryWrapper对象
        QueryWrapper<Meterial> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(meterialVO)) {
            return baseMapper.selectPage(meterialPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        //物料编码
        query.like(StringUtils.isNotBlank(meterialVO.getMaterialCoding()), "material_coding", meterialVO.getMaterialCoding());
        //物料名称

        query.like(StringUtils.isNotBlank(meterialVO.getMaterialName()), "material_name", meterialVO.getMaterialName());

        //物料俗称
        query.like(StringUtils.isNotBlank(meterialVO.getSlang()), "slang", meterialVO.getSlang());

        //规格型号
        query.like(StringUtils.isNotBlank(meterialVO.getSpecificationModel()), "specification_model", meterialVO.getSpecificationModel());

        //物料材质
        query.like(StringUtils.isNotBlank(meterialVO.getMaterial()), "material", meterialVO.getMaterial());

        //物料图号
        query.like(StringUtils.isNotBlank(meterialVO.getDrawingNumber()), "drawing_number", meterialVO.getMeasurementUnit());

        //计量单位
        query.like(StringUtils.isNotBlank(meterialVO.getMeasurementUnit()), "measurement_unit", meterialVO.getMeasurementUnit());

        //辅助单位
        query.like(StringUtils.isNotBlank(meterialVO.getAuxiliaryUnit()), "auxiliary_unit", meterialVO.getAuxiliaryUnit());

        //执行标准
        query.like(StringUtils.isNotBlank(meterialVO.getExecutiveStandard()), "executive_standard", meterialVO.getExecutiveStandard());

        //技术要求
        query.like(StringUtils.isNotBlank(meterialVO.getSkillsRequiremen()), "skills_requiremen", meterialVO.getSkillsRequiremen());

        //图号
        query.like(StringUtils.isNotBlank(meterialVO.getDrawingNumber()), "drawing_number", meterialVO.getDrawingNumber());

        //安全质量标准
        query.like(StringUtils.isNotBlank(meterialVO.getSafetyQualityStandards()), "safety_quality_standards", meterialVO.getSafetyQualityStandards());
        return baseMapper.selectPage(meterialPage, query);
    }
}
