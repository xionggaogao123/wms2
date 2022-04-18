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
@ApiModel(value="入库明细表分页查询参数", description="入库明细表分页查询参数")
@Data
public class WarehousingDetailPage extends Page<WarehousingRecord> {


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


    @ApiModelProperty(value = "入库开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inDateStart;

    @ApiModelProperty(value = "入库结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inDateEnd;

    @ApiModelProperty(value = "库管员（提交入库申请表单的用户）")
    private String warehouseManager;
}
