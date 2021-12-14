package com.huanhong.wms;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 业务实体父类
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @date 2018/1/4 12:49
 */
@Data
public class SuperBsEntity extends SuperEntity {

    @ApiModelProperty(value = "父公司ID")
    private Integer parentCompanyId;

    @ApiModelProperty(value = "所属公司ID")
    private Integer companyId;

}
