package com.huanhong.wms.entity.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.AllocationPlan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value="调拨明细汇总表分页查询参数", description="调拨明细汇总表分页查询参数")
@Data
public class AllocationDetailPage extends Page<AllocationPlan> {


    private Integer userId;
    private String userName;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "生产厂家")
    private String supplier2;

    @ApiModelProperty(value = "供应商")
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

    @ApiModelProperty(value = "调拨申请单据编号")
    private String allocationNumber;
    @ApiModelProperty(value = "调出负责人")
    private String sendUser;
    @ApiModelProperty(value = "调入负责人")
    private String receiveUser;

    @ApiModelProperty(value = "调出单位")
    private String sendCompany;

    @ApiModelProperty(value = "调入单位")
    private String receiveCompany;

}
