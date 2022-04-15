package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description="物料价格")
public class MaterialPriceVO {

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "时间")
    private Date createTime;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "不含税单价")
    private BigDecimal unitPriceWithoutTax;

    @ApiModelProperty(value = "含税单价")
    private BigDecimal unitPriceIncludingTax;
}
