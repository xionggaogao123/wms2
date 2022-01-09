package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.ShelfTemplate;
import com.huanhong.wms.entity.vo.ShelfTemplateVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-06
 */
public interface IShelfTemplateService extends SuperService<ShelfTemplate> {

    ShelfTemplate getShelfTemplateByID(Integer id);

    Page<ShelfTemplate> pageFuzzyQuery(Page<ShelfTemplate> shelfTemplatePage, ShelfTemplateVO shelfTemplateVO);

    List<ShelfTemplate> ListQueryByWarehouseId(String warehouseId);

}
