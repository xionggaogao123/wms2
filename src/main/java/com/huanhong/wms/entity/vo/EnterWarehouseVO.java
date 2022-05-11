package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "EnterWarehouseVO查询对象", description = "入库单据查询对象封装")
public class EnterWarehouseVO {

    @ApiModelProperty(value = "采购入库单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "入库类型-1. 暂估入库（默认）2.正式入库")
    private Integer storageType;

    @ApiModelProperty(value = "采购合同编号")
    private String contractNumber;

    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;

    @ApiModelProperty(value = "状态:1.草拟2.审批中3.审批生效4.作废 5.驳回")
    private Integer state;

    @ApiModelProperty(value = "到货检验单编号")
    private String verificationDocumentNumber;

    @ApiModelProperty(value = "计划类别-1.正常、2.加急、3.补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "发票号")
    private String receiptNumber;

    @ApiModelProperty(value = "到货日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryDateStart;

    @ApiModelProperty(value = "到货日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryDateEnd;

    @ApiModelProperty(value = "入库日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enterDateStart;

    @ApiModelProperty(value = "入库日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enterDateEnd;

    @ApiModelProperty(value = "经办人")
    private String manager;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "仓库")
    private String warehouse;

}
