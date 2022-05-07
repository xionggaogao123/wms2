package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "临库分页查询对象")
public class TemporaryLibraryInventoryVO {


    @ApiModelProperty(value = "清点单编号")
    private String documentNumber;

    @ApiModelProperty(value = "送货单编号")
    private String deliveryNoteNumber;

    @ApiModelProperty(value = "申请单编号")
    private String rfqNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

}
