package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.param.MaterialProfitParam;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.vo.InventoryInfoVo;
import com.huanhong.wms.entity.vo.InventoryInformationVO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-25
 */
public interface IInventoryInformationService extends SuperService<InventoryInformation> {

    /**
     * 分页查询
     */
    Page<InventoryInformation> pageFuzzyQuery(Page<InventoryInformation> inventoryInformationPage, InventoryInformationVO inventoryInformationVO);

    /**
     * 库存信息更新
     */
    Result  updateInventoryInformation(UpdateInventoryInformationDTO updateInventoryInformationDTO);

    /**
     *
     * @param addInventoryInformationDTO
     * @return
     */
    Result addInventoryInformation(AddInventoryInformationDTO addInventoryInformationDTO);


    /**
     * 根据货位编码查询其中存储的物料信息
     */
    List<InventoryInformation>  getInventoryInformationByCargoSpaceId(String cargoSpaceId);


    /**
     * 查询新增库存时，传入的货位、物料编码、批次是否已经存在
     * 若存在，则加上对应库存数量
     * 若不存在，则新增此条数据
     */
    InventoryInformation getInventoryById(int id);


    InventoryInformation getInventoryInformation(String materialCoding, String batch, String cargoSpaceId);

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
    List<InventoryInformation> getInventoryInformationListByMaterialCodingAndWarehouseId(String materialCoding,String warehouseId);


    /**
     * 根据物料编码获取半年间（内外部）价格
     * @param materialCoding
     * @return
     */
    HashMap getMaterialPrice(String materialCoding);


    /**
     * 根据物料编码和批次和仓库Id获取物料库存List
     * @param materialCoding
     * @param batch
     * @param warehouseId
     * @return
     */
    List<InventoryInformation> getInventoryInformationListByMaterialCodingAndBatchAndWarehouseId(String materialCoding,String batch,String warehouseId);

    Result<Page<InventoryInfoVo>> inventoryBill(InventoryInfoPage page);

    void inventoryBillExport(InventoryInfoPage page, HttpServletRequest request, HttpServletResponse response);

    Result<Page<InventoryInfoVo>> deadGoods(InventoryInfoPage page);

    void deadGoodsExport(InventoryInfoPage page, HttpServletRequest request, HttpServletResponse response);

    Result<Page<InventoryInfoVo>> deadGoodsSettle(InventoryInfoPage page);

    void deadGoodsSettleExport(InventoryInfoPage page, HttpServletRequest request, HttpServletResponse response);

    Result<Object> getMaterialProfit(MaterialProfitParam param);

    Result<Object> getBelowSafetyStockMaterialWarning(String warehouseId);

    Result<Object> getPreExpirationWarning(String warehouseId, Integer days);
}
