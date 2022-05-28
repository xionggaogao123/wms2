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
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDTO;
import com.huanhong.wms.entity.vo.InventoryDocumentVO;
import com.huanhong.wms.mapper.InventoryDocumentMapper;
import com.huanhong.wms.service.IInventoryDocumentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-21
 */
@Service
public class InventoryDocumentServiceImpl extends SuperServiceImpl<InventoryDocumentMapper, InventoryDocument> implements IInventoryDocumentService {


    @Resource
    private InventoryDocumentMapper inventoryDocumentMapper;

    /**
     * 分页查询
     *
     * @param inventoryDocumentPage
     * @param inventoryDocumentVO
     * @return
     */
    @Override
    public Page<InventoryDocument> pageFuzzyQuery(Page<InventoryDocument> inventoryDocumentPage, InventoryDocumentVO inventoryDocumentVO) {

        //新建QueryWrapper对象
        QueryWrapper<InventoryDocument> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(inventoryDocumentVO)) {
            return inventoryDocumentMapper.selectPage(inventoryDocumentPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(inventoryDocumentVO.getDocumentNumber()), "document_number", inventoryDocumentVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(inventoryDocumentVO.getDeliveryNoteNumber()), "delivery_note_number", inventoryDocumentVO.getDeliveryNoteNumber());

        query.like(StringUtils.isNotBlank(inventoryDocumentVO.getRfqNumber()), "rfq_number", inventoryDocumentVO.getRfqNumber());

        query.like(StringUtils.isNotBlank(inventoryDocumentVO.getMaterialCoding()), "material_coding", inventoryDocumentVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(inventoryDocumentVO.getWarehouse()), "warehouse", inventoryDocumentVO.getWarehouse());

        query.eq(ObjectUtil.isNotNull(inventoryDocumentVO.getComplete()),"complete",inventoryDocumentVO.getComplete());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 清点单时间区间查询
         */
        if (ObjectUtil.isNotEmpty(inventoryDocumentVO.getCreateDateStart()) && ObjectUtil.isNotEmpty(inventoryDocumentVO.getCreateDateEnd())) {

            String createDateStart = dtf1.format(inventoryDocumentVO.getCreateDateStart());

            String createDateEnd = dtf1.format(inventoryDocumentVO.getCreateDateEnd());

            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        query.eq(ObjectUtil.isNotNull(inventoryDocumentVO.getIsImported()), "is_imported", inventoryDocumentVO.getIsImported());
        return inventoryDocumentMapper.selectPage(inventoryDocumentPage, query);
    }

    /**
     * 新增清点单
     *
     * @param addInventoryDocumentDTO
     * @return
     */
    @Override
    public Result addInventoryDocument(AddInventoryDocumentDTO addInventoryDocumentDTO) {
        try {
            /**
             * 生成清点单据编码（QD+年月日八位数字+五位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
//            QueryWrapper<InventoryDocument> queryInventoryDocument = new QueryWrapper<>();
            /**
             * 当前仓库
             */
//            queryInventoryDocument.eq("warehouse", addInventoryDocumentDTO.getWarehouse());
//            /**
//             * 当前日期
//             */
//            String today = StrUtils.HandleData(DateUtil.today());
//            queryInventoryDocument.likeRight("document_number", "QD" + today);
//            /**
//             * likeRigh: QD+XXXXXXXX(当前年月日)
//             */
//            InventoryDocument maxInventoryDocument = inventoryDocumentMapper.selectOne(queryInventoryDocument.orderByDesc("id").last("limit 1"));
//            //目前最大的单据编码
//            String maxDocNum = null;
//            if (ObjectUtil.isNotEmpty(maxInventoryDocument)) {
//                maxDocNum = maxInventoryDocument.getDocumentNumber();
//            }
//            String orderNo = null;
//            //单据编码前缀-QD+年月日
//            String code_pfix = "QD" + today;
//            if (maxDocNum != null && maxInventoryDocument.getDocumentNumber().contains(code_pfix)) {
//                String code_end = maxInventoryDocument.getDocumentNumber().substring(10, 15);
//                int endNum = Integer.parseInt(code_end);
//                int tmpNum = 100000 + endNum + 1;
//                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
//            } else {
//                orderNo = code_pfix + "00001";
//            }

            /**
             * 新增单据
             */
            InventoryDocument inventoryDocument = new InventoryDocument();
            BeanUtil.copyProperties(addInventoryDocumentDTO, inventoryDocument);
            inventoryDocument.setDocumentNumber("QD"+ String.valueOf(System.currentTimeMillis()));
            int i = inventoryDocumentMapper.insert(inventoryDocument);
            if (i > 0) {
                return Result.success(getInventoryDocumentByDocumentNumberAndWarehouseId(inventoryDocument.getDocumentNumber(), addInventoryDocumentDTO.getWarehouse()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增清点单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    /**
     * 更新清点单
     *
     * @param updateInventoryDocumentDTO
     * @return
     */
    @Override
    public Result updateInventoryDocument(UpdateInventoryDocumentDTO updateInventoryDocumentDTO) {

        InventoryDocument inventoryDocumentOld = getInventoryDocumentById(updateInventoryDocumentDTO.getId());

        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */

        //送货单编号
        if (StringUtils.isNotBlank(updateInventoryDocumentDTO.getDeliveryNoteNumber())) {
            inventoryDocumentOld.setDeliveryNoteNumber(updateInventoryDocumentDTO.getDeliveryNoteNumber());
        }

        //询价单编号
        if (StringUtils.isNotBlank(updateInventoryDocumentDTO.getRfqNumber())) {
            inventoryDocumentOld.setRfqNumber(updateInventoryDocumentDTO.getRfqNumber());
        }

        //清点 0-未清点 1-已请你单
        if(ObjectUtil.isNotNull(updateInventoryDocumentDTO.getComplete())){
            inventoryDocumentOld.setComplete(updateInventoryDocumentDTO.getComplete());
        }

        //备注
        if (StringUtils.isNotBlank(updateInventoryDocumentDTO.getRemark())) {
            inventoryDocumentOld.setRemark(updateInventoryDocumentDTO.getRemark());
        }

        int update = inventoryDocumentMapper.updateById(inventoryDocumentOld);
        return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
    }

    /**
     * 根据id获取清点单
     *
     * @param id
     * @return
     */
    @Override
    public InventoryDocument getInventoryDocumentById(Integer id) {
        return inventoryDocumentMapper.selectById(id);
    }

    /**
     * 根据清点单编号和仓库id
     *
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    @Override
    public InventoryDocument getInventoryDocumentByDocumentNumberAndWarehouseId(String documentNumber, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number", documentNumber);
        queryWrapper.eq("warehouse", warehouseId);
        InventoryDocument inventoryDocument = inventoryDocumentMapper.selectOne(queryWrapper);
        return inventoryDocument;
    }

    @Override
    public Result<Integer> updateIsImportedByDocumentNumbers(Integer isImported, String documentNumberImported, String[] documentNumbers) {
        InventoryDocument inventoryDocument = new InventoryDocument();
        inventoryDocument.setIsImported(isImported);
        inventoryDocument.setDocumentNumberImported(documentNumberImported);
        Integer i = inventoryDocumentMapper.update(inventoryDocument, Wrappers.<InventoryDocument>lambdaUpdate().in(InventoryDocument::getDocumentNumber,documentNumbers));
        return Result.success(i);
    }

}
