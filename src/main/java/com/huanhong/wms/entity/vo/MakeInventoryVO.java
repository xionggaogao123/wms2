package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "MakeInventoryVO查询对象", description = "盘点单对象封装")
public class MakeInventoryVO {


    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废")
    private Integer planStatus;

    @ApiModelProperty(value = "物料类型: 0-全部物料、1-指定物料、2-随机物料")
    private Integer materialType;

    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存 3-全部 ")
    private Integer inventoryType;

    @ApiModelProperty(value = "货主： 0-泰丰盛和  1-矿上自有 2-全部")
    private Integer consignor;

    @ApiModelProperty(value = "是否全盘: 0-非全盘 1-全盘")
    private Integer allMake;

    @ApiModelProperty(value = "状态: 0-待盘点，1-已盘点")
    private Integer checkStatus;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime CreateDateStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime CreateDateEnd;
}
