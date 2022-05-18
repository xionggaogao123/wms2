package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="供应商")
public class Supplier extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "供应商名")
    private String name;


}
