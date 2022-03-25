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
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.dto.AddMakeInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDTO;
import com.huanhong.wms.entity.vo.MakeInventoryVO;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.service.IMakeInventoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-28
 */
@Service
public class MakeInventoryServiceImpl extends SuperServiceImpl<MakeInventoryMapper, MakeInventory> implements IMakeInventoryService {

    @Resource
    private MakeInventoryMapper makeInventoryMapper;

    /**
     * 分页查询
     * @param makeInventoryPage
     * @param makeInventoryVO
     * @return
     */
    @Override
    public Page<MakeInventory> pageFuzzyQuery(Page<MakeInventory> makeInventoryPage, MakeInventoryVO makeInventoryVO) {

        //新建QueryWrapper对象
        QueryWrapper<MakeInventory> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(makeInventoryVO)) {
            return makeInventoryMapper.selectPage(makeInventoryPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        //盘点单单据编号
        query.like(StringUtils.isNotBlank(makeInventoryVO.getDocumentNumber()), "document_number", makeInventoryVO.getDocumentNumber());

        //子库编号
        query.like(StringUtils.isNotBlank(makeInventoryVO.getSublibraryId()), "sublibrary_id", makeInventoryVO.getSublibraryId());

        //库区编号
        query.like(StringUtils.isNotBlank(makeInventoryVO.getWarehouseAreaId()), "warehouse_area_id",makeInventoryVO.getWarehouseAreaId());

        //货位编码
        query.like(StringUtils.isNotBlank(makeInventoryVO.getCargoSpaceId()),"cargo_space_id",makeInventoryVO.getCargoSpaceId());

        //物料编码
        query.like(StringUtils.isNotBlank(makeInventoryVO.getMaterialCoding()),"material_coding",makeInventoryVO.getMaterialCoding());

        //物料名称
        query.like(StringUtils.isNotBlank(makeInventoryVO.getMaterialName()),"material_name",makeInventoryVO.getMaterialName());

        //批次
        query.like(StringUtils.isNotBlank(makeInventoryVO.getBatch()),"batch",makeInventoryVO.getBatch());

        //盘点状态
        query.eq(ObjectUtil.isNotNull(makeInventoryVO.getCheckStatus()),"check_status",makeInventoryVO.getCheckStatus());


        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 清点单时间区间查询
         */
        if (ObjectUtil.isNotEmpty(makeInventoryVO.getCreateDateStart()) && ObjectUtil.isNotEmpty(makeInventoryVO.getCreateDateEnd())) {

            String createDateStart = dtf1.format(makeInventoryVO.getCreateDateStart());

            String createDateEnd = dtf1.format(makeInventoryVO.getCreateDateEnd());

            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return makeInventoryMapper.selectPage(makeInventoryPage, query);

    }

    /**
     * 新增盘点单
     * @param addMakeInventoryDTO
     * @return
     */
    @Override
    public Result addMakeInventory(AddMakeInventoryDTO addMakeInventoryDTO) {

        try {
            /**
             * 生成盘点单据编码（PD+年月日八位数字+五位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后五位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<MakeInventory> queryMakeInventory = new QueryWrapper<>();

            /**
             * 当前仓库
             */
            queryMakeInventory.likeRight("sublibrary_id", addMakeInventoryDTO.getSublibraryId().substring(0,4));

            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryMakeInventory.likeRight("document_number", "PD" + today);

            /**
             * likeRight: PD+XXXXXXXX(当前年月日)
             */
            MakeInventory maxMakeInventory = makeInventoryMapper.selectOne(queryMakeInventory.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxMakeInventory)) {
                maxDocNum = maxMakeInventory.getDocumentNumber();
            }

            String orderNo = null;
            //单据编码前缀-PD+年月日
            String code_pfix = "PD" + today;
            if (maxDocNum != null && maxMakeInventory.getDocumentNumber().contains(code_pfix)) {
                String code_end = maxMakeInventory.getDocumentNumber().substring(10, 15);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 100000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "00001";
            }

            /**
             * 新增单据
             */
            MakeInventory makeInventory = new MakeInventory();
            BeanUtil.copyProperties(addMakeInventoryDTO, makeInventory);
            makeInventory.setDocumentNumber(orderNo);
            int i = makeInventoryMapper.insert(makeInventory);
            if (i > 0) {
                return Result.success(getMakeInventoryByDocNumAndWarehouse(orderNo, addMakeInventoryDTO.getSublibraryId().substring(0,4)), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增盘点单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    /**
     * 更新盘点单
     * @param updateMakeInventoryDTO
     * @return
     */
    @Override
    public Result updateMakeInventory(UpdateMakeInventoryDTO updateMakeInventoryDTO) {

        //获取最新版本号的数据
        MakeInventory makeInventoryOld = getMakeInventoryById(updateMakeInventoryDTO.getId());

        /**
         * vesion 对比veision 如果一致则更新并加一  不一致则不更新
         */

        //子库编号
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getSublibraryId())){
            makeInventoryOld.setSublibraryId(updateMakeInventoryDTO.getSublibraryId());
        }

        //库区编号
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getWarehouseAreaId())){
            makeInventoryOld.setWarehouseAreaId(updateMakeInventoryDTO.getWarehouseAreaId());
        }

        //货位编号
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getCargoSpaceId())){
            makeInventoryOld.setCargoSpaceId(updateMakeInventoryDTO.getCargoSpaceId());
        }

