package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.MaterialClassification;
import com.huanhong.wms.entity.vo.MeterialClassficationVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物料分类 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-16
 */
public interface IMaterialClassificationService extends SuperService<MaterialClassification> {

    //组合分页模糊查询
    Page<MaterialClassification> pageFuzzyQuery(Page<MaterialClassification> materialClassificationPage, MeterialClassficationVO materialClassificationVO);

    //根据类型编码获取详细信息
    MaterialClassification getMaterialClassificationByTypeCode(String typeCode);

    //根据父类编码获取子分类list
    List<MaterialClassification> getMaterialClassificationListByParentCode(String parentCode);

    //根据分类编码删除
    Integer deleteMaterialClassification(MaterialClassification materialClassification);

    //获取树状图List
    List<Map<String, Object>> getTreeNode();

}
