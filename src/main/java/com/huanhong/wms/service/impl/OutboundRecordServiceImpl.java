package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.DataUtil;
import com.huanhong.common.units.excel.ExportExcel;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.param.MaterialOutInParam;
import com.huanhong.wms.entity.param.OutboundDetailPage;
import com.huanhong.wms.entity.vo.OutboundDetailVo;
import com.huanhong.wms.entity.vo.OutboundRecordVO;
import com.huanhong.wms.entity.vo.WarehousingDetailVo;
import com.huanhong.wms.mapper.OutboundRecordMapper;
import com.huanhong.wms.mapper.WarehousingRecordMapper;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.IOutboundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 出库记录 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-07
 */
@Service
public class OutboundRecordServiceImpl extends SuperServiceImpl<OutboundRecordMapper, OutboundRecord> implements IOutboundRecordService {


    @Resource
    private OutboundRecordMapper outboundRecordMapper;

    @Resource
    private WarehousingRecordMapper warehousingRecordMapper;

    @Autowired
    private OssProperties ossProperties;

    /**
     * 分页查询
     *
     * @param outboundRecordPage
     * @param outboundRecordVO
     * @return
     */
    @Override
    public Page<OutboundRecord> pageFuzzyQuery(Page<OutboundRecord> outboundRecordPage, OutboundRecordVO outboundRecordVO) {

        //新建QueryWrapper对象
        QueryWrapper<OutboundRecord> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(outboundRecordVO)) {
            return outboundRecordMapper.selectPage(outboundRecordPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(outboundRecordVO.getDocumentNumber()), "document_number", outboundRecordVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getWarehouseId()), "warehouse_id", outboundRecordVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getMaterialCoding()), "material_coding", outboundRecordVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getCargoSpaceId()), "cargo_space_id", outboundRecordVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getBatch()), "batch", outboundRecordVO.getBatch());

        query.like(ObjectUtil.isNotNull(outboundRecordVO.getStatus()), "status", outboundRecordVO.getStatus());

        query.like(ObjectUtil.isNotNull(outboundRecordVO.getConsignor()),"consignor",outboundRecordVO.getConsignor());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 创建时间时间区间
         */
        if (ObjectUtil.isNotEmpty(outboundRecordVO.getCreateDateStart()) && ObjectUtil.isNotEmpty(outboundRecordVO.getCreateDateEnd())) {
            String enterDateStart = dtf1.format(outboundRecordVO.getCreateDateStart());
            String enterDateEnd = dtf1.format(outboundRecordVO.getCreateDateEnd());
            /**
             * 创建时间时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + enterDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + enterDateEnd + "')");

        }
        return outboundRecordMapper.selectPage(outboundRecordPage, query);
    }

    @Override
    public Result addOutboundRecord(AddOutboundRecordDTO addOutboundRecordDTO) {
        OutboundRecord outboundRecord = getOutboundRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(addOutboundRecordDTO.getDocumentNumber(), addOutboundRecordDTO.getCargoSpaceId(), addOutboundRecordDTO.getMaterialCoding(), addOutboundRecordDTO.getBatch());
        if (null != outboundRecord) {
            return Result.success(outboundRecord);
        }
        outboundRecord = new OutboundRecord();
        BeanUtil.copyProperties(addOutboundRecordDTO, outboundRecord);
        int add = outboundRecordMapper.insert(outboundRecord);
        return add > 0 ? Result.success(getOutboundRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(addOutboundRecordDTO.getDocumentNumber(), addOutboundRecordDTO.getCargoSpaceId(), addOutboundRecordDTO.getMaterialCoding(), addOutboundRecordDTO.getBatch())) : Result.failure("新增失败");
    }

    @Override
    public Result addOutboundRecordList(List<AddOutboundRecordDTO> addOutboundRecordDTOList) {
        int count = 0;
        for (AddOutboundRecordDTO addOutboundRecordDTO : addOutboundRecordDTOList
        ) {
            OutboundRecord outboundRecord = new OutboundRecord();
            BeanUtil.copyProperties(addOutboundRecordDTO, outboundRecord);
            int add = outboundRecordMapper.insert(outboundRecord);
            if (add > 1) {
                count++;
            }
        }
        return count == addOutboundRecordDTOList.size() ? Result.success("新增记录成功") : Result.failure("新增记录失败");
    }

    @Override
    public Result updateOutboundRecord(UpdateOutboundRecordDTO updateOutboundRecordDTO) {
        OutboundRecord outboundRecord = new OutboundRecord();
        BeanUtil.copyProperties(updateOutboundRecordDTO, outboundRecord);
        int update = outboundRecordMapper.updateById(outboundRecord);
        return update > 0 ? Result.success("更新成功！") : Result.failure("更新失败！");
    }

    @Override
    public OutboundRecord getOutboundRecordById(Integer id) {
        return outboundRecordMapper.selectById(id);
    }

    @Override
    public List<OutboundRecord> getOutboundRecordListByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        return outboundRecordMapper.selectList(queryWrapper);
    }

    @Override
    public List<OutboundRecord> getOutboundRecordByDocNumAndWarehouseIdAndMaterialCoding(String docNum, String warehouseId, String materialCoding) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("warehouse_id", warehouseId);
        return outboundRecordMapper.selectList(queryWrapper);
    }

    @Override
    public OutboundRecord getOutboundRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(String docNum, String cargoSpace, String materialCoding, String batch) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("cargo_space_id", cargoSpace);
        queryWrapper.eq("batch", batch);
        queryWrapper.last("limit 1");
        return outboundRecordMapper.selectOne(queryWrapper);
    }

    @Override
    public Result<Page<OutboundDetailVo>> outboundDetail(OutboundDetailPage page) {
        Page<OutboundDetailVo> pageData = outboundRecordMapper.outboundDetail(page);
        int i = 1;
        for (OutboundDetailVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void outboundDetailExport(OutboundDetailPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<OutboundDetailVo> pageData = outboundRecordMapper.outboundDetail(page);
        int i = 1;
        for (OutboundDetailVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        params.put("gmtStart", page.getGmtStart());
        params.put("gmtEnd", page.getGmtEnd());
        String templatePath = ossProperties.getPath() + "templates/outboundDetail.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "入库明细表.xlsx", params, request, response);

    }

    @Override
    public Result<Object> getTheTrendOfWarehouseInboundAndOutbound(MaterialOutInParam param) {
        Map<String, Object> map = new HashMap<>();
        if (param.getType() == null){
            param.setType(1);
        }
        List<Map<String, Object>> out = outboundRecordMapper.getTheTrendOfWarehouseOutboundByParam(param);
        map.put("out", out);
        List<Map<String, Object>> in = warehousingRecordMapper.getTheTrendOfWarehouseInboundByParam(param);
        map.put("in", in);
        return Result.success(map);
    }

    @Override
    public Result<Object> getStatisticalAnalysisOfInboundAndOutboundAmount(MaterialOutInParam param) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> out = outboundRecordMapper.getTheTotalMoneyOfOutboundByParam(param);
        map.put("out", out);
        List<Map<String, Object>> in = warehousingRecordMapper.getTheTotalMoneyOfWarehouseInboundByParam(param);
        map.put("in", in);
        return Result.success(map);
    }
}
