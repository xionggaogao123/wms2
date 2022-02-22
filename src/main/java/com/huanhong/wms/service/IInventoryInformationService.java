package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.dto.AddInventoryInformationDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryInformationDTO;
import com.huanhong.wms.entity.vo.InventoryInformationVO;

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
     * 获取库存list
     * @param inventoryInformationVO
     * @return
     */
    InventoryInformation getInventoryInformation(InventoryInformationVO inventoryInformationVO);


    /**
     * 查询新增库存时，传入的货位、物料编码、批次是否已经存在
     * 若存在，则加上对应库存数量
     * 若不存在，则新增此条数据
     */
    InventoryInformation getInventoryById(int id);
}
