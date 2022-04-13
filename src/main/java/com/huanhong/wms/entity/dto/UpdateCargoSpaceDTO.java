package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(description = "更新货位")
public class UpdateCargoSpaceDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;


    @Min(0)
    @Max(1)
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "0-空闲 1-未满 2-已满")
    private Integer full;


//    @ApiModelProperty(value = "货位承重(地堆无限大)")
//    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
//    private String cargoSpaceBearing;


    @ApiModelProperty(value = "备注")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String remark;

}
