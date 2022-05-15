package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ArrivalVerification;
import com.huanhong.wms.entity.dto.AddArrivalVerificationDTO;
import com.huanhong.wms.entity.dto.UpdateArrivalVerificationDTO;
import com.huanhong.wms.entity.vo.ArrivalVerificationVO;
import com.huanhong.wms.mapper.ArrivalVerificationMapper;
import com.huanhong.wms.service.IArrivalVerificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 到货检验主表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-23
 */
@Service
public class ArrivalVerificationServiceImpl extends SuperServiceImpl<ArrivalVerificationMapper, ArrivalVerification> implements IArrivalVerificationService {

    @Resource
    private ArrivalVerificationMapper arrivalVerificationMapper;

    @Override
    public Page<ArrivalVerification> pageFuzzyQuery(Page<ArrivalVerification> arrivalVerificationPage, ArrivalVerificationVO arrivalVerificationVO) {

        //新建QueryWrapper对象
        QueryWrapper<ArrivalVerification> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(arrivalVerificationVO)) {
            return arrivalVerificationMapper.selectPage(arrivalVerificationPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getVerificationDocumentNumber()), "verification_document_number", arrivalVerificationVO.getVerificationDocumentNumber());

        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getContractNumber()), "contract_number", arrivalVerificationVO.getContractNumber());

        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getRfqNumber()), "rfq_number", arrivalVerificationVO.getRfqNumber());

        query.like(ObjectUtil.isNotNull(arrivalVerificationVO.getPlanClassification()), "plan_classification", arrivalVerificationVO.getPlanClassification());

        query.like(ObjectUtil.isNotNull(arrivalVerificationVO.getPlanStatus()), "plan_status", arrivalVerificationVO.getPlanStatus());

        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getInspector()), "inspector", arrivalVerificationVO.getInspector());

        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getSupplierName()), "supplier_name", arrivalVerificationVO.getSupplierName());

        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getCarNumber()), "car_number", arrivalVerificationVO.getCarNumber());

        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getWarehouseId()), "warehouse_id", arrivalVerificationVO.getWarehouseId());

        query.like(ObjectUtil.isNotNull(arrivalVerificationVO.getVerificationStatus()), "verification_status",arrivalVerificationVO.getVerificationStatus());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(arrivalVerificationVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(arrivalVerificationVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(arrivalVerificationVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(arrivalVerificationVO.getCreateTimeEnd());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        query.eq(ObjectUtil.isNotNull(arrivalVerificationVO.getIsImported()), "is_imported", arrivalVerificationVO.getIsImported());
        return baseMapper.selectPage(arrivalVerificationPage, query);
    }

    @Override
    public Page<ArrivalVerification> pageFuzzyQueryPDA(Page<ArrivalVerification> arrivalVerificationPage, ArrivalVerificationVO arrivalVerificationVO) {
        //新建QueryWrapper对象
        QueryWrapper<ArrivalVerification> query = new QueryWrapper<>();

        //根据id排序
        query.orderByAsc("id");

        //单据编号
        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getVerificationDocumentNumber()), "verification_document_number", arrivalVerificationVO.getVerificationDocumentNumber());

        //仓库
        query.like(StringUtils.isNotBlank(arrivalVerificationVO.getWarehouseId()), "warehouse_id", arrivalVerificationVO.getWarehouseId());

        //单据状态
        if (ObjectUtil.isNotNull(arrivalVerificationVO.getVerificationStatus()) && arrivalVerificationVO.getVerificationStatus() == 0) {
            query.eq("verification_status", 0).or().eq("verification_status", 1);
        } else if (ObjectUtil.isNotNull(arrivalVerificationVO.getVerificationStatus()) && arrivalVerificationVO.getVerificationStatus() == 1) {
            query.eq("verification_status", 2);
        }

        return baseMapper.selectPage(arrivalVerificationPage, query);
    }

    @Override
    public Result addArrivalVerification(AddArrivalVerificationDTO addArrivalVerificationDTO) {
        try {
            /**
             * 生成到货检验单单据编码（DHJY+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<ArrivalVerification> queryProcurementPlan = new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryProcurementPlan.eq("warehouse_id", addArrivalVerificationDTO.getWarehouseId());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryProcurementPlan.likeRight("verification_document_number", "DHJY" + today);
            /**
             * likeRigh: XQJH+XXXXXXXX(当前年月日)
             */
            ArrivalVerification maxArrivalVerification = arrivalVerificationMapper.selectOne(queryProcurementPlan.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxArrivalVerification)) {
                maxDocNum = maxArrivalVerification.getVerificationDocumentNumber();
            }
            String orderNo = null;
            //单据编码前缀-DHJY+年月日
            String code_pfix = "DHJY" + today;
            if (maxDocNum != null && maxArrivalVerification.getVerificationDocumentNumber().contains(code_pfix)) {
                String code_end = maxArrivalVerification.getVerificationDocumentNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            ArrivalVerification arrivalVerification = new ArrivalVerification();
            BeanUtil.copyProperties(addArrivalVerificationDTO, arrivalVerification);
            arrivalVerification.setVerificationDocumentNumber(orderNo);
            int i = arrivalVerificationMapper.insert(arrivalVerification);
            if (i > 0) {
                return Result.success(getArrivalVerificationByDocNumberAndWarhouseId(orderNo, arrivalVerification.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增到货检验单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    @Override
    public Result updateArrivalVerification(UpdateArrivalVerificationDTO updateArrivalVerificationDTO) {
        ArrivalVerification arrivalVerificationOld = getArrivalVerificationById(updateArrivalVerificationDTO.getId());
        BeanUtil.copyProperties(updateArrivalVerificationDTO,arrivalVerificationOld);
        int update = arrivalVerificationMapper.updateById(arrivalVerificationOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public ArrivalVerification getArrivalVerificationByDocNumberAndWarhouseId(String docNumber, String warehouseId) {
        QueryWrapper<ArrivalVerification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("verification_document_number",docNumber);
        queryWrapper.eq("warehouse_id",warehouseId);
        return arrivalVerificationMapper.selectOne(queryWrapper);
    }

    @Override
    public ArrivalVerification getArrivalVerificationById(Integer id) {
        return arrivalVerificationMapper.selectById(id);
    }

    @Override
    public ArrivalVerification getArrivalVerificationByProcessInstanceId(String processInstanceId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_instance_id",processInstanceId);
        ArrivalVerification arrivalVerification = arrivalVerificationMapper.selectOne(queryWrapper);
        return arrivalVerification;
    }

    @Override
    public Result<Integer> updateIsImportedByDocumentNumbers(Integer isImported, String documentNumberImported, String[] verificationDocumentNumber) {
        ArrivalVerification arrivalVerification = new ArrivalVerification();
        arrivalVerification.setIsImported(isImported);
        arrivalVerification.setDocumentNumberImported(documentNumberImported);
        int i = arrivalVerificationMapper.update(arrivalVerification, Wrappers.<ArrivalVerification>lambdaUpdate()
                .in(ArrivalVerification::getVerificationDocumentNumber,verificationDocumentNumber));
        return Result.success(i);
    }
}
