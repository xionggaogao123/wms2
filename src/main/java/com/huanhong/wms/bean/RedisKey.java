package com.huanhong.wms.bean;

/**
 * redis公共key约定
 */
public interface RedisKey {

    /**
     * 临时数据
     */
    String TEMP = "temp:";

    /**
     * 部门树
     */
    String DEPT_TREE = "dept:tree";
}
