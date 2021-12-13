package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "部门表")
public class Dept extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门代码")
    private Integer deptCode;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "0.不隐藏本部门 1.隐藏本部门")
    private Integer isHide;

    @ApiModelProperty(value = "0.无限制 1.不能看到所有通讯录，仅可见自己 2.只能看到所在部门及下级部门通讯录")
    private Integer isLimit;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "父Id")
    private Integer parentId;

    @ApiModelProperty(value = "父Code")
    private Integer parentCode;

    @ApiModelProperty(value = "顺序 从小到大")
    private Integer sort;

    @ApiModelProperty(value = "是否为公司")
    private Integer isCompany;

    @ApiModelProperty(value = "部门自定义字段")
    private String ext;

    @ApiModelProperty(value = "用户数")
    private Integer userCount;

    /**
     * 子节点
     */
    @TableField(exist = false)
    private List<Dept> children;


    public Dept() {
    }

    // 建立树形结构
    public List<Dept> builTree(List<Dept> list, boolean countUserCount) {
        List<Dept> treeDepts = new ArrayList<>();
        for (Dept dept : getRootNode(list)) {
            dept = buildChilTree(dept, list, countUserCount);
            treeDepts.add(dept);
        }
        return treeDepts;
    }

    // 递归，建立子树形结构
    private Dept buildChilTree(Dept node, List<Dept> list, boolean countUserCount) {
        List<Dept> chilDepts = new ArrayList<>();
        for (Dept dept : list) {
            if (dept.getParentId().equals(node.getId())) {
                chilDepts.add(buildChilTree(dept, list, countUserCount));
            }
        }
        // 子部门人数累计
        if (countUserCount) {
            node.setUserCount(node.getUserCount() + chilDepts.stream().mapToInt(Dept::getUserCount).sum());
        }
        if (chilDepts.size() > 0) {
            node.setChildren(chilDepts);
        }
        return node;
    }

    // 获取根节点
    private List<Dept> getRootNode(List<Dept> list) {
        List<Dept> rootDeptLists = new ArrayList<>();
        for (Dept dept : list) {
            if (dept.getParentId() == 0) {
                rootDeptLists.add(dept);
            }
        }
        return rootDeptLists;
    }
}
