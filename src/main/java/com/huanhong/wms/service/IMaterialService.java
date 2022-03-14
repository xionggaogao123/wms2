package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.vo.MaterialVO;

import java.util.List;

/**
 * <p>
 * 材料 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-22
 */
public interface IMaterialService extends SuperService<Material> {

    Material getMeterialByMeterialCode(String materialCode);

//    Meterial getMeterialByMeterialName(String meterialName);

    Page<Material> pageFuzzyQuery(Page<Material> materialPage, MaterialVO materialVO);

    List<Material> listFuzzyQuery(String typeCode);


    /**
     * 查询某物料是否停用  0-使用中  1-停用
     * @param materialCode
     * @return
     */
   int isStopUsing(String materialCode);

    /**
     * 根据物料编码或物料名称模糊查询物料
     * @return
     */
   List<Material> getMaterialListByKey(String key);

}
