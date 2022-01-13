package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ApiModel(value = "UpdateShelfVO更新对象", description = "货架更新对象封装")
@Data
public class UpdateShelfDTO {

    private static final long serialVersionUID = 1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;

    @ApiModelProperty(value = "备注")
    private String remark;
}
