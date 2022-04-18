package com.huanhong.wms.entity.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.InventoryInformation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value="库存账分页查询参数", description="库存账分页查询参数")
@Data
public class InventoryInfoPage extends Page<InventoryInformation> {


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

    @ApiModelProperty(value = "库房名称")
    private String warehouseName;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "物料分类")
    private String typeCode;

    @ApiModelProperty(value = "库龄开始 单位天")
    private Integer inDayStart;
    @ApiModelProperty(value = "库龄结束 单位天")
    private Integer inDayEnd;


    @ApiModelProperty(value = "失效开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDateStart;

    @ApiModelProperty(value = "失效结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDateEnd;

    @ApiModelProperty(value = "入库开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inDateStart;

    @ApiModelProperty(value = "入库结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inDateEnd;


    @ApiModelProperty(value = "入库后≥6个月")
    private Integer inMonth;
}
