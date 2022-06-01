package com.huanhong.wms.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.entity.MakeInventoryReportDetails;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author wang
 * @date 2022/6/1 12:42
 */

@Data
public class UpdateMakeInventoryReportOneRequest {

    private Integer id;

    @ApiModelProperty(value = "盘点数量快照")
    private Double checkSnapShoot;

    @ApiModelProperty(value = "稽核数量快照")
    private Double auditSnapShoot;

    @ApiModelProperty(value = "盘点数量快照时间")
    private LocalDateTime checkSnapShootTime;

    @ApiModelProperty(value = "稽核数量快照时间")
    private LocalDateTime auditSnapShootTime;

    @ApiModelProperty(value = "盘点报告编号")
    private String reportNumber;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "库存类型：0-正式库存 1-暂存库存 2-临时库存")
    private Integer inventoryType;

    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "稽核数量")
    private Double auditCredit;

    @ApiModelProperty(value = "盈亏数量")
    private Double finalCredit;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "盘点状态: 0-待盘点，1-一致 ，2-盘盈 ，3-盘亏")
    private Integer checkStatusDetails;

    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "盈亏金额")
    private BigDecimal finalAmounts;

    @ApiModelProperty(value = "差异原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;

    @ApiModelProperty(value = "计划创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;

}
