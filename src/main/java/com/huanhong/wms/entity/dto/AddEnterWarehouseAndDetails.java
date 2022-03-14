package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description = "新增入库单和明细单")
public class AddEnterWarehouseAndDetails {

    @Valid
    @ApiModelProperty(value = "入库单主表")
    private AddEnterWarehouseDTO addEnterWarehouseDTO;

    @Valid
    @ApiModelProperty(value = "入库单明细")
    private List<AddEnterWarehouseDetailsDTO> addEnterWarehouseDetailsDTOList;
}
