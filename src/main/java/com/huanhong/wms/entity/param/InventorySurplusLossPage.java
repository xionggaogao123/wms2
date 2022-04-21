package com.huanhong.wms.entity.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.WarehousingRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value="盘点盈亏表分页查询参数", description="盘点盈亏表分页查询参数")
@Data
public class InventorySurplusLossPage extends Page<WarehousingRecord> {


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

    @ApiModelProperty(value = "盘点人")
    private String checker;



}
