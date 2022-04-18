package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.dto.AddShelfDTO;
import com.huanhong.wms.entity.vo.ShelfVO;

import java.util.List;

/**
 * <p>
 * 货架管理 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface IShelfManagementService extends SuperService<ShelfManagement> {

    //根据子库ID获取所属货架
    List<ShelfManagement> getShelfListByWarehouseAreaId(String warehouseAreaId);

    //根据货架ID获取货架信息
    ShelfManagement getShelfByShelfId(String shelfId);

    //组合分页模糊查询
    Page<ShelfManagement> pageFuzzyQuery(Page<ShelfManagement> shelfManagementPage, ShelfVO shelfVO);
    
    //查询某库区是否停用 0- 使用中  1- 停用
    int isStopUsing(String shelfId);

    /**
     *
     * @param parentCode
     * @param enable true = 随父级启用  false = 随父级停用
     * @return
     */
    int stopUsingByParentCode(String parentCode,boolean enable);

    Result addShelf(AddShelfDTO addShelfDTO);
}
