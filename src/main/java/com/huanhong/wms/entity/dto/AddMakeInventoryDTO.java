package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel("新增盘点单DTO")
public class AddMakeInventoryDTO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废")
    private Integer planStatus;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "物料类型: 0-全部物料、1-指定物料、2-随机物料")
    private Integer materialType;

    @Min(0)
    @Max(3)
    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存 3-全部 ")
    private Integer inventoryType;


    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "货主: 0-泰丰盛和  1-矿上自有 2-全部")
    private Integer consignor;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否全盘: 0-非全盘 1-全盘")
    private Integer allMake;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态: 0-待盘点,1-已盘点")
    private Integer checkStatus;

    @ApiModelProperty(value = "盘点人")
    private String checkerIds;

    @ApiModelProperty(value = "稽核人Id")
    private Integer auditId;

    @ApiModelProperty(value = "稽核人姓名")
    private String auditName;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "计划类别-1-正常、2-加急、3-补计划、请选择（默认）")
    private Integer planClassification;

}
