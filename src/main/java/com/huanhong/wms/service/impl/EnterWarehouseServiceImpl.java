package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDTO;
import com.huanhong.wms.entity.vo.EnterWarehouseVO;
import com.huanhong.wms.mapper.EnterWarehouseMapper;
import com.huanhong.wms.service.IEnterWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 采购入库单主表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-24
 */
@Slf4j
@Service
public class EnterWarehouseServiceImpl extends SuperServiceImpl<EnterWarehouseMapper, EnterWarehouse> implements IEnterWarehouseService {


    @Resource
    private EnterWarehouseMapper enterWarehouseMapper;

    /**
     * 分页查询
     * @param enterWarehousePage
     * @param enterWarehouseVO
     * @return
     */
    @Override
    public Page<EnterWarehouse> pageFuzzyQuery(Page<EnterWarehouse> enterWarehousePage, EnterWarehouseVO enterWarehouseVO) {
        //新建QueryWrapper对象
        QueryWrapper<EnterWarehouse> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(enterWarehouseVO)) {
            return enterWarehouseMapper.selectPage(enterWarehousePage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(enterWarehouseVO.getDocumentNumber()), "document_number", enterWarehouseVO.getDocumentNumber());

        query.like(ObjectUtil.isNotNull(enterWarehouseVO.getStorageType()), "storage_type", enterWarehouseVO.getStorageType());

        query.like(StringUtils.isNotBlank(enterWarehouseVO.getContractNumber()), "contract_number", enterWarehouseVO.getContractNumber());

        query.like(StringUtils.isNotBlank(enterWarehouseVO.getRfqNumber()), "rfq_number", enterWarehouseVO.getRfqNumber());

        query.like(ObjectUtil.isNotNull(enterWarehouseVO.getState()), "state", enterWarehouseVO.getState());

        query.like(StringUtils.isNotBlank(enterWarehouseVO.getVerificationDocumentNumber()), "verification_document_number", enterWarehouseVO.getVerificationDocumentNumber());

        query.like(ObjectUtil.isNotNull(enterWarehouseVO.getPlanClassification()), "plan_classification", enterWarehouseVO.getPlanClassification());

        query.like(StringUtils.isNotBlank(enterWarehouseVO.getReceiptNumber()), "receipt_number", enterWarehouseVO.getReceiptNumber());

        query.like(ObjectUtil.isNotNull(enterWarehouseVO.getManager()), "manager", enterWarehouseVO.getManager());

        query.like(ObjectUtil.isNotNull(enterWarehouseVO.getSupplierName()), "supplier_name", enterWarehouseVO.getSupplierName());

        query.like(ObjectUtil.isNotNull(enterWarehouseVO.getWarehouse()), "warehouse", enterWarehouseVO.getWarehouse());


        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 入库时间区间
         */
        if (ObjectUtil.isNotEmpty(enterWarehouseVO.getEnterDateStart())&&ObjectUtil.isNotEmpty(enterWarehouseVO.getEnterDateEnd())){
            String enterDateStart = dtf1.format(enterWarehouseVO.getEnterDateStart());
            String enterDateEnd = dtf1.format(enterWarehouseVO.getEnterDateEnd());
            /**
             * 入库时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + enterDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + enterDateEnd + "')");

        }

        /**
         * 到货时间区间
         */
        if (ObjectUtil.isNotEmpty(enterWarehouseVO.getDeliveryDateStart())&&ObjectUtil.isNotEmpty(enterWarehouseVO.getDeliveryDateEnd())){
            String deliveryDateStart = dtf1.format(enterWarehouseVO.getDeliveryDateStart());
            String deliveryDateEnd = dtf1.format(enterWarehouseVO.getDeliveryDateEnd());
            /**
             * 到货时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(delivery_date) >= UNIX_TIMESTAMP('" + deliveryDateStart + "')")
                    .apply("UNIX_TIMESTAMP(delivery_date) <= UNIX_TIMESTAMP('" + deliveryDateEnd + "')");

        }
        return baseMapper.selectPage(enterWarehousePage, query);
    }

    @Override
    public Result addEnterWarehouse(AddEnterWarehouseDTO addEnterWarehouseDTO) {

        try {
            /**
             * 生成采购入库单据编码（CGRK+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<EnterWarehouse> queryEnterWarehouse = new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryEnterWarehouse.eq("warehouse", addEnterWarehouseDTO.getWarehouse());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryEnterWarehouse.likeRight("document_number", "CGRU" + today);
            /**
             * likeRigh: CGRK+XXXXXXXX(当前年月日)
             */
            EnterWarehouse maxEnterWarehouse = enterWarehouseMapper.selectOne(queryEnterWarehouse.orderByDesc("id").last("limit 1"));
            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxEnterWarehouse)) {
                maxDocNum = maxEnterWarehouse.getDocumentNumber();
            }
            String orderNo = null;
            //单据编码前缀-CGRU+年月日
            String code_pfix = "CGRU" + today;
            if (maxDocNum != null && maxEnterWarehouse.getDocumentNumber().contains(code_pfix)) {
                String code_end = maxEnterWarehouse.getDocumentNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            EnterWarehouse enterWarehouse = new EnterWarehouse();
            BeanUtil.copyProperties(addEnterWarehouseDTO, enterWarehouse);
            enterWarehouse.setDocumentNumber(orderNo);
            int i = enterWarehouseMapper.insert(enterWarehouse);
            if (i > 0) {
                return Result.success(getEnterWarehouseByDocNumberAndWarhouse(orderNo, enterWarehouse.getWarehouse()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增采购入库单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }

    /**
     * 根据单据编号和仓库编号获取采购入库单
     * @param docNumber
     * @param warhouse
     * @return
     */
    @Override
    public EnterWarehouse getEnterWarehouseByDocNumberAndWarhouse(String docNumber,String warhouse) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number",docNumber);
        queryWrapper.eq("warehouse",warhouse);
        EnterWarehouse enterWarehouse = enterWarehouseMapper.selectOne(queryWrapper);
        return enterWarehouse;
    }

    /**
     * 根据ID获取入库单信息
     * @param id
     * @return
     */
    @Override
    public EnterWarehouse getEnterWarehouseById(Integer id) {
        return enterWarehouseMapper.selectById(id);
    }


    /**
     * 更新入库单
     * @param updateEnterWarehouseDTO
     * @return
     */
    @Override
    public Result updateEnterWarehouse(UpdateEnterWarehouseDTO updateEnterWarehouseDTO) {
        EnterWarehouse enterWarehouseOld = getEnterWarehouseById(updateEnterWarehouseDTO.getId());
        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */
        //采购合同编号
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getContractNumber())){
            enterWarehouseOld.setContractNumber(updateEnterWarehouseDTO.getContractNumber());
        }
        //流程ID
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getProcessInstanceId())){
            enterWarehouseOld.setProcessInstanceId(updateEnterWarehouseDTO.getProcessInstanceId());
        }
        //询价单编号
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getRfqNumber())){
            enterWarehouseOld.setRfqNumber(updateEnterWarehouseDTO.getRfqNumber());
        }
        //入库类型
        if (ObjectUtil.isNotNull(updateEnterWarehouseDTO.getStorageType())){
            enterWarehouseOld.setStorageType(updateEnterWarehouseDTO.getStorageType());
        }
        //状态
        if (ObjectUtil.isNotNull(updateEnterWarehouseDTO.getState())){
            enterWarehouseOld.setState(updateEnterWarehouseDTO.getState());
        }
        //到货检验单编号
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getVerificationDocumentNumber())){
            enterWarehouseOld.setVerificationDocumentNumber(updateEnterWarehouseDTO.getVerificationDocumentNumber());
        }
        //计划类别
        if (ObjectUtil.isNotNull(updateEnterWarehouseDTO.getPlanClassification())){
            enterWarehouseOld.setPlanClassification(updateEnterWarehouseDTO.getPlanClassification());
        }
        //发票号
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getReceiptNumber())){
            enterWarehouseOld.setReceiptNumber(updateEnterWarehouseDTO.getReceiptNumber());
        }
        //到货日期
        if (ObjectUtil.isNotEmpty(updateEnterWarehouseDTO.getDeliveryDate())){
            enterWarehouseOld.setDeliveryDate(updateEnterWarehouseDTO.getDeliveryDate());
        }
        //经办人
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getManager())){
            enterWarehouseOld.setManager(updateEnterWarehouseDTO.getManager());
        }
        //供应商名称
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getSupplierName())){
            enterWarehouseOld.setSupplierName(updateEnterWarehouseDTO.getSupplierName());
        }
        //备注
        if (StringUtils.isNotBlank(updateEnterWarehouseDTO.getRemark())){
            enterWarehouseOld.setRemark(updateEnterWarehouseDTO.getRemark());
        }
        int update = enterWarehouseMapper.updateById(enterWarehouseOld);
        return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
    }


    @Override
    public EnterWarehouse getEnterWarehouseByProcessInstanceId(String processInstanceId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_instance_id",processInstanceId);
        EnterWarehouse enterWarehouse = enterWarehouseMapper.selectOne(queryWrapper);
        return enterWarehouse;
    }
}
