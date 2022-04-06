package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="设备表")
public class Device extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "设备系统->大分类")
    private String deviceSystem;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "设备品牌")
    private String deviceBrand;

    @ApiModelProperty(value = "设备名")
    private String deviceName;

    @ApiModelProperty(value = "设备号")
    private String deviceNo;

    @ApiModelProperty(value = "设备图标")
    private String deviceIcon;

    @ApiModelProperty(value = "几栋")
    private String building;

    @ApiModelProperty(value = "几楼")
    private String floor;

    @ApiModelProperty(value = "几室")
    private String room;

    @ApiModelProperty(value = "具体点位")
    private String address;

    @ApiModelProperty(value = "是否在线 1.是 0.否")
    private Boolean isOnline;

    @ApiModelProperty(value = "是否维修 1.是 0.否")
    private Boolean isRepair;

    @ApiModelProperty(value = "是否报警 1.是 0.否")
    private Boolean isAlarm;

    @ApiModelProperty(value = "是否能源设备 1.是 0.否")
    private Boolean isEnergy;

    @ApiModelProperty(value = "设备属性")
    private String attribute;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "序号")
    private Integer sort;


    @ApiModelProperty(value = "其他平台唯一标识")
    private String otherId;
}
