package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDetailsDTO;
import com.huanhong.wms.mapper.EnterWarehouseDetailsMapper;
import com.huanhong.wms.service.IEnterWarehouseDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 采购入库单明细表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-27
 */
@Slf4j
@Service
public class EnterWarehouseDetailsServiceImpl extends SuperServiceImpl<EnterWarehouseDetailsMapper, EnterWarehouseDetails> implements IEnterWarehouseDetailsService {


    @Resource
    private EnterWarehouseDetailsMapper enterWarehouseDetailsMapper;

    @Override
    public Result addEnterWarehouseDetails(List<AddEnterWarehouseDetailsDTO> listAddDto) {
        EnterWarehouseDetails enterWarehouseDetails = new EnterWarehouseDetails();
        try {
            for (int i = 0; i<=listAddDto.size(); i++){
                BeanUtil.copyProperties(listAddDto.get(i),enterWarehouseDetails);
                enterWarehouseDetailsMapper.insert(enterWarehouseDetails);
            }
            return Result.success();
        }catch (Exception e){
            log.error("明细插入失败！异常：",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "明细新增失败！");
        }
    }

    @Override
    public Result updateEnterWarehouseDetails(UpdateEnterWarehouseDetailsDTO updateEnterWarehouseDetailsDTO) {
        EnterWarehouseDetails enterWarehouseDetails = new EnterWarehouseDetails();
        BeanUtil.copyProperties(updateEnterWarehouseDetailsDTO,enterWarehouseDetails);
        int update = enterWarehouseDetailsMapper.updateById(enterWarehouseDetails);
        return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
    }

    @Override
    public List<EnterWarehouseDetails> getListWarehouseDetailsByDocNumberAndWarehosue(String documentNumber,String warehouse) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("original_document_number",documentNumber);
        queryWrapper.eq("warehouse",warehouse);
        List<EnterWarehouseDetails> listData = enterWarehouseDetailsMapper.selectList(queryWrapper);
        return listData;
    }

    @Override
    public EnterWarehouseDetails getEnterWarehouseDetailsByDetailsID(int id) {
        return enterWarehouseDetailsMapper.selectById(id);
    }
}
