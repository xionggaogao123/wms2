package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OnShelf;
import com.huanhong.wms.entity.dto.AddOnShelfDTO;
import com.huanhong.wms.entity.dto.UpdateOnShelfDTO;
import com.huanhong.wms.entity.vo.OnShelfVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-24
 */
public interface IOnShelfService extends SuperService<OnShelf> {

    /**
     * 分页查询
     * @param OnShelfPage
     * @param onShelfVO
     * @return
     */
    Page<OnShelf> pageFuzzyQuery(Page<OnShelf> onShelfPage, OnShelfVO onShelfVO);


    /**
     * 新增上架单
     * @param add
     * @return
     */
    Result addOnShelf(AddOnShelfDTO addOnShelfDTO);

    /**
     * 更新上架单
     * @param updateOnShelfDTO
     * @return
     */
    Result updateOnshelf(UpdateOnShelfDTO updateOnShelfDTO);

    /**
     * 根据单据编号和仓库ID获取单据信息
     * @param docNum
     * @param warehouseId
     * @return
     */
    OnShelf getOnshelfByDocNumAndWarehouseId(String docNum,String warehouseId);


    /**
     * 根据id获取单据信息
     * @param id
     * @return
     */
    OnShelf getOnshelfById(Integer id);


    List<OnShelf> getOnshelfByMaterialCodingAndWarehouseId(String materialCoding,String warehouseId);


}
