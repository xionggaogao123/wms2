package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.SublibraryVO;
import com.huanhong.wms.entity.vo.WarehouseVo;

import java.util.List;

/**
 * <p>
 * 子库管理 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface ISublibraryManagementService extends SuperService<SublibraryManagement> {


    //根据库房ID获取所属子库信息
    List<SublibraryManagement> getSublibraryManagementByWarehouseId(String warehouseId);

    //根据子库编号获取获取子库信息
    SublibraryManagement getSublibraryBySublibraryId(String sublibraryId);

    //组合分页模糊查询
    Page<SublibraryManagement> pageFuzzyQuery(Page<SublibraryManagement> SublibraryManagementPage, SublibraryVO sublibraryVO);

}
