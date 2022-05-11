package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.TemporaryLibrary;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryDTO;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import com.huanhong.wms.entity.vo.TemporaryLibraryVO;

import java.util.List;

/**
 * <p>
 * 临库库存表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
public interface ITemporaryLibraryService extends SuperService<TemporaryLibrary> {


    /**
     * 分页查询
     */
    Page<TemporaryLibrary> pageFuzzyQuery(Page<TemporaryLibrary> temporaryLibraryPage, TemporaryLibraryVO temporaryLibraryVO);

    /**
     * 库存信息更新
     * @param updateTemporaryLibraryDTO
     * @return
     */
    Result updateTemporaryLibrary(UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO);


    /**
     * 临库新增库存
     * @param addTemporaryLibraryDTO
     * @return
     */
    Result addTemporaryLibrary(AddTemporaryLibraryDTO addTemporaryLibraryDTO);


    /**
     * 根据ID获取库存信息
     * @param id
     * @return
     */
    TemporaryLibrary getTemporaryLibraryById(int id);


    /**
     * 根据物料编码、批次、货位编号
     * @param materialCoding
     * @param batch
     * @param cargoSpaceId
     * @return
     */
    TemporaryLibrary getTemporaryLibrary(String materialCoding, String batch, String cargoSpaceId);



    /**
     * 根据物料编码和仓库Id获取物料库存List
     * @param materialCoding
     * @param warehouseId
     * @return
     */
    List<TemporaryLibrary> getTemporaryLibraryListByMaterialCodingAndWarehouseId(String materialCoding,String warehouseId);


    /**
     * 获取对应仓库的所有库存
     * @param warehouseId
     * @return
     */
    List<TemporaryLibrary> getTemporaryLibraryListByWarehouseId(String warehouseId);


    /**
     * 根据物料编码和仓库Id获取物料库存数量
     * @return
     */
    Double getNumByMaterialCodingAndWarehouseId(String materialCoding,String warehouseId);

    /**
     * 根据物料编码和批次和仓库Id获取物料库存数量
     * @return
     */
    Double getNumByMaterialCodingAndBatchAndWarehouseId(String materialCoding,String batch,String warehouseId);

    /**
     * 根据物料编码和仓库Id获取物料库存List
     * @param materialCoding
     * @param warehouseId
     * @return
     */
    List<TemporaryLibrary> getInventoryInformationListByMaterialCodingAndWarehouseId(String materialCoding,String warehouseId);



}
