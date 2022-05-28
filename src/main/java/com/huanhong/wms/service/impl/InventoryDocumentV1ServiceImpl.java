package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.EntityUtils;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryDetails;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDTO;
import com.huanhong.wms.entity.vo.InventoryDocumentVO;
import com.huanhong.wms.mapper.InventoryDocumentDetailsMapper;
import com.huanhong.wms.mapper.InventoryDocumentMapper;
import com.huanhong.wms.service.IInventoryDocumentService;
import com.huanhong.wms.service.InventoryDocumentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-21
 */
@Service
public class InventoryDocumentV1ServiceImpl implements InventoryDocumentService {

    @Resource
    private InventoryDocumentDetailsMapper inventoryDocumentDetailsMapper;

    @Resource
    private InventoryDocumentMapper inventoryDocumentMapper;

    @Override
    public Result selectById(Integer id) {
        InventoryDocument inventoryDocument = inventoryDocumentMapper.selectById(id);
        if (ObjectUtil.isEmpty(inventoryDocument)) {
            return Result.failure("未找到对应信息！");
        }
        QueryWrapper<InventoryDocumentDetails> queryWrapper = new QueryWrapper<InventoryDocumentDetails>();
        queryWrapper.eq("document_number",inventoryDocument.getDocumentNumber());
        List<InventoryDocumentDetails> inventoryDocumentDetails = inventoryDocumentDetailsMapper.selectList(queryWrapper);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mainValue", inventoryDocument);
        jsonObject.put("detailsValue", inventoryDocumentDetails);
        return Result.success(jsonObject);
    }
}
