package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.dto.AddSubliraryDTO;
import com.huanhong.wms.entity.vo.SublibraryVO;

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


    //根据仓库ID获取所属子库信息
    List<SublibraryManagement> getSublibraryManagementByWarehouseId(String warehouseId);

    //根据子库编号获取获取子库信息
    SublibraryManagement getSublibraryBySublibraryId(String sublibraryId);

    //组合分页模糊查询
    Page<SublibraryManagement> pageFuzzyQuery(Page<SublibraryManagement> SublibraryManagementPage, SublibraryVO sublibraryVO);

    //查询某子库是否停用
    int isStopUsing(String sublibraryId);

    //新增子库
    Result addSublibraryManagement(AddSubliraryDTO addSubliraryDTO);

    /**
     *
     * @param parentCode
     * @param enable true = 随父级启用  false = 随父级停用
     * @return
     */
    int stopUsingByParentCode(String parentCode,boolean enable);
}