        //物料编码
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getMaterialCoding())){
            makeInventoryOld.setMaterialCoding(updateMakeInventoryDTO.getMaterialCoding());
        }

        //物料名称
        if(StringUtils.isNotBlank(updateMakeInventoryDTO.getMaterialName())){
            makeInventoryOld.setMaterialName(updateMakeInventoryDTO.getMaterialName());
        }

        //规格型号
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getSpecificationModel())){
            makeInventoryOld.setSpecificationModel(updateMakeInventoryDTO.getSpecificationModel());
        }

        //计量单位
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getMeasurementUnit())){
            makeInventoryOld.setMeasurementUnit(updateMakeInventoryDTO.getMeasurementUnit());
        }

        //批次
        if (StringUtils.isNotBlank(updateMakeInventoryDTO.getBatch())){
            makeInventoryOld.setBatch(updateMakeInventoryDTO.getBatch());
        }

        //库存数量
        if (ObjectUtil.isNotNull(updateMakeInventoryDTO.getInventoryCredit())){
            makeInventoryOld.setInventoryCredit(updateMakeInventoryDTO.getInventoryCredit());
        }

        //实盘数量
        if (ObjectUtil.isNotNull(updateMakeInventoryDTO.getCheckCredit())){
            makeInventoryOld.setCheckCredit(updateMakeInventoryDTO.getCheckCredit());
        }


        //盘点状态
        if (ObjectUtil.isNotNull(updateMakeInventoryDTO.getCheckStatus())){
            makeInventoryOld.setCheckStatus(updateMakeInventoryDTO.getCheckStatus());
        }

        //备注
        if(StringUtils.isNotBlank(updateMakeInventoryDTO.getRemark())){
            makeInventoryOld.setRemark(updateMakeInventoryDTO.getRemark());
        }

        int update = makeInventoryMapper.updateById(makeInventoryOld);

        return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
    }

    /**
     * 根据ID获取盘点单信息
     * @param id
     * @return
     */
    @Override
    public MakeInventory getMakeInventoryById(Integer id) {
        MakeInventory makeInventory = makeInventoryMapper.selectById(id);
        return ObjectUtil.isNotNull(makeInventory) ? makeInventory : null;
    }

    /**
     * 根据单据号和仓库获取盘点单
     * @param docNum
     * @param warehouse
     * @return
     */
    @Override
    public MakeInventory getMakeInventoryByDocNumAndWarehouse(String docNum, String warehouse) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("document_number",docNum);
        queryWrapper.likeRight("sublibrary_id",warehouse);
        MakeInventory makeInventory = makeInventoryMapper.selectOne(queryWrapper);
        return ObjectUtil.isNotNull(makeInventory) ? makeInventory : null;
    }
}