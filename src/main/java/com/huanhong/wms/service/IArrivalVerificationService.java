package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ArrivalVerification;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.dto.AddArrivalVerificationDTO;
import com.huanhong.wms.entity.dto.UpdateArrivalVerificationDTO;
import com.huanhong.wms.entity.vo.ArrivalVerificationVO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;

/**
 * <p>
 * 到货检验主表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-23
 */
public interface IArrivalVerificationService extends SuperService<ArrivalVerification> {

    /**
     * 分页查询
     */
    Page<ArrivalVerification> pageFuzzyQuery(Page<ArrivalVerification> arrivalVerificationPage, ArrivalVerificationVO arrivalVerificationVO);


    /**
     *  PDA端分页查询
     * @param arrivalVerificationPage
     * @param arrivalVerificationVO
     * @return
     */
    Page<ArrivalVerification> pageFuzzyQueryPDA(Page<ArrivalVerification> arrivalVerificationPage, ArrivalVerificationVO arrivalVerificationVO);



    /**
     * 到货检验单新增
     * @param addArrivalVerificationDTO
     * @return
     */
    Result addArrivalVerification(AddArrivalVerificationDTO addArrivalVerificationDTO);


    /**
     * 到货检验单更新
     * @param updateArrivalVerificationDTO
     * @return
     */
    Result updateArrivalVerification(UpdateArrivalVerificationDTO updateArrivalVerificationDTO);


    /**
     * 根据单据编号和仓库ID获取到货检验单
     * @param docNumber
     * @param warhouseId
     * @return
     */
    ArrivalVerification getArrivalVerificationByDocNumberAndWarhouseId(String docNumber, String warehouseId);


    /**
     * 根据ID获取到货检验单
     * @param id
     * @return
     */
    ArrivalVerification getArrivalVerificationById(Integer id);


    /**
     * 根据流程Id获取到货检验信息
     *
     * @param processInstanceId
     * @return
     */
    ArrivalVerification getArrivalVerificationByProcessInstanceId(String processInstanceId);

}
