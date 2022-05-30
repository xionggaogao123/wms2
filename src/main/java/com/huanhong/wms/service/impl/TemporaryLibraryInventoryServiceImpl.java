package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryInventoryDTO;
import com.huanhong.wms.entity.vo.TemporaryLibraryInventoryVO;
import com.huanhong.wms.mapper.TemporaryLibraryInventoryMapper;
import com.huanhong.wms.service.ITemporaryLibraryInventoryService;
import com.huanhong.wms.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-06
 */
@Slf4j
@Service
public class TemporaryLibraryInventoryServiceImpl extends SuperServiceImpl<TemporaryLibraryInventoryMapper, TemporaryLibraryInventory> implements ITemporaryLibraryInventoryService {

    @Resource
    private TemporaryLibraryInventoryMapper temporaryLibraryInventoryMapper;

    @Override
    public Page<TemporaryLibraryInventory> pageFuzzyQuery(Page<TemporaryLibraryInventory> temporaryLibraryInventoryPage, TemporaryLibraryInventoryVO temporaryLibraryInventoryVO) {

        //新建QueryWrapper对象
        QueryWrapper<TemporaryLibraryInventory> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(temporaryLibraryInventoryVO)) {
            return temporaryLibraryInventoryMapper.selectPage(temporaryLibraryInventoryPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(temporaryLibraryInventoryVO.getDocumentNumber()), "document_number", temporaryLibraryInventoryVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(temporaryLibraryInventoryVO.getDeliveryNoteNumber()), "delivery_note_number", temporaryLibraryInventoryVO.getDeliveryNoteNumber());

        query.like(StringUtils.isNotBlank(temporaryLibraryInventoryVO.getRfqNumber()), "rfq_number", temporaryLibraryInventoryVO.getRfqNumber());

        query.like(StringUtils.isNotBlank(temporaryLibraryInventoryVO.getWarehouseId()), "warehouse_id", temporaryLibraryInventoryVO.getWarehouseId());

        query.eq(ObjectUtil.isNotNull(temporaryLibraryInventoryVO.getComplete()),"complete",temporaryLibraryInventoryVO.getComplete());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 清点单时间区间查询
         */
        if (ObjectUtil.isNotEmpty(temporaryLibraryInventoryVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(temporaryLibraryInventoryVO.getCreateTimeEnd())) {

            String createDateStart = dtf1.format(temporaryLibraryInventoryVO.getCreateTimeStart());

            String createDateEnd = dtf1.format(temporaryLibraryInventoryVO.getCreateTimeEnd());

            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        return temporaryLibraryInventoryMapper.selectPage(temporaryLibraryInventoryPage, query);
    }

    @Override
    public Result addTemporaryLibraryInventory(AddTemporaryLibraryInventoryDTO addTemporaryLibraryInventoryDTO) {
        try {
            /**
             * 生成清点单据编码（LKQD+年月日八位数字+五位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<TemporaryLibraryInventory> queryTemporaryLibraryInventory= new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryTemporaryLibraryInventory.eq("warehouse_id", addTemporaryLibraryInventoryDTO.getWarehouseId());
            /**
             * 当前日期
             */
//            String today = StrUtils.HandleData(DateUtil.today());
//            queryTemporaryLibraryInventory.likeRight("document_number", "LKQD" + today);
//            /**
//             * likeRigh: LKQD+XXXXXXXX(当前年月日)
//             */
//            TemporaryLibraryInventory maxTemporaryLibraryInventory = temporaryLibraryInventoryMapper.selectOne(queryTemporaryLibraryInventory.orderByDesc("id").last("limit 1"));
//            //目前最大的单据编码
//            String maxDocNum = null;
//            if (ObjectUtil.isNotEmpty(maxTemporaryLibraryInventory)) {
//                maxDocNum = maxTemporaryLibraryInventory.getDocumentNumber();
//            }
//            String orderNo = null;
//            //单据编码前缀-QD+年月日
//            String code_pfix = "LKQD" + today;
//            if (maxDocNum != null && maxTemporaryLibraryInventory.getDocumentNumber().contains(code_pfix)) {
//                String code_end = maxTemporaryLibraryInventory.getDocumentNumber().substring(12, 16);
//                int endNum = Integer.parseInt(code_end);
//                int tmpNum = 10000 + endNum + 1;
//                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
//            } else {
//                orderNo = code_pfix + "0001";
//            }

            /**
             * 新增单据
             */
            TemporaryLibraryInventory temporaryLibraryInventory = new TemporaryLibraryInventory();
            BeanUtil.copyProperties(addTemporaryLibraryInventoryDTO, temporaryLibraryInventory);
            temporaryLibraryInventory.setDocumentNumber("LKQD"+String.valueOf(System.currentTimeMillis()));
            log.info("添加临时清点的数据为:{}", JsonUtil.obj2String(temporaryLibraryInventory));
            temporaryLibraryInventory.setComplete(0);
            int i = temporaryLibraryInventoryMapper.insert(temporaryLibraryInventory);
            if (i > 0) {
                return Result.success(getTemporaryLibraryInventoryByDocumentNumberAndWarehouseId(temporaryLibraryInventory.getDocumentNumber(), addTemporaryLibraryInventoryDTO.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增临库清点单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    @Override
    public Result updateTemporaryLibraryInventory(UpdateTemporaryLibraryInventoryDTO updateTemporaryLibraryInventoryDTO) {
        TemporaryLibraryInventory temporaryLibraryInventoryOld = getTemporaryLibraryInventoryById(updateTemporaryLibraryInventoryDTO.getId());
        BeanUtil.copyProperties(updateTemporaryLibraryInventoryDTO,temporaryLibraryInventoryOld);
        int update = temporaryLibraryInventoryMapper.updateById(temporaryLibraryInventoryOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public TemporaryLibraryInventory getTemporaryLibraryInventoryById(Integer id) {
        return temporaryLibraryInventoryMapper.selectById(id);
    }

    @Override
    public TemporaryLibraryInventory getTemporaryLibraryInventoryByDocumentNumberAndWarehouseId(String documentNumber, String warehouseId) {
        QueryWrapper<TemporaryLibraryInventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", documentNumber);
        queryWrapper.eq("warehouse_id", warehouseId);
        return temporaryLibraryInventoryMapper.selectOne(queryWrapper);
    }

    @Override
    public TemporaryLibraryInventory addTemporaryLibraryInventory(TemporaryLibraryInventory temporaryLibraryInventory) {
        return null;
    }

}
