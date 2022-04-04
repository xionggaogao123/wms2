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
import com.huanhong.wms.entity.OnShelf;
import com.huanhong.wms.entity.dto.AddOnShelfDTO;
import com.huanhong.wms.entity.dto.UpdateOnShelfDTO;
import com.huanhong.wms.entity.vo.OnShelfVO;
import com.huanhong.wms.mapper.OnShelfMapper;
import com.huanhong.wms.service.IOnShelfService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-24
 */
@Service
public class OnShelfServiceImpl extends SuperServiceImpl<OnShelfMapper, OnShelf> implements IOnShelfService {


    @Resource
    private OnShelfMapper onShelfMapper;

    @Override
    public Page<OnShelf> pageFuzzyQuery(Page<OnShelf> onShelfPage, OnShelfVO onShelfVO) {

        //新建QueryWrapper对象
        QueryWrapper<OnShelf> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(onShelfVO)) {
            return onShelfMapper.selectPage(onShelfPage, query);
        }
        //清点单编号
        query.like(StringUtils.isNotBlank(onShelfVO.getDocumentNumber()),"document_number",onShelfVO.getDocumentNumber());

        //物料编码
        query.like(StringUtils.isNotBlank(onShelfVO.getMaterialCoding()),"material_coding",onShelfVO.getMaterialCoding());

        //仓库
        query.like(StringUtils.isNotBlank(onShelfVO.getWarehouse()),"warehouse",onShelfVO.getWarehouse());

        //库区编号
        query.like(StringUtils.isNotBlank(onShelfVO.getWarehouseAreaId()),"warehouse_area_id",onShelfVO.getWarehouseAreaId());

        //货位编码
        query.like(StringUtils.isNotBlank(onShelfVO.getCargoSpaceId()),"cargo_space_id",onShelfVO.getCargoSpaceId());

        //清点状态
        query.eq(ObjectUtil.isNotNull(onShelfVO.getComplete()),"complete",onShelfVO.getComplete());


        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 清点单时间区间查询
         */
        if (ObjectUtil.isNotEmpty(onShelfVO.getCreateDateStart()) && ObjectUtil.isNotEmpty(onShelfVO.getCreateDateEnd())) {

            String createDateStart = dtf1.format(onShelfVO.getCreateDateStart());

            String createDateEnd = dtf1.format(onShelfVO.getCreateDateEnd());

            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }
        return onShelfMapper.selectPage(onShelfPage,query);
    }

    @Override
    public Result addOnShelf(AddOnShelfDTO addOnShelfDTO) {
        try {
            /**
             * 生成上架单据编码（SJ+年月日八位数字+五位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<OnShelf> queryOshelf = new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryOshelf.eq("warehouse", addOnShelfDTO.getWarehouse());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryOshelf.likeRight("document_number", "SJ" + today);
            /**
             * likeRigh: SJ+XXXXXXXX(当前年月日)
             */
            OnShelf maxOnshelf = onShelfMapper.selectOne(queryOshelf.orderByDesc("id").last("limit 1"));
            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxOnshelf)) {
                maxDocNum = maxOnshelf.getDocumentNumber();
            }
            String orderNo = null;
            //单据编码前缀-QD+年月日
            String code_pfix = "SJ" + today;
            if (maxDocNum != null && maxOnshelf.getDocumentNumber().contains(code_pfix)) {
                String code_end = maxOnshelf.getDocumentNumber().substring(10, 15);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 100000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "00001";
            }

            /**
             * 新增单据
             */
            OnShelf onShelf = new OnShelf();
            BeanUtil.copyProperties(addOnShelfDTO,onShelf);
            onShelf.setDocumentNumber(orderNo);
            int i =onShelfMapper.insert(onShelf);
            if (i > 0) {
                return Result.success(getOnshelfByDocNumAndWarehouseId(orderNo, addOnShelfDTO.getWarehouse()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增上架单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    @Override
    public Result updateOnshelf(UpdateOnShelfDTO updateOnShelfDTO) {

        OnShelf onShelfOld = getOnshelfById(updateOnShelfDTO.getId());
        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */
        BeanUtil.copyProperties(updateOnShelfDTO,onShelfOld);

        int update = onShelfMapper.updateById(onShelfOld);

        return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
    }

    @Override
    public OnShelf getOnshelfByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number",docNum);
        queryWrapper.eq("warehouse",warehouseId);
        return onShelfMapper.selectOne(queryWrapper);
    }

    @Override
    public OnShelf getOnshelfById(Integer id) {
        return onShelfMapper.selectById(id);
    }
}
