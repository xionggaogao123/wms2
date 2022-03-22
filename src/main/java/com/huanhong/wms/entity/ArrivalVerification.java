package com.huanhong.wms.entity;

import java.time.LocalDateTime;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="到货检验主表")
public class ArrivalVerification extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @ApiModelProperty(value = "到货检验单编号")
    private String verificationDocumentNumber;

    @ApiModelProperty(value = "采购合同编号")
    private String contractNumber;

    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;

    @ApiModelProperty(value = "计划类别-正常、加急、补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "附件-图片文件地址或图片MD5")
    private String appendix;

    @ApiModelProperty(value = "状态: 草拟 审批中 审批生效 作废")
    private String planStatus;

    @ApiModelProperty(value = "到货日期")
    private LocalDateTime deliveryDate;

    @ApiModelProperty(value = "理货人")
    private String inspector;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "车号")
    private String carNumber;

    @ApiModelProperty(value = "仓库")
    private String warehouse;


}
