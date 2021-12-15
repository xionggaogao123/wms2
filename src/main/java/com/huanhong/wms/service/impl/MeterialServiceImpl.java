package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.entity.dto.AddMeterialDTO;
import com.huanhong.wms.mapper.MeterialMapper;
import com.huanhong.wms.service.IMeterialService;
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
     * @param meterialPage
     * @param addMeterialDTO
     * @return
     */
    @Override
    public Page pageFuzzyQuery(Page meterialPage, AddMeterialDTO addMeterialDTO) {

        //新建QueryWrapper对象
        QueryWrapper<Meterial> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(addMeterialDTO)) {
            return baseMapper.selectPage(meterialPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        //物料编码
        query.like(StringUtils.isNotBlank(addMeterialDTO.getMaterialCoding()), "material_coding", addMeterialDTO.getMaterialCoding());
        //物料名称

        query.like(StringUtils.isNotBlank(addMeterialDTO.getMaterialName()), "material_name", addMeterialDTO.getMaterialName());

        //物料俗称
        query.like(StringUtils.isNotBlank(addMeterialDTO.getSlang()), "slang", addMeterialDTO.getSlang());

        //规格型号
        query.like(StringUtils.isNotBlank(addMeterialDTO.getSpecificationModel()), "specification_model",addMeterialDTO.getSpecificationModel());

        //物料材质
        query.like(StringUtils.isNotBlank(addMeterialDTO.getMaterial()), "material", addMeterialDTO.getMaterial());

        //物料图号
        query.like(StringUtils.isNotBlank(addMeterialDTO.getDrawingNumber()), "drawing_number", addMeterialDTO.getMeasurementUnit());

        //计量单位
        query.like(StringUtils.isNotBlank(addMeterialDTO.getMeasurementUnit()), "measurement_unit", addMeterialDTO.getMeasurementUnit());

        //辅助单位
        query.like(StringUtils.isNotBlank(addMeterialDTO.getAuxiliaryUnit()), "auxiliary_unit", addMeterialDTO.getAuxiliaryUnit());

        //执行标准
        query.like(StringUtils.isNotBlank(addMeterialDTO.getExecutiveStandard()), "executive_standard", addMeterialDTO.getExecutiveStandard());

        //技术要求
        query.like(StringUtils.isNotBlank(addMeterialDTO.getSkillsRequiremen()), "skills_requiremen", addMeterialDTO.getSkillsRequiremen());

        //图号
        query.like(StringUtils.isNotBlank(addMeterialDTO.getDrawingNumber()), "drawing_number", addMeterialDTO.getDrawingNumber());

        //安全质量标准
        query.like(StringUtils.isNotBlank(addMeterialDTO.getSafetyQualityStandards()), "safety_quality_standards", addMeterialDTO.getSafetyQualityStandards());

        //品牌
        query.like(StringUtils.isNotBlank(addMeterialDTO.getBrand()),"brand",addMeterialDTO.getBrand());

        return baseMapper.selectPage(meterialPage, query);
    }
}
