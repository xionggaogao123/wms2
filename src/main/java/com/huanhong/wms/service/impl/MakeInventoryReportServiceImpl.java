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
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.dto.AddMakeInventoryReportDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryReportDTO;
import com.huanhong.wms.entity.vo.MakeInventoryReportVO;
import com.huanhong.wms.entity.vo.MakeInventoryVO;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.IMakeInventoryReportService;
import com.huanhong.wms.SuperServiceImpl;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-12
 */
@Service
public class MakeInventoryReportServiceImpl extends SuperServiceImpl<MakeInventoryReportMapper, MakeInventoryReport> implements IMakeInventoryReportService {

    @Reference
    private MakeInventoryReportMapper makeInventoryReportMapper;

    @Override
    public Page<MakeInventoryReport> pageFuzzyQuery(Page<MakeInventoryReport> makeInventoryReportPage, MakeInventoryReportVO makeInventoryReportVO) {

        //新建QueryWrapper对象
        QueryWrapper<MakeInventoryReport> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(makeInventoryReportVO)) {
            return makeInventoryReportMapper.selectPage(makeInventoryReportPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(makeInventoryReportVO.getDocumentNumber()), "document_number", makeInventoryReportVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(makeInventoryReportVO.getReportNumber()), "report_number", makeInventoryReportVO.getReportNumber());

        query.like(ObjectUtil.isNotNull(makeInventoryReportVO.getPlanStatus()), "plan_status", makeInventoryReportVO.getPlanStatus());

        query.like(ObjectUtil.isNotNull(makeInventoryReportVO.getAllMake()), "all_make", makeInventoryReportVO.getAllMake());

        query.like(ObjectUtil.isNotNull(makeInventoryReportVO.getCheckStatus()),"check_status",makeInventoryReportVO.getCheckStatus());

        query.like(StringUtils.isNotBlank(makeInventoryReportVO.getWarehouseId()),"warehouse_id",makeInventoryReportVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(makeInventoryReportVO.getSublibraryId()),"sublibrary_id",makeInventoryReportVO.getSublibraryId());

        query.like(StringUtils.isNotBlank(makeInventoryReportVO.getUserId()),"user_id",makeInventoryReportVO.getUserId());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 创建时间区间
         */
        if (ObjectUtil.isNotEmpty(makeInventoryReportVO.getStartTime())&& ObjectUtil.isNotEmpty(makeInventoryReportVO.getEndTime())) {
            String createDateStart = dtf1.format(makeInventoryReportVO.getStartTime());
            String createDateEnd = dtf1.format(makeInventoryReportVO.getEndTime());
            /**
             * 创建时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(start_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(end_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        return makeInventoryReportMapper.selectPage(makeInventoryReportPage, query);
    }

    @Override
    public Result addMakeInventoryReport(AddMakeInventoryReportDTO addMakeInventoryReportDTO) {

        try {
            /**
             * 生成调拨出库单编码（PDBG+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后四位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<MakeInventoryReport> queryWrapperMakeInventoryReport = new QueryWrapper<>();

            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryWrapperMakeInventoryReport.likeRight("report_number", "PDBG" + today);
            /**
             * likeRigh: PDBG+XXXXXXXX(当前年月日)
             */
            MakeInventoryReport maxMakeInventoryReport = makeInventoryReportMapper.selectOne(queryWrapperMakeInventoryReport.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;

            if (ObjectUtil.isNotEmpty(maxMakeInventoryReport)) {
                maxDocNum = maxMakeInventoryReport.getReportNumber();
            }

            String orderNo = null;

            //单据编码前缀-PDBG+年月日
            String code_pfix = "PDBG" + today;
            if (maxDocNum != null && maxMakeInventoryReport.getReportNumber().contains(code_pfix)) {
                String code_end = maxMakeInventoryReport.getReportNumber().substring(12, 16);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 10000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "0001";
            }

            /**
             * 新增单据
             */
            MakeInventoryReport makeInventoryReport = new MakeInventoryReport();
            BeanUtil.copyProperties(addMakeInventoryReportDTO, makeInventoryReport);
            makeInventoryReport.setReportNumber(orderNo);
            int i = makeInventoryReportMapper.insert(makeInventoryReport);
            if (i > 0) {
                return Result.success(getMakeInventoryReportByDocNumAndWarehouse(addMakeInventoryReportDTO.getWarehouseId(),orderNo), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        }catch (Exception e){
            log.error("新增盘点报告异常",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常！");
        }

    }

    @Override
    public Result updateMakeInventoryReport(UpdateMakeInventoryReportDTO updateMakeInventoryReportDTO) {
        MakeInventoryReport makeInventoryReportOld = getMakeInventoryReportById(updateMakeInventoryReportDTO.getId());
        BeanUtil.copyProperties(updateMakeInventoryReportDTO,makeInventoryReportOld);
        int update = makeInventoryReportMapper.updateById(makeInventoryReportOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public MakeInventoryReport getMakeInventoryReportById(Integer id) {
        return makeInventoryReportMapper.selectById(id);
    }

    @Override
    public MakeInventoryReport getMakeInventoryReportByDocNumAndWarehouse(String docNum, String warehouseId) {
        QueryWrapper<MakeInventoryReport> queryWrapperMakeInventoryReport = new QueryWrapper<>();
        queryWrapperMakeInventoryReport.eq("report_number",docNum);
        queryWrapperMakeInventoryReport.eq("warehouse_id",warehouseId);
        return makeInventoryReportMapper.selectOne(queryWrapperMakeInventoryReport);
    }
}
