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
import com.huanhong.wms.entity.WarehousingRecord;
import com.huanhong.wms.entity.dto.AddWarehousingRecordDTO;
import com.huanhong.wms.entity.dto.UpdateWarehousingRecordDTO;
import com.huanhong.wms.entity.param.InventoryRecordPage;
import com.huanhong.wms.entity.param.WarehousingDetailPage;
import com.huanhong.wms.entity.vo.InventoryRecordVo;
import com.huanhong.wms.entity.vo.WarehousingDetailVo;
import com.huanhong.wms.entity.vo.WarehousingRecordVO;
import com.huanhong.wms.mapper.WarehousingRecordMapper;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.IWarehousingRecordService;
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
 * 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-07
 */
@Service
public class WarehousingRecordServiceImpl extends SuperServiceImpl<WarehousingRecordMapper, WarehousingRecord> implements IWarehousingRecordService {


    @Resource
    private WarehousingRecordMapper warehousingRecordMapper;

    @Autowired
    private OssProperties ossProperties;

    @Override
    public Page<WarehousingRecord> pageFuzzyQuery(Page<WarehousingRecord> warehousingRecordPage, WarehousingRecordVO warehousingRecordVO) {

        //新建QueryWrapper对象
        QueryWrapper<WarehousingRecord> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehousingRecordVO)) {
            return warehousingRecordMapper.selectPage(warehousingRecordPage, query);
        }

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getDocumentNumber()), "document_number", warehousingRecordVO.getDocumentNumber());

        query.like(ObjectUtil.isNotNull(warehousingRecordVO.getEnterType()), "out_type", warehousingRecordVO.getEnterType());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getWarehouseId()), "warehouse_id", warehousingRecordVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getCargoSpaceId()), "cargo_space_id", warehousingRecordVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getMaterialCoding()), "material_coding", warehousingRecordVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getBatch()), "batch", warehousingRecordVO.getBatch());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 创建时间时间区间
         */
        if (ObjectUtil.isNotEmpty(warehousingRecordVO.getCreateDateStart()) && ObjectUtil.isNotEmpty(warehousingRecordVO.getCreateDateEnd())) {
            String enterDateStart = dtf1.format(warehousingRecordVO.getCreateDateStart());
            String enterDateEnd = dtf1.format(warehousingRecordVO.getCreateDateEnd());
            /**
             * 创建时间时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + enterDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + enterDateEnd + "')");

        }
        return warehousingRecordMapper.selectPage(warehousingRecordPage, query);
    }

    @Override
    public Result addWarehousingRecord(AddWarehousingRecordDTO addWarehousingRecordDTO) {
        WarehousingRecord warehousingRecord = new WarehousingRecord();
        BeanUtil.copyProperties(addWarehousingRecordDTO, warehousingRecord);
        int add = warehousingRecordMapper.insert(warehousingRecord);
        return add > 0 ? Result.success() : Result.failure("新增失败");
    }

    @Override
    public Result updateWarehousingRecord(UpdateWarehousingRecordDTO updateWarehousingRecordDTO) {
        WarehousingRecord warehousingRecord = new WarehousingRecord();
        BeanUtil.copyProperties(updateWarehousingRecordDTO, warehousingRecord);
        int update = warehousingRecordMapper.updateById(warehousingRecord);
        return update > 0 ? Result.success("更新成功！") : Result.failure("更新失败！");
    }

    @Override
    public WarehousingRecord getWarehousingRecordById(Integer id) {
        return warehousingRecordMapper.selectById(id);
    }

    @Override
    public List<WarehousingRecord> getWarehousingRecordByDocNumAndWarhouseId(String docNumber, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number", docNumber);
        queryWrapper.eq("warehouse_id", warehouseId);
        return warehousingRecordMapper.selectList(queryWrapper);
    }

    @Override
    public Result<Page<WarehousingDetailVo>> warehousingDetail(WarehousingDetailPage page) {
        Page<WarehousingDetailVo> pageData = warehousingRecordMapper.warehousingDetail(page);
        int i = 1;
        for (WarehousingDetailVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            ii.setEnterTypeStr(DataUtil.getEnterType(ii.getEnterType()));
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void warehousingDetailExport(WarehousingDetailPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<WarehousingDetailVo> pageData = warehousingRecordMapper.warehousingDetail(page);
        int i = 1;
        for (WarehousingDetailVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            ii.setEnterTypeStr(DataUtil.getEnterType(ii.getEnterType()));
            i++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("list", pageData.getRecords());
        params.put("gmtCreate", new Date());
        params.put("userName", page.getUserName());
        params.put("inDateEnd", page.getInDateEnd());
        params.put("inDateStart", page.getInDateStart());
        String templatePath = ossProperties.getPath() + "templates/warehousingDetail.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "入库明细表.xlsx", params, request, response);

    }

    @Override
    public Result<Page<InventoryRecordVo>> inventoryRecord(InventoryRecordPage page) {
        Page<InventoryRecordVo> pageData = warehousingRecordMapper.inventoryRecord(page);
        int i = 1;
        for (InventoryRecordVo ii : pageData.getRecords()) {
            ii.setIndex(i);
            ii.setConsignorStr(DataUtil.getConsignor(ii.getConsignor()));
            i++;
        }
        return Result.success(pageData);
    }

    @Override
    public void inventoryRecordExport(InventoryRecordPage page, HttpServletRequest request, HttpServletResponse response) {
        Page<InventoryRecordVo> pageData = warehousingRecordMapper.inventoryRecord(page);
        int i = 1;
        for (InventoryRecordVo ii : pageData.getRecords()) {
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
        String templatePath = ossProperties.getPath() + "templates/inventoryRecord.xlsx";
        ExportExcel.exportExcel(templatePath, ossProperties.getPath() + "temp/", "库存流水账.xlsx", params, request, response);

    }
}
