package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.TemporaryLibrary;
import com.huanhong.wms.entity.vo.TemporaryLibraryVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-18
 */
public interface ITemporaryLibraryService extends SuperService<TemporaryLibrary> {

    //组合分页模糊查询
    Page<TemporaryLibrary> pageFuzzyQuery(Page<TemporaryLibrary> TemporaryLibraryPage, TemporaryLibraryVO temporaryLibraryVO);


    /**
     * 库存信息更新
     * 根据临时入库单单据号获取详细信息--一对多
     */
    int updateTemporaryLibrary(TemporaryLibrary temporaryLibrary);


}
