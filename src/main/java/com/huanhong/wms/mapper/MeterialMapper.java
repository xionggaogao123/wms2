package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.entity.dto.UpdateMeterialDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 材料 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-22
 */
public interface MeterialMapper extends BaseMapper<Meterial> {


    /**
     * 通过物料编码搜索物料信息
     *
     * @param meterialCode
     * @return
     */
    @Select("select * from `meterial` where del = 0 and material_coding = #{meterialCode}")
    Meterial getMeterialByMeterialCode(String meterialCode);


    /**
     * 通过物料名称搜索物料信息
     *
     * @param meterialName
     * @return
     */
    @Select("select * from `meterial` where del = 0 and material_name = #{meterialName}")
    Meterial getMeterialByMeterialName(String meterialName);

    /**
     * 模糊查询
     *
     * @param field
     * @return
     */
    @Select("select ${field} from `meterial` where del = 0 and LOCATE(#{value},${field})>0")
    List<String> fuzzyQuerySelectList(String field, String value);


    int update(UpdateMeterialDTO updateMeterialVO, UpdateWrapper<UpdateMeterialDTO> updateWrapper);
}
