package com.huanhong.wms.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessAssignment;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.WarehouseVo;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.IWarehouseManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 仓库管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class WarehouseManagementServiceImpl extends SuperServiceImpl<WarehouseManagementMapper, WarehouseManagement> implements IWarehouseManagementService {

    @Resource
    private WarehouseManagementMapper warehouseManagementMapper;

    @Resource
    private UserMapper userMapper;
    @Resource
    private SublibraryManagementMapper sublibraryManagementMapper;
    @Resource
    private WarehouseAreaManagementMapper warehouseAreaManagementMapper;
    @Resource
    private ShelfManagementMapper shelfManagementMapper;
    @Resource
    private CargoSpaceManagementMapper cargoSpaceManagementMapper;
    @Resource
    private ProcessAssignmentMapper processAssignmentMapper;
    @Resource
    private OutboundRecordMapper outboundRecordMapper;
    @Resource
    private WarehousingRecordMapper warehousingRecordMapper;

    @Override
    public List<WarehouseManagement> getWarehouseByCompanyId(Integer CompanyId) {
        QueryWrapper<WarehouseManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id", CompanyId);
        return warehouseManagementMapper.selectList(queryWrapper);
    }

    @Override
    public WarehouseManagement getWarehouseByWarehouseId(String WarehouseId) {
        QueryWrapper<WarehouseManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", WarehouseId);
        WarehouseManagement warehouseManagement = warehouseManagementMapper.selectOne(wrapper);
        return warehouseManagement;
    }

//    @Override
//    public List<String> fuzzyQuerySelectList(String field, String value) {
//        List<String> resultList = warehouseManagementMapper.fuzzyQuerySelectList(field, value);
//        return resultList;
//    }

    @Override
    public Page<WarehouseManagement> pageFuzzyQuery(Page<WarehouseManagement> warehouseManagementPage, WarehouseVo warehouseVo) {

        //新建QueryWrapper对象
        QueryWrapper<WarehouseManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehouseVo)) {
            return baseMapper.selectPage(warehouseManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(ObjectUtil.isNotEmpty(warehouseVo.getCompanyId()), "company_id", warehouseVo.getCompanyId());

        query.like(StringUtils.isNotBlank(warehouseVo.getWarehouseId()), "warehouse_id", warehouseVo.getWarehouseId());

        query.like(StringUtils.isNotBlank(warehouseVo.getWarehouseName()), "warehouse_name", warehouseVo.getWarehouseName());

        return baseMapper.selectPage(warehouseManagementPage, query);
    }

    /**
     * 查询仓库是否停用 0-使用中 1-停用
     *
     * @param warehouseId
     * @return
     */
    @Override
    public int isStopUsing(String warehouseId) {
        WarehouseManagement warehouseManagement = getWarehouseByWarehouseId(warehouseId);
        return warehouseManagement.getStopUsing();
    }

    @Override
    public Result<Object> selectWarehouseInfo(Integer id) {
        Map<String, Object> map = new HashMap<>();
        User user = userMapper.selectById(id);
        List<ProcessAssignment> processAssignments = processAssignmentMapper.selectList(new QueryWrapper<ProcessAssignment>().eq("user_account", user.getLoginName()).eq("status", 0).orderByDesc("id").last("limit 10"));
        map.put("task", processAssignments);
        if (user.getCompanyId() == null) {
            return Result.success(map);
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        QueryWrapper<WarehouseManagement> qw = new QueryWrapper<>();
        qw.eq("company_id", user.getCompanyId());
        List<WarehouseManagement> warehouseManagements = warehouseManagementMapper.selectList(qw);
        if (warehouseManagements.size() > 0) {
            for (WarehouseManagement wm : warehouseManagements) {
                //    查询仓库占用率
                //    查询子库
                //    查询库区
                //    查询货架
                Map<String, Object> warehouseMap = new HashMap<>();
                int total = 0;
                float occupation = 0.00f;
                List<Map<String, Object>> list = warehouseManagementMapper.countWarehouseOccupationByWarehouse(wm.getWarehouseId());
                for (Map<String, Object> map0 : list) {
                    total = total + Convert.toInt(map0.get("num"));
                    if (Convert.toFloat(map0.get("full"), 0.00f) > 0) {
                        occupation = occupation + Convert.toFloat(map0.get("full")) * Convert.toInt(map0.get("num"));
                    }
                }
                warehouseMap.put("warehouseName", wm.getWarehouseName());
                warehouseMap.put("occupation", String.format("%.2f", (occupation / total) * 100));

                //    查询品类占用情况
                List<Map<String, Object>> cas = warehouseManagementMapper.selectMaterialByWarehouse(wm.getWarehouseId());
                warehouseMap.put("material", cas);


                //     查询出库情况
                Map<String, Object> outMap = new HashMap<>();
                Double totalMoney = outboundRecordMapper.getTotalOutboundByWarehouse(wm.getWarehouseId());
                outMap.put("total", totalMoney);
                List<Map<String, Object>> outboundRecords = new ArrayList<>();
                if (totalMoney>0){
                    outboundRecords = outboundRecordMapper.countOutboundRecordByWarehouse(wm.getWarehouseId(),totalMoney);
                }
                outMap.put("outList", outboundRecords);
                warehouseMap.put("out", outMap);


                //for (int i = 0; i < 3; i++) {
                //    Map<String, Object> record = new HashMap<>();
                //    switch (i) {
                //        case 1:
                //            record.put("name", "手套");
                //            record.put("num", "20%");
                //            outboundRecords.add(record);
                //        case 2:
                //            record.put("name", "钢材");
                //            record.put("num", "67%");
                //            outboundRecords.add(record);
                //        case 0:
                //            record.put("name", "螺丝");
                //            record.put("num", "13%");
                //            outboundRecords.add(record);
                //
                //    }
                //}
                //outMap.put("outList", outboundRecords);
                //warehouseMap.put("out", outMap);

//     查询入库情况
                Map<String, Object> inMap = new HashMap<>();
                Double inMoney = outboundRecordMapper.getTotalWarehousingRecordByWarehouse(wm.getWarehouseId());
                inMap.put("total", inMoney);
                List<Map<String, Object>> inRecords = new ArrayList<>();
                if (inMoney>0){
                    inRecords = warehousingRecordMapper.countWarehousingRecordByWarehouse(wm.getWarehouseId(),inMoney);
                }
                inMap.put("inList", inRecords);
                warehouseMap.put("in", inMap);

                //for (int i = 0; i < 3; i++) {
                //    Map<String, Object> record = new HashMap<>();
                //    switch (i) {
                //        case 1:
                //            record.put("name", "手套");
                //            record.put("num", "10%");
                //            inRecords.add(record);
                //        case 2:
                //            record.put("name", "钢材");
                //            record.put("num", "77%");
                //            inRecords.add(record);
                //        case 0:
                //            record.put("name", "螺丝");
                //            record.put("num", "13%");
                //            inRecords.add(record);
                //
                //    }
                //}
                //inMap.put("inList", inRecords);
                //warehouseMap.put("in", inMap);

                mapList.add(warehouseMap);
            }
            map.put("warehouseList", mapList);
        }
        return Result.success(map);
    }

    @Override
    public Result<Object> getWarehouseUsageAnalysis(String warehouseId) {
        QueryWrapper<WarehouseManagement> qw = new QueryWrapper<>();
        qw.eq("warehouse_id", warehouseId);
        List<WarehouseManagement> warehouseManagements = warehouseManagementMapper.selectList(qw);
        if (warehouseManagements.size() > 0) {
            WarehouseManagement wm = warehouseManagements.get(0);
            //    查询仓库占用率
            //    查询子库
            //    查询库区
            //    查询货架
            Map<String, Object> warehouseMap = new HashMap<>();
            int total = 0;
            float occupation = 0.00f;
            List<Map<String, Object>> list = warehouseManagementMapper.countWarehouseOccupationByWarehouse(wm.getWarehouseId());
            for (Map<String, Object> map0 : list) {
                total = total + Convert.toInt(map0.get("num"));
                if (Convert.toFloat(map0.get("full"), 0.00f) > 0) {
                    occupation = occupation + Convert.toFloat(map0.get("full")) * Convert.toInt(map0.get("num"));
                }
            }
            log.debug("占用量："+occupation +"；总数："+total);
            warehouseMap.put("warehouseName", wm.getWarehouseName());
            warehouseMap.put("occupation", String.format("%.2f", (occupation / total) * 100));

            //   查询品类占用情况
            List<Map<String, Object>> cas = warehouseManagementMapper.selectMaterialByWarehouse(wm.getWarehouseId());
            warehouseMap.put("material", cas);
            return Result.success(warehouseMap);
        } else {
            return Result.failure("该仓库不存在");
        }
    }

}
