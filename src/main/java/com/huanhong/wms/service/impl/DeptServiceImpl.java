package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.wms.SuperEntity;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.RedisKey;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.service.IDeptService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-26
 */
@Service
public class DeptServiceImpl extends SuperServiceImpl<DeptMapper, Dept> implements IDeptService {

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public Result<List<Dept>> getDeptTree(QueryWrapper<Dept> query) {
        Result<List<Dept>> result = new Result<>();
        query.select("id, `name`, is_hide, is_limit, level, parent_id, parent_code, user_count");
        List<Dept> depts = this.baseMapper.selectList(query);
        result.setData(new Dept().builTree(depts, false));
        result.setOk(true);
        return result;
    }

    @Override
    public Result<Integer> deleteDept(LoginUser loginUser, Integer deptId) {
        Dept dept = this.baseMapper.selectOne(Wrappers.<Dept>lambdaQuery().eq(SuperEntity::getId, deptId));
        if (dept == null) {
            return Result.noDataError();
        }
        if (dept.getParentId() == 0 && dept.getIsCompany() == 1) {
            return Result.failure("根数据不可删除");
        }
        this.baseMapper.deleteById(deptId);
        redisTemplate.delete(RedisKey.DEPT_TREE);
        return Result.success();
    }

    @Override
    public void updateDeptUserCount() {
        QueryWrapper<Dept> query = new QueryWrapper<>();
        query.select("id, `name`, is_hide, is_limit, level, parent_id, (select count(*) from user where del = 0 and dept_id = dept.id) as userCount");
        List<Dept> depts = this.baseMapper.selectList(query);
        final List<Dept> tree = new Dept().builTree(depts, true);
        this.recursionUpdate(tree);
        redisTemplate.delete(RedisKey.DEPT_TREE);
    }

    /**
     * 递归更新
     *
     * @param list
     */
    private void recursionUpdate(List<Dept> list) {
        list.forEach(dept -> {
            this.baseMapper.updateDeptUserCount(dept.getId(), dept.getUserCount());
            if (dept.getChildren() != null) {
                this.recursionUpdate(dept.getChildren());
            }
        });
    }
}
