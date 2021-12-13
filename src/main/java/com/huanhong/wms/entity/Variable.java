package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户变量表
 * </p>
 *
 * @author liudeyi
 * @since 2020-01-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "商户变量表")
public class Variable extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @TableField(value = "`key`")
    @ApiModelProperty(value = "键 ")
    private String key;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "扩产信息")
    private String extra;

    @ApiModelProperty(value = "父ID")
    private Integer parentId;

    @ApiModelProperty(value = "父名称")
    private String parentValue;

    @ApiModelProperty(value = "顺序")
    private Integer orders;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "用户ID(个人变量)")
    private Integer userId;

    @ApiModelProperty(value = "创建用户ID")
    private Integer createdBy;

    @ApiModelProperty(value = "更新用户ID")
    private Integer updateBy;


}
