package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationEnter;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.AddTemporaryEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryEnterWarehouseDTO;
import com.huanhong.wms.entity.vo.TemporaryEnterWarehouseVO;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseMapper;
import com.huanhong.wms.service.ITemporaryEnterWarehouseService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
@Service
public class TemporaryEnterWarehouseServiceImpl extends SuperServiceImpl<TemporaryEnterWarehouseMapper, TemporaryEnterWarehouse> implements ITemporaryEnterWarehouseService {


    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Override
    public Page<TemporaryEnterWarehouse> pageFuzzyQuery(Page<TemporaryEnterWarehouse> temporaryEnterWarehousePage, TemporaryEnterWarehouseVO temporaryEnterWarehouseVO) {
        //新建QueryWrapper对象
        QueryWrapper<TemporaryEnterWarehouse> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(temporaryEnterWarehouseVO)) {
            return temporaryEnterWarehouseMapper.selectPage(temporaryEnterWarehousePage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(temporaryEnterWarehouseVO.getDocumentNumber()), "document_number", temporaryEnterWarehouseVO.getDocumentNumber());
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 入库时间区间
         */
        if (ObjectUtil.isNotEmpty(temporaryEnterWarehouseVO.getCreateTimeStart())&&ObjectUtil.isNotEmpty(temporaryEnterWarehouseVO.getCreateTimeEnd())){
            String createDateStart = dtf1.format(temporaryEnterWarehouseVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(temporaryEnterWarehouseVO.getCreateTimeEnd());
            /**
             * 入库时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        return temporaryEnterWarehouseMapper.selectPage(temporaryEnterWarehousePage, query);
    }



    @Override
    public Result addEnterWarehouse(AddTemporaryEnterWarehouseDTO addTemporaryEnterWarehouseDTO) {

        try {
            /**
             * 生成临库入库单据编码（LKRK+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<TemporaryEnterWarehouse> queryTemporaryEnterWarehouse= new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryTemporaryEnterWarehouse.eq("warehouse_id", addTemporaryEnterWarehouseDTO.getWarehouseId());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryTemporaryEnterWarehouse.likeRight("document_number", "LKRK" + today);
            /**
             * likeRigh: LKRK+XXXXXXXX(当前年月日)
             */
            TemporaryEnterWarehouse maxTemporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectOne(queryTemporaryEnterWarehouse.orderByDesc("id").last("limit 1"));
            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxTemporaryEnterWarehouse)) {
                maxDocNum = maxTemporaryEnterWarehouse.getDocumentNumber();
            }
            String orderNo = null;
            //单据编码前缀-LKRK+年月日
            String code_pfix = "LKRK" + today;
            if (maxDocNum != null && maxTemporaryEnterWarehouse.getDocumentNumber().contains(code_pfix)) {
                String code_end = maxTemporaryEnterWarehouse.getDocumentNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            TemporaryEnterWarehouse temporaryEnterWarehouse = new TemporaryEnterWarehouse();
            BeanUtil.copyProperties(addTemporaryEnterWarehouseDTO, temporaryEnterWarehouse);
            temporaryEnterWarehouse.setDocumentNumber(orderNo);
            int i = temporaryEnterWarehouseMapper.insert(temporaryEnterWarehouse);
            if (i > 0) {
                return Result.success(getTemporaryEnterWarehouseByDocNumberAndWarhouseId(orderNo, temporaryEnterWarehouse.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增采购入库单异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }
    }


    @Override
    public Result updateTemporaryEnterWarehouse(UpdateTemporaryEnterWarehouseDTO updateTemporaryEnterWarehouseDTO) {
        TemporaryEnterWarehouse temporaryEnterWarehouseOld = getTemporaryEnterWarehouseById(updateTemporaryEnterWarehouseDTO.getId());
        BeanUtil.copyProperties(updateTemporaryEnterWarehouseDTO,temporaryEnterWarehouseOld);
        int update = temporaryEnterWarehouseMapper.updateById(temporaryEnterWarehouseOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }


    @Override
    public TemporaryEnterWarehouse getTemporaryEnterWarehouseByDocNumberAndWarhouseId(String docNumber, String warhouseId) {
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number",docNumber);
        queryWrapper.eq("warehouse_id",warhouseId);
        return temporaryEnterWarehouseMapper.selectOne(queryWrapper);
    }

    @Override
    public TemporaryEnterWarehouse getTemporaryEnterWarehouseById(Integer id) {
        return temporaryEnterWarehouseMapper.selectById(id);
    }

    @Override
    public TemporaryEnterWarehouse getTemporaryEnterWarehouseByProcessInstanceId(String processInstanceId) {
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_instance_id",processInstanceId);
        return temporaryEnterWarehouseMapper.selectOne(queryWrapper);
    }
}
