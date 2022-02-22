package com.huanhong.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Dept;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-26
 */
public interface IDeptService extends SuperService<Dept> {

    /**
     * 获取门店下部门树列表
     *
     * @param ew 查询条件
     * @return Result<List < Dept>>
     */
    Result<List<Dept>> getDeptTree(QueryWrapper<Dept> query);

    @Async
    void updateDeptUserCount();

    Result<Integer> deleteDept(LoginUser loginUser, Integer deptId);

    boolean isStopUsing(int deptId);

    Dept getDeptById(int deptId);

}
