package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.RequiremetsPlanningDetails;
import com.huanhong.wms.entity.dto.AddRequiremetsPlanningDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateRequiremetsPlanningDetailsDTO;

import java.util.List;

/**
 * <p>
 * 需求计划明细表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-16
 */
public interface IRequiremetsPlanningDetailsService extends SuperService<RequiremetsPlanningDetails> {

//    /**
//     * 分页查询
//     * @param requiremetsPlanningDetailsPage
//     * @param requiremetsPlanningDetailsVO
//     * @return
//     */
//    Page<RequiremetsPlanningDetails> pageFuzzyQuery(Page<RequiremetsPlanningDetails> requiremetsPlanningDetailsPage, RequiremetsPlanningDetailsVO requiremetsPlanningDetailsVO);
//

    /**
     * 新增需求计划表
     * @param addRequiremetsPlanningDetailsDTOList
     * @return
     */
    Result addRequiremetsPlanningDetails(List<AddRequiremetsPlanningDetailsDTO> addRequiremetsPlanningDetailsDTOList);


    /**
     * 更新需求计划表
     * @param updateRequiremetsPlanningDetailsDTOList
     * @return
     */
    Result updateRequiremetsPlanningDetails(List<UpdateRequiremetsPlanningDetailsDTO> updateRequiremetsPlanningDetailsDTOList);

    /**
     * 通过Id获取需求计划明细表
     * @param id
     * @return
     */
    RequiremetsPlanningDetails getRequiremetsPlanningDetailsById(Integer id);

    /**
     * 通过需求计划单据编号和仓库编号获取明细list
     * @param docNum
     * @param warehouseId
     * @return
     */
    List<RequiremetsPlanningDetails> getRequiremetsPlanningDetailsByDocNumAndWarehouseId(String docNum, String warehouseId);

}
