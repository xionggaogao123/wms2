package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "名称金额")
public class NameAmountVo {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "金额（单位：元）")
    private BigDecimal value;

}
