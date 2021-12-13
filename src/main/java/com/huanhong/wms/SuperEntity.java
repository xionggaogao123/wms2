package com.huanhong.wms;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘德宜 wudihaike@vip.qq.com
 * @date 2018/1/4 12:49
 */
@Data
public class SuperEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "自增长主键")
    private Integer id;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;
    /**
     * 是否删除 0=未删除 1=删除 OR false=未删除 true=删除
     */
    @TableLogic
    @TableField(select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "是否删除")
    private Integer del;

}
