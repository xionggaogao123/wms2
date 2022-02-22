package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.dto.AddCargoSpacedDTO;
import com.huanhong.wms.entity.dto.AddShelfDTO;
import com.huanhong.wms.entity.dto.UpdateShelfDTO;
import com.huanhong.wms.entity.vo.ShelfVO;
import com.huanhong.wms.mapper.ShelfManagementMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.IShelfManagementService;
import com.huanhong.wms.service.IWarehouseAreaManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/shelf-management")
@ApiSort()
@Api(tags = "货架管理")
public class ShelfManagementController extends BaseController {

    @Resource
    private IShelfManagementService shelfManagementService;

    @Resource
    private IWarehouseAreaManagementService warehouseAreaManagementService;

    @Resource
    private ShelfManagementMapper shelfManagementMapper;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(SublibraryManagementController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页组合查询货架")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<ShelfManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              ShelfVO shelfVO//查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ShelfManagement> pageResult = shelfManagementService.pageFuzzyQuery(new Page<>(current, size), shelfVO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加货架管理")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddShelfDTO addShelfDTO) {

        try {
            /**
             * 验证货架编码是否合法
             */
            //是否是三位字符
            if (addShelfDTO.getShelfId().length()==3) {
                String str = addShelfDTO.getShelfId();
                //第一位是否大写且I和O以外的字母
                if (StrUtils.isEnglish(String.valueOf(str.charAt(0)))){
                         if (StrUtils.isNumeric(str.substring(1))){
                           //符合规则 拼接库区编号和货架编号为完整货架编号
                           addShelfDTO.setShelfId(addShelfDTO.getWarehouseAreaId()+addShelfDTO.getShelfId());
                         }else {
                                return Result.failure(ErrorCode.SYSTEM_ERROR, "货架编号是一位大写字母(I和O除外)加两位数字");
                               }
                    }else {
                           return Result.failure(ErrorCode.SYSTEM_ERROR, "货架编号是一位大写字母(I和O除外)加两位数字");}
             }else {
                    return Result.failure(ErrorCode.SYSTEM_ERROR, "货架编号是一位大写字母(I和O除外)加两位数字");
             }


            ShelfManagement shelfManagementIsExist = shelfManagementService.getShelfByShelfId(addShelfDTO.getShelfId());
            WarehouseAreaManagement warehouseAreaManagementIsExist = warehouseAreaManagementService.getWarehouseAreaByWarehouseAreaId(addShelfDTO.getWarehouseAreaId());

            //判断库区是否存在
            if (ObjectUtil.isEmpty(warehouseAreaManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "库区不存在,无法添加货架");
            }

            //查看库区是否停用
            if (warehouseAreaManagementService.isStopUsing(addShelfDTO.getWarehouseAreaId())!=0){
                return Result.failure(ErrorCode.DATA_IS_NULL, "库区停用中,无法添加货架");
            }

            //货架编号重复判定
            if (ObjectUtil.isNotEmpty(shelfManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "货架编号重复,货架已存在");
            }
            try {
                ShelfManagement shelfManagement = new ShelfManagement();
                BeanUtil.copyProperties(addShelfDTO,shelfManagement);
                int insert = shelfManagementMapper.insert(shelfManagement);
                int i = 0;
                if (insert > 0) {
                    LOGGER.info("添加货架成功");
                    //自动创建货位
                    AddCargoSpacedDTO addCargoSpacedDTO = new AddCargoSpacedDTO();
                    addCargoSpacedDTO.setShelfId(shelfManagement.getShelfId());
                    try{
                       i = cargoSpaceManagementService.addCargoSpace(addCargoSpacedDTO);
                    }catch (Exception e){
                        LOGGER.error("自动生成货位失败--异常",e);
                    }
                    if (i>0){
                        return Result.success("货架创建成功,货位自动生成");
                    }else {
                        return Result.success("货架创建成功,货位自动生成出错");
                    }
                } else {
                    LOGGER.error("添加货架失败");
                    return render(insert>0);
                }
            } catch (Exception e) {
                LOGGER.error("添加货架错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加货架失败--处理（判断库区编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新货架管理", notes = "生成代码")
    @PutMapping("/update")
    public Result updateShelfByShelfId(@Valid @RequestBody UpdateShelfDTO updateShelfDTO) {
        UpdateWrapper<ShelfManagement> updateWrapper = new UpdateWrapper<>();
        try {
            //空值判断
            if (StringUtils.isBlank(updateShelfDTO.getShelfId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "货架编码不能为空");
            }

            //判断更新的货架是否存在
            ShelfManagement shelfManagementIsExist = shelfManagementService.getShelfByShelfId(updateShelfDTO.getShelfId());
            if (ObjectUtil.isEmpty(shelfManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此货架编码，货架不存在");
            }

            /**
             * 查询货架是否停用 0-使用中  1-单独停用
             *
             */
            //父级停用无法手动单独启用
            ShelfManagement shelfManagement = shelfManagementService.getShelfByShelfId(updateShelfDTO.getShelfId());
            if (warehouseAreaManagementService.isStopUsing(shelfManagement.getWarehouseAreaId())==1){
                    return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "库区已停用,货架无法编辑!");
            }

            //单独停用可以手动修改更新为启用状态
            if (shelfManagementService.isStopUsing(updateShelfDTO.getShelfId())==1){
                if (updateShelfDTO.getStopUsing()!=0) {
                    return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "货架已停用,无法编辑！");
                }
            }

            updateWrapper.eq("shelf_id", updateShelfDTO.getShelfId());
            ShelfManagement updateShlef = new ShelfManagement();
            BeanUtil.copyProperties(updateShelfDTO, updateShlef);
            int update = shelfManagementMapper.update(updateShlef, updateWrapper);
            String parentCode = updateShlef.getShelfId();
            if (update > 0 && ObjectUtil.isNotNull(shelfManagement.getStopUsing())) {
                //如果货架更新成功 判断此次更新货架是否处于启用状态
                if (updateShlef.getStopUsing()==0){
                        cargoSpaceManagementService.stopUsingByParentCode(parentCode,true);
                } else {
                    //若是停用状态 则将停用状态为 0-启用 的子级全部停用
                        cargoSpaceManagementService.stopUsingByParentCode(parentCode,false);
                }
                return Result.success("操作成功");
            }
            return Result.failure("操作失败");
        } catch (Exception e) {
            LOGGER.error("更新库区信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：库区更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除货架管理", notes = "生成代码")
    @DeleteMapping("/delete/{shelfId}")
    public Result delete(@PathVariable String shelfId) {

        try {
            ShelfManagement shelfIsExist = shelfManagementService.getShelfByShelfId(shelfId);
            if (ObjectUtil.isEmpty(shelfIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此货架编码");
            }

            if (shelfManagementService.isStopUsing(shelfId)!=0){
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "货架已停用,无法删除！");
            }

            //检查此货架下是否有货位存在
            List<CargoSpaceManagement> cargoSpaceManagementsList = cargoSpaceManagementService.getCargoSpaceListByShelfId(shelfId);
            if (ObjectUtil.isNotEmpty(cargoSpaceManagementsList)){
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR,"此货架仍有货位存在,请先删除货位");
            }

            QueryWrapper<ShelfManagement> wrapper = new QueryWrapper<>();
            wrapper.eq("shelf_id", shelfId);
            int i = shelfManagementMapper.delete(wrapper);
            LOGGER.info("货架: " + shelfId + " 删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除货架信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：货架删除失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取货架详细信息")
    @GetMapping("/getShelfByShelfId/{shelfId}")
    public Result getShelf(@PathVariable String shelfId) {
        try {
            ShelfManagement shelfManagement = shelfManagementService.getShelfByShelfId(shelfId);
            return Result.success(shelfManagement);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取货架信息失败");
        }
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "获取对应货架的所有货位")
    @GetMapping("/getCargoSpaceByShelfId/{shelfId}")
    public Result getAllCargoSpace(@PathVariable String shelfId) {
        try {
            List<CargoSpaceManagement> cargoSpaceManagementList = cargoSpaceManagementService.getCargoSpaceListByShelfId(shelfId);
            return Result.success(cargoSpaceManagementList);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取货位信息失败");
        }
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "自动新增所有货位")
    @GetMapping("/addAuto/{shelfId}")
    public Result addAuto(@PathVariable String shelfId){

        /**
         * 如果货架里已有货位不能再使用自动生成
         */
        List<CargoSpaceManagement> cargoSpaceManagements = cargoSpaceManagementService.getCargoSpaceListByShelfId(shelfId);
        if (ObjectUtil.isNotEmpty(cargoSpaceManagements)){
            return Result.failure(ErrorCode.DATA_EXISTS_ERROR,"此货架有货位，无法自动生成货位");
        }
        ShelfManagement shelfManagement = shelfManagementService.getShelfByShelfId(shelfId);
        AddCargoSpacedDTO addCargoSpacedDTO = new AddCargoSpacedDTO();
        addCargoSpacedDTO.setShelfId(shelfId);
        if (ObjectUtil.isEmpty(shelfManagement)){
            return Result.failure(ErrorCode.DATA_IS_NULL,"货架不存在");
        }
        //自动创建货位
        int i = 0;
        try{
            i = cargoSpaceManagementService.addCargoSpace(addCargoSpacedDTO);
        }catch (Exception e){
            LOGGER.error("自动生成货位失败--异常",e);
        }
        return render(i>0);
    }

}

