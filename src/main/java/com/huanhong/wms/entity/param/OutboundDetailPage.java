package com.huanhong.wms.entity.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.OutboundRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value="领料出库明细表分页查询参数", description="领料出库明细表分页查询参数")
@Data
public class OutboundDetailPage extends Page<OutboundRecord> {


    private Integer userId;
    private String userName;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "生产厂家")
    private String supplier;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "物料分类")
    private String typeCode;


    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date gmtStart;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date gmtEnd;
    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;
    @ApiModelProperty(value = "领用人")
    private String recipient;
    @ApiModelProperty(value = "费用项目")
    private String expenseItem;
    @ApiModelProperty(value = "费用承担单位")
    private String costBearingUnit;
    @ApiModelProperty(value = "物资用途")
    private String materialUse;
    @ApiModelProperty(value = "领用用途")
    private String requisitionUse;
    @ApiModelProperty(value = "库管员")
    private String librarian;
}
